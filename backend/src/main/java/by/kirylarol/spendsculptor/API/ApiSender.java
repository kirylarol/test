package by.kirylarol.spendsculptor.API;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ApiSender {
    String send (File image);
    String send (MultipartFile multipartFile);

}
