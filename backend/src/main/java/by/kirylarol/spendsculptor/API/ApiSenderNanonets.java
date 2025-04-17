package by.kirylarol.spendsculptor.API;


import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource("classpath:NanonetsApi.properties")
public class ApiSenderNanonets implements ApiSender {

    private RestTemplate restTemplate = new RestTemplate();
    @Value("${model}")
    private String modelId;
    @Value("${key}")
    private String key;
    @Value("${type}")
    private String type;
    @Value("${url}")
    private String url;

    @Override
    public String send(MultipartFile multipartFile) {
        try {
            File convertedFile = File.createTempFile("temp", null);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, convertedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return send(convertedFile);

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    @Override
    public String send(File image) {
        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
        url = url.replace("{{model_id}}", modelId);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .build();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", image.getName(), RequestBody.create(MEDIA_TYPE_JPG, image))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", Credentials.basic(key, ""))
                .build();


        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

