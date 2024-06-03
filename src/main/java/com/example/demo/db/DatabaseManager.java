package com.example.demo.db;

import java.beans.BeanProperty;
import java.sql.*;
import com.example.demo.entities.User;
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "23072005"; // 3 cái ni điền vô à pass?

    private static Connection conn;

    public static void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }

    public static void registerUser(String name, String fullName, String password, String email, String gender,
            String phoneNo) {
        try {
            String query = "INSERT INTO users (name, fullName, password, email, gender, phoneNo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, fullName);
            stmt.setString(3, password);
            stmt.setString(4, email);
            stmt.setString(5, gender);
            stmt.setString(6, phoneNo);
            stmt.executeUpdate();
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to register user!");
            e.printStackTrace();
        }
    }

    public static boolean authenticateUser(String name, String password) {
        try {
            String query = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Failed to authenticate user!");
            e.printStackTrace();
        }
        return false;
    }

    public static User getUserData(String name) {
        try {
            String query = "SELECT * FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.name = rs.getString("name");
                user.fullName = rs.getString("fullName");
                user.password = rs.getString("password");
                user.email = rs.getString("email");
                user.gender = rs.getString("gender");
                user.phoneNo = rs.getString("phoneNo");
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve user data!");
            e.printStackTrace();
        }
        return null;
    }

    //Save image to database
    public static void saveImage(String name, byte[] image) {
        try {
            String query = "UPDATE users SET image = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setBytes(1, image);
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("Image saved successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to save image!");
            e.printStackTrace();
        }
    }

     public String getImageUser(String name) {
        try {
            String query = "SELECT image FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("image");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve image!");
            e.printStackTrace();
        }
        return null;
    }

    public void updateImageUser(String name, String url){
        try {
            String query = "UPDATE users SET image = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, url);
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("Image saved successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to save image!");
            e.printStackTrace();
        }
    }


}
