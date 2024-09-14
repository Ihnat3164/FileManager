package org.mainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}

//////Решить проблему с admin(скрипт при запуске и добавление) на потом


//////API админа(вывод есть, удаление есть) + модификация пользователя(корректная обработка patch и обновление в бд(query))
