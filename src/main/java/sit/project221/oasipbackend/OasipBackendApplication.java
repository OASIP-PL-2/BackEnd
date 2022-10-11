package sit.project221.oasipbackend;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.io.File;
import java.io.IOException;


@SpringBootApplication
@ImportResource("classpath:app-config.xml")
public class OasipBackendApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(OasipBackendApplication.class, args);
//        String root = "upload-dir/files";
//        String folderName = "1";
//        File folder = new File(root + "\\" + folderName);
//        if (!folder.exists()) {
//            if (folder.mkdir()) {
//                System.out.println(root + "\\" + folderName);
//            } else {
//                System.out.println("Failed to create directory!");
//            }
//        }else {
//            FileUtils.cleanDirectory(folder);
//            System.out.println(root + "\\" + folderName);
//        }


    }


}
