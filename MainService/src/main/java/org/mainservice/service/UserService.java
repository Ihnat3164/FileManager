package org.mainservice.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.JwtRequest;
import org.mainservice.DTO.JwtResponse;
import org.mainservice.DTO.UserRegistrationDTO;
import org.mainservice.component.JWTUtils;
import org.mainservice.exception.AuthenticationFailedException;
import org.mainservice.exception.ObjectNotFoundException;
import org.mainservice.exception.UserAlreadyExistsException;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public UserRegistrationDTO registerUser(UserRegistrationDTO userRegistrationDTO){

        Optional.ofNullable(userRepository.findUserByEmail(userRegistrationDTO.getEmail()))
                .ifPresent(user -> { throw new UserAlreadyExistsException("User with this email is exist: " + userRegistrationDTO.getEmail()); });

        User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole("USER");

        String encodedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return userRegistrationDTO;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User createUser(User user){
        Optional.ofNullable(userRepository.findUserByEmail(user.getEmail()))
                .ifPresent(userOptional -> { throw new UserAlreadyExistsException("User with this email is exist: " + user.getEmail()); });

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User editUserById(Long id, User incompleteUser)  throws IllegalAccessException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User not found with id: " + id));

        Class<?> internClass= User.class;
        Field[] internFields=internClass.getDeclaredFields();

        for(Field field : internFields){

            field.setAccessible(true);
            Object value=field.get(incompleteUser);
            if(value!=null){
                field.set(existingUser,value);
            }
            field.setAccessible(false);
        }
        return  userRepository.save(existingUser);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteUserById(Long id){
        userRepository.findUserById(id).orElseThrow(() -> new ObjectNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    public JwtResponse createAuthToken(JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        }
        catch (BadCredentialsException ex){
            throw new AuthenticationFailedException("User with this email isn't exist:" + authRequest.getEmail());
        }
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        return new JwtResponse(token);
    }

}