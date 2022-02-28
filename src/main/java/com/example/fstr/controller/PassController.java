package com.example.fstr.controller;

import com.example.fstr.exceptions.BadRequestException;
import com.example.fstr.exceptions.OperationExecutionException;
import com.example.fstr.model.Image;
import com.example.fstr.model.Pass;
import com.example.fstr.repository.ImageRepository;
import com.example.fstr.repository.PassRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class PassController {
    @Autowired
    PassRepository passRepository;
    @Autowired
    ImageRepository imageRepository;

    @PostMapping("/pereval_added")
    public Pass submitData(@RequestBody String pass) throws BadRequestException, OperationExecutionException, IOException {
        int indexOfImagesData = pass.indexOf("\"images\":");
        StringBuilder builder = new StringBuilder(pass.substring(0, indexOfImagesData - 1).trim());
        String passData = builder.replace(builder.length() - 1, builder.length() - 1, "}").toString();
        String imagesData = "{" + pass.substring(indexOfImagesData);
        saveImages(new JSONObject(pass).getJSONArray("images"));
        if (!passContainsCoords(passData)) throw new BadRequestException("Pass coordinates are mandatory");
        return passRepository.save(new Pass(LocalDateTime.now(), passData, imagesData, "new"));
    }

    private boolean passContainsCoords(String passJsonString) {
        JSONObject passJson = new JSONObject(passJsonString);
        return (!passJson.getJSONObject("coords").get("latitude").equals("") &&
                !passJson.getJSONObject("coords").get("longitude").equals("") &&
                !passJson.getJSONObject("coords").get("height").equals(""));
    }

    @PostMapping("/pereval_images")
    public Image addImage(byte[] imageBytes){
        return imageRepository.save(new Image(LocalDateTime.now(), imageBytes));
    }

    private byte[] getImageBytes(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try{
            is = new BufferedInputStream(url.openStream());
            byte[] imageBytes = new byte[4096];
            int length;
            while((length = is.read(imageBytes)) != -1){
                baos.write(imageBytes, 0, length);
            }
            return  baos.toByteArray();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally{
            if (is != null){is.close();}
        }
        return null;
    }

    private void saveImages(JSONArray imagesJsonArray) throws IOException {
        for (int i = 0; i < imagesJsonArray.toList().size(); i++){
            JSONObject jsonObject = imagesJsonArray.getJSONObject(i);
            addImage(getImageBytes(new URL(jsonObject.getString("url"))));
        }
    }
}
