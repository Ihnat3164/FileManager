package org.mainservice.sevice;

import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.UserRegistrationDTO;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> addUser(UserRegistrationDTO userRegistrationDTO){

        if(userIsExist(userRegistrationDTO.getEmail())){
            return new ResponseEntity<>("User is exist", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setRole("USER");

        String encodedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return new ResponseEntity<>("User created",HttpStatus.CREATED);
    }

    public boolean userIsExist(String email){
        return userRepository.findUserByEmail(email).isPresent();
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User editUserById(Long id, User incompleteUser)  throws IllegalAccessException {
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

        return userRepository.save(existingUser);
    }

    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

}