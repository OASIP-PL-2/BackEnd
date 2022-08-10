package sit.project221.oasipbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;



@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class OasipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OasipBackendApplication.class, args);

    }


}
