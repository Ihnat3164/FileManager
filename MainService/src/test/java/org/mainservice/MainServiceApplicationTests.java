package org.mainservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MainServiceApplicationTests {

    @Test
    void contextLoads() {

        String one = "Hello";
        String two = " world";
        assertEquals("Hello world", one + two);
    }

}
