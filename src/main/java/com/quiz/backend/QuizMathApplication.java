package com.quiz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication()

public class QuizMathApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizMathApplication.class, args);
    }

}
