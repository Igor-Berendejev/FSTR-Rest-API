package com.example.fstr.controller;

import com.example.fstr.exceptions.BadRequestException;
import com.example.fstr.model.Image;
import com.example.fstr.model.Pass;
import com.example.fstr.repository.ImageRepository;
import com.example.fstr.repository.PassRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/submitData")
public class PassController {
    @Autowired
    PassRepository passRepository;
    @Autowired
    ImageRepository imageRepository;

    @PostMapping("/pereval_added")
    public Pass submitData(@RequestBody String pass) throws BadRequestException, IOException {
        String passData = getRawDataFromJSON(pass);
        if (!passContainsCoords(passData)) throw new BadRequestException("Pass coordinates are mandatory");
        if (!passContainsUserDetails(passData)) throw new BadRequestException("User details are mandatory. Check name, surname, email");
        String imagesData = saveImages(new JSONObject(pass).getJSONArray("images"));
        return passRepository.save(new Pass(LocalDateTime.now(), passData, imagesData, "new"));
    }

    @PostMapping("/pereval_images")
    public Image addImage(byte[] imageBytes) {
        return imageRepository.save(new Image(LocalDateTime.now(), imageBytes));
    }

    @GetMapping("/pereval_added/{id}")
    public Pass getPassById(@PathVariable(value = "id") Integer passId) {
        return passRepository.getById(passId);
    }

    @GetMapping("/pereval_added/{id}/status")
    public String getStatus(@PathVariable(value = "id") Integer passId) {
        return passRepository.getStatusById(passId);
    }

    @GetMapping("/pereval_added")
    public List<Pass> getAll(){
        return passRepository.findAll();
    }

    @PutMapping("/pereval_added/{id}")
    public Pass updatePass(@PathVariable("id") int id, @RequestBody String updatedPass) throws BadRequestException, IOException {
        Pass databasePass = passRepository.getById(id);
        if (!userDetailsMatch(databasePass.getRaw_data(), updatedPass))
            throw new BadRequestException("Cannot change user details");
        if (!databasePass.getStatus().equals("new"))
            throw new BadRequestException("Cannot update, record has been processed already");
        if (!passContainsCoords(updatedPass)) throw new BadRequestException("Pass coordinates are mandatory");
        databasePass.setRaw_data(getRawDataFromJSON(updatedPass));
        deletePassImages(databasePass.getImages());
        String imagesData = saveImages(new JSONObject(updatedPass).getJSONArray("images"));
        databasePass.setImages(imagesData);
        return passRepository.save(databasePass);
    }

    @DeleteMapping("/pereval_images/{id}")
    public void deleteImage(@PathVariable("id") int id) {
        imageRepository.deleteById(id);
    }


    private boolean passContainsCoords(String passJsonString) {
        JSONObject passJson = new JSONObject(passJsonString);
        return (!passJson.getJSONObject("coords").get("latitude").equals("") &&
                !passJson.getJSONObject("coords").get("longitude").equals("") &&
                !passJson.getJSONObject("coords").get("height").equals(""));
    }

    private boolean passContainsUserDetails(String passJsonString){
        Pattern emailPattern = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        JSONObject passJson = new JSONObject(passJsonString);
        return (!passJson.getJSONObject("user").getString("name").equals("") &&
                !passJson.getJSONObject("user").getString("surname").equals("") &&
                emailPattern.matcher(passJson.getJSONObject("user").getString("email")).matches());
    }

    private byte[] getImageBytes(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new BufferedInputStream(url.openStream());
            byte[] imageBytes = new byte[4096];
            int length;
            while ((length = is.read(imageBytes)) != -1) {
                baos.write(imageBytes, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    private String saveImages(JSONArray imagesJsonArray) throws IOException {
        String imagesJson = "";
        for (int i = 0; i < imagesJsonArray.toList().size(); i++) {
            JSONObject jsonObject = imagesJsonArray.getJSONObject(i);
            if (!jsonObject.getString("url").equals("")) {
                Image image = addImage(getImageBytes(new URL(jsonObject.getString("url"))));
                imagesJson = imagesJson + "\"" + jsonObject.getString("title") + "\"" + ":" + image.getId();
                if (i < imagesJsonArray.toList().size() - 1) imagesJson = imagesJson + ",";
            }
        }
        return "{" + imagesJson + "}";
    }

    private boolean userDetailsMatch(String passJsonString, String updatedPassJsonString) {
        JSONObject passJson = new JSONObject(passJsonString);
        JSONObject updatedPassJson = new JSONObject(updatedPassJsonString);
        return (passJson.getJSONObject("user").get("name").equals(updatedPassJson.getJSONObject("user").get("name")) &&
                passJson.getJSONObject("user").get("surname").equals(updatedPassJson.getJSONObject("user").get("surname")) &&
                passJson.getJSONObject("user").get("otc").equals(updatedPassJson.getJSONObject("user").get("otc")) &&
                passJson.getJSONObject("user").get("email").equals(updatedPassJson.getJSONObject("user").get("email")) &&
                passJson.getJSONObject("user").get("phone").equals(updatedPassJson.getJSONObject("user").get("phone")));
    }

    private String getRawDataFromJSON(String passJsonString) {
        int indexOfImagesData = passJsonString.indexOf("\"images\":");
        StringBuilder builder = new StringBuilder(passJsonString.substring(0, indexOfImagesData - 1).trim());
        return builder.replace(builder.length() - 1, builder.length() - 1, "}").toString();
    }

    private void deletePassImages(String passImagesJsonString) {
        JSONObject imagesJson = new JSONObject(passImagesJsonString);
        Iterator<String> keys = imagesJson.keys();
        while (keys.hasNext()) {
            int imageID = imagesJson.getInt(keys.next());
            deleteImage(imageID);
        }
    }
}
