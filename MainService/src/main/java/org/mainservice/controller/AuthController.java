package org.mainservice.controller;

import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.JwtRequest;
import org.mainservice.DTO.JwtResponse;
import org.mainservice.component.JWTUtils;
import org.mainservice.sevice.MyUserDetailsService;
import org.mainservice.sevice.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;

    @PostMapping("/api/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
         try {
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
         }
         catch (BadCredentialsException ex){
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
         }
         UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getEmail());
         String token = jwtUtils.generateToken(userDetails);
         return ResponseEntity.ok(new JwtResponse(token));
     }
}
