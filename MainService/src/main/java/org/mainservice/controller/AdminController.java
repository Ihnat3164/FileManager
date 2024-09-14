package org.mainservice.controller;

import org.mainservice.model.User;
import org.mainservice.sevice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @GetMapping
    public List<User> outputAllUsers(){
        return userService.getAllUsers();
    }

    @PatchMapping
    public ResponseEntity<Void> modifyUser(@RequestParam(value= "id" ,required = false) Long id, @RequestBody User user) throws IllegalAccessException {
         userService.editUserById(id, user);
         return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> eraseUser(@RequestParam(value = "id" ,required = false) Long id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
