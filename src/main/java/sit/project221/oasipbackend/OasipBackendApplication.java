package sit.project221.oasipbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.EventListener;
import sit.project221.oasipbackend.services.EmailSenderService;


@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class OasipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OasipBackendApplication.class, args);

    }


}
