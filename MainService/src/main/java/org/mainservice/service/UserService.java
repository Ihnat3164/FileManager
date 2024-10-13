package org.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.JwtRequest;
import org.mainservice.DTO.JwtResponse;
import org.mainservice.DTO.UserRegistrationDTO;
import org.mainservice.component.JWTUtils;
import org.mainservice.exception.ObjectNotFoundException;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> registerUser(UserRegistrationDTO userRegistrationDTO){

        if(userIsExist(userRegistrationDTO.getEmail())){
            return new ResponseEntity<>("User is exist. Please login", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole("USER");


        String encodedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());
        user.setPassword(encodedPassword);

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<?> createUser(User user){
        if(userIsExist(user.getEmail())){
            return new ResponseEntity<>("User is exist. Please login", HttpStatus.BAD_REQUEST);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);;
        return ResponseEntity.ok(userRepository.save(user));
    }

    private boolean userIsExist(String email){
        return userRepository.findUserByEmail(email).isPresent();
    }
    private boolean userIsExist(Long id){return userRepository.findUserById(id).isPresent();}

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public ResponseEntity<?> editUserById(Long id, User incompleteUser)  throws IllegalAccessException {
        if(!userIsExist(id)){
            throw new ObjectNotFoundException("User not found");
        }

        User existingUser = userRepository.findById(id).get();

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
        return ResponseEntity.ok(userRepository.save(existingUser));
    }

    public ResponseEntity<?> deleteUserById(Long id){
        if(!userIsExist(id)){
            throw new ObjectNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        }
        catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage() + " or user isn't exist");
        }
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

}