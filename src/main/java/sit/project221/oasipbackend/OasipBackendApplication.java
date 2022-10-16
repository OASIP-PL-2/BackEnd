package sit.project221.oasipbackend;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class OasipBackendApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(OasipBackendApplication.class, args);

    }


}
