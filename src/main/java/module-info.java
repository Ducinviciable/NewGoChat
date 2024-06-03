module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires AnimateFX;
    requires java.desktop;
    requires javafx.swing;
    requires cloudinary.core;
    requires dotenv.java;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.db;
    exports com.example.demo.controllers;
    exports com.example.demo.entities;

    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.db to javafx.fxml;
    exports com.example.demo.config;
    opens com.example.demo.config to javafx.fxml;
    exports com.example.demo.services;
    opens com.example.demo.services to javafx.fxml;
    exports com.example.demo.Server;
    opens com.example.demo.Server to javafx.fxml;
}