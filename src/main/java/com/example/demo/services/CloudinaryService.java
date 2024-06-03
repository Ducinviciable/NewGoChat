package com.example.demo.services;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService() {
        Dotenv dotenv = Dotenv.configure().directory(String.valueOf(Paths.get("src", "main", "resources"))).load();
        this.cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }
    public Map uploadFile(File file) throws IOException {
        return cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
    }

}
