package org.mainservice.component;

import lombok.RequiredArgsConstructor;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            String encodedPassword = passwordEncoder.encode("admin");
            admin.setPassword(encodedPassword);
            userRepository.save(admin);

        }
    }
}
