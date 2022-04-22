package com.example.fstr.controller;

import com.example.fstr.exceptions.BadRequestException;
import com.example.fstr.model.Image;
import com.example.fstr.model.Pass;
import com.example.fstr.repository.ImageRepository;
import com.example.fstr.repository.PassRepository;
import com.example.fstr.util.JsonParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    /**
     * Checks if data received from the client contains all mandatory details,
     * parses data, saves a pass data to the database table mount_pass_added
     *
     * @param pass - a String representation of json received from the client
     * @return Pass - a record in database table mount_pass_added
     * @throws BadRequestException
     * @throws IOException
     */
    @PostMapping("/mount_pass_added")
    public Pass submitData(@RequestBody String pass) throws BadRequestException, IOException {
        String passData = JsonParser.getRawData(pass);
        if (!passContainsCoords(passData)) throw new BadRequestException("Pass coordinates are mandatory");
        if (!passContainsUserDetails(passData))
            throw new BadRequestException("User details are mandatory. Check name, surname, email");

        //String imagesData = getPassImages(new JSONObject(pass).getJSONArray("images"));
        String imagesData = getPassImages(pass);
        return passRepository.save(new Pass(LocalDateTime.now(), passData, imagesData, "new"));
    }

    /**
     * Saves an image to the database table mount_pass_images
     *
     * @param imageBytes - represents a byte array of the image
     * @return Image - a record in database table mount_pass_images
     */
    @PostMapping("/mount_pass_images")
    public Image addImage(byte[] imageBytes) {
        return imageRepository.save(new Image(LocalDateTime.now(), imageBytes));
    }

    /**
     * @param passId - an id of the record in the database table mount_pass_added
     * @return - Pass a record in database table mount_pass_added
     */
    @GetMapping("/mount_pass_added/{id}")
    public Pass getPassById(@PathVariable(value = "id") Integer passId) {
        return passRepository.getById(passId);
    }

    /**
     * @param passId - an id of the record in the database table mount_pass_added
     * @return status of the record in the database
     */
    @GetMapping("/mount_pass_added/{id}/status")
    public String getStatus(@PathVariable(value = "id") Integer passId) {
        return passRepository.getStatusById(passId);
    }

    /**
     * @return all records in the database table mount_pass_added
     */
    @GetMapping("/mount_pass_added")
    public List<Pass> getAll() {
        return passRepository.findAll();
    }

    /**
     * Checks if data received from the client contains all mandatory details,
     * checks if the record client wants to update is in status "new",
     * parses data received from the client and updates record in the database
     *
     * @param id          - an id of a record to be updated in database table mount_pass_added
     * @param updatedPass - updated data of the record
     * @return Pass - an updated record in database table mount_pass_added
     * @throws BadRequestException in case user details in the updated record do not match user details in the database,
     *                             in case database record status is not "new", in case pass coordinates are missing int the updated record
     * @throws IOException
     */
    @PutMapping("/mount_pass_added/{id}")
    public Pass updatePass(@PathVariable("id") int id, @RequestBody String updatedPass) throws BadRequestException, IOException {
        Pass databasePass = passRepository.getById(id);
        if (!databasePass.getStatus().equals("new"))
            throw new BadRequestException("Cannot update, record has been processed already");
        if (!userDetailsMatch(databasePass.getRaw_data(), updatedPass))
            throw new BadRequestException("Cannot change user details");
        if (!passContainsCoords(updatedPass)) throw new BadRequestException("Pass coordinates are mandatory");

        databasePass.setRaw_data(JsonParser.getRawData(updatedPass));
        deletePassImages(databasePass.getImages());
        String imagesData = getPassImages(updatedPass);
        databasePass.setImages(imagesData);

        return passRepository.save(databasePass);
    }

    /**
     * deletes an image from the database table mount_pass_images
     *
     * @param id an id of the record in database table mount_pass_images
     */
    @DeleteMapping("/mount_pass_images/{id}")
    public void deleteImage(@PathVariable("id") int id) {
        imageRepository.deleteById(id);
    }

    /**
     * checks if pass data received from the client contains pass coordinates
     *
     * @param passJsonString a String representation of json received from the client
     * @return true is coordinates are available, otherwise false
     */
    private boolean passContainsCoords(String passJsonString) {
        return (!JsonParser.getLatitude(passJsonString).equals("") &&
                !JsonParser.getLongitude(passJsonString).equals("") &&
                !JsonParser.getHeight(passJsonString).equals(""));
    }

    /**
     * checks if pass data received from the client contains application user details
     *
     * @param passJsonString a String representation of json received from the client
     * @return
     */
    private boolean passContainsUserDetails(String passJsonString) {
        Pattern emailPattern = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        return (!JsonParser.getName(passJsonString).equals("") &&
                !JsonParser.getSurname(passJsonString).equals("") &&
                emailPattern.matcher(JsonParser.getEmail(passJsonString)).matches());
    }

    /**
     * gets an image as byte array from the URL
     *
     * @param url a URL of the image
     * @return image as byte array
     * @throws IOException in case URL could not be opened
     */
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

    /**
     * checks if user details match in two strings representing pass data received from client
     *
     * @param passJsonString        a string representation of json saved in the raw_data column of mount_pass_added table
     * @param updatedPassJsonString a String representation of json received from the client
     * @return true if user details match, otherwise false
     */
    private boolean userDetailsMatch(String passJsonString, String updatedPassJsonString) {
        return (JsonParser.getName(passJsonString).equals(JsonParser.getName(updatedPassJsonString)) &&
                JsonParser.getSurname(passJsonString).equals(JsonParser.getSurname(updatedPassJsonString)) &&
                JsonParser.getEmail(passJsonString).equals(JsonParser.getEmail(updatedPassJsonString)) &&
                JsonParser.getPhone(passJsonString).equals(JsonParser.getPhone(updatedPassJsonString)));
    }

    /**
     * deletes all images associated with a pass record in mount_pass_added table
     * @param passImagesJsonString
     */
    private void deletePassImages(String passImagesJsonString) {
        JSONObject imagesJson = new JSONObject(passImagesJsonString);
        Iterator<String> keys = imagesJson.keys();
        while (keys.hasNext()) {
            int imageID = imagesJson.getInt(keys.next());
            deleteImage(imageID);
        }
    }

    /**
     * @param passJsonString a String representation of json received from the client
     * @return a String representation of images json for mount_pass_added table
     * @throws IOException         in case image URL is invalid
     * @throws BadRequestException in case no images received from the client
     */
    private String getPassImages(String passJsonString) throws IOException, BadRequestException {
        HashMap<String, String> imagesMap = JsonParser.getUrlMap(passJsonString);
        StringBuilder imagesJson = new StringBuilder("{");
        if (!imagesMap.isEmpty()) {
            for (String key : imagesMap.keySet()) {
                Image image = addImage(getImageBytes(new URL(imagesMap.get(key))));
                imagesJson.append("\"").append(key).append("\":").append(image.getId()).append(",");
            }
            imagesJson.deleteCharAt(imagesJson.length() - 1).append("}");
        } else throw new BadRequestException("At least one image must be added");
        return imagesJson.toString();
    }
}
