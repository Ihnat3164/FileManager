package org.mainservice.sevice;

import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void addUser(User user){
        user.setRole("USER");
        userRepository.save(user);
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

}