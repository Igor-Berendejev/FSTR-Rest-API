package com.example.fstr.controller;

import com.example.fstr.exceptions.BadRequestException;
import com.example.fstr.exceptions.OperationExecutionException;
import com.example.fstr.model.Pass;
import com.example.fstr.repository.PassRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class PassController {
    @Autowired
    PassRepository passRepository;

    @PostMapping("/pereval_added")
    public Pass submitData(@RequestBody String pass) throws BadRequestException, OperationExecutionException {
        int indexOfImagesData = pass.indexOf("\"images\":");
        StringBuilder builder = new StringBuilder(pass.substring(0, indexOfImagesData - 1).trim());
        String passData = builder.replace(builder.length() - 1, builder.length() - 1, "}").toString();
        String imagesData = "{" + pass.substring(indexOfImagesData);
        if (!passContainsCoords(passData)) throw new BadRequestException("Pass coordinates are mandatory");
        return passRepository.save(new Pass(LocalDateTime.now(), passData, imagesData, "new"));
    }

    private boolean passContainsCoords(String passJsonString) {
        JSONObject passJson = new JSONObject(passJsonString);
        return (!passJson.getJSONObject("coords").get("latitude").equals("") &&
                !passJson.getJSONObject("coords").get("longitude").equals("") &&
                !passJson.getJSONObject("coords").get("height").equals(""));
    }
}
