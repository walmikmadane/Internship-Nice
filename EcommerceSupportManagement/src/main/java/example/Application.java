package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"rest","service","domain","repository"})
public class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
