package org.mainservice.component;

import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");

            String encodedPassword = passwordEncoder.encode("admin");
            admin.setPassword(encodedPassword);

            userRepository.save(admin);
            System.out.println("Admin user created.");
        } else {
            System.out.println("Admin user already exists or other users exist.");
        }
    }
}
