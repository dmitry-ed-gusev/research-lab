package pl.finsys.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pl.finsys.example"})
public class BookstoreApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
