package com.example.demo.controllers;

import animatefx.animation.FadeIn;
import com.example.demo.db.DatabaseManager;
import com.example.demo.entities.User;
import com.example.demo.services.CloudinaryService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;



import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.example.demo.controllers.Controller.users;

public class Room extends Thread implements Initializable {
    @FXML
    public Label clientName;
    @FXML
    public Button chatBtn;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label online;
    @FXML
    public Label fullName;
    @FXML
    public Label email;
    @FXML
    public Label phoneNo;
    @FXML
    public Label gender;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public TextField fileChoosePath;
    @FXML
    public ImageView proImage;
    @FXML
    public Circle showProPic;
    private FileChooser fileChooser;
    private File filePath;
    public boolean toggleChat = false, toggleProfile = false;

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;

    public void connectSocket() {
        try {
            socket = new Socket("localhost", 8889);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = reader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];
                System.out.println(cmd);
                StringBuilder fulmsg = new StringBuilder();
                for (int i = 1; i < tokens.length; i++) {
                    fulmsg.append(tokens[i]);
                }
                System.out.println(fulmsg);
                if (cmd.equalsIgnoreCase(Controller.username + ":")) {
                    continue;
                } else if (fulmsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
                msgRoom.appendText(msg + "\n");
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleProfileBtn(ActionEvent event) {
        if (event.getSource().equals(profileBtn) && !toggleProfile) {
            new FadeIn(profile).play();
            profile.toFront();
            chat.toBack();
            toggleProfile = true;
            toggleChat = false;
            profileBtn.setText("Back");
            setProfile();
        } else if (event.getSource().equals(profileBtn) && toggleProfile) {
            new FadeIn(chat).play();
            chat.toFront();
            toggleProfile = false;
            toggleChat = false;
            profileBtn.setText("Profile");
        }
    }

    public void setProfile() {
        User user = DatabaseManager.getUserData(Controller.username);
        if (user != null) {
            fullName.setText(user.getFullName());
            fullName.setOpacity(1);
            email.setText(user.getEmail());
            email.setOpacity(1);
            phoneNo.setText(user.getPhoneNo());
            gender.setText(user.getGender());
        }
    }

    public void handleSendEvent(MouseEvent event) {
        send();
        for (User user : users) {
            System.out.println(user.name);
        }
    }

    public void send() {
        String msg = msgField.getText();
        writer.println(Controller.username + ": " + msg);
        msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        msgRoom.appendText("Me: " + msg + "\n");
        msgField.setText("");
        if (msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
            System.exit(0);
        }
    }


    public boolean saveControl = false;

    public void chooseImageButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    public void sendMessageByKey(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    public boolean filterImage(File image) {
        // check size
        if (image.length() > 5000000) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification");
            String s = "Image size should not exceed 5 MB";
            alert.setContentText(s);
            alert.setHeaderText(null);
            alert.showAndWait();
            return false;
        }

        // check image JPG PNG
        if (!Objects.requireNonNull(image.getName()).endsWith(".jpg") && !Objects.requireNonNull(image.getName()).endsWith(".png")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification");
            String s = "Image only supports .png and .jpg format";
            alert.setContentText(s);
            alert.setHeaderText(null);
            alert.showAndWait();
            return false;
        }
        return true;
    }
    public void saveImage() {
        if (saveControl) {
            try {
                // check image before save
                if (!filterImage(filePath)) {
                    return;
                }
                // upload image Coudinary
                CloudinaryService cloudinaryService = new CloudinaryService();
                Map result = cloudinaryService.uploadFile(filePath);
                String url = (String) result.get("secure_url");

                BufferedImage bufferedImage = ImageIO.read(new URL(url));
                if (bufferedImage == null) {
                    throw new IOException("Unsupported image format or corrupted file");
                }
                // Save image to database
                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.updateImageUser(Controller.username, url);

                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                proImage.setImage(image);
                showProPic.setFill(new ImagePattern(image));
                saveControl = false;
                fileChoosePath.setText("");
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Notification");
                    String s = "Network error, please try again";
                    alert.setContentText(s);
                    alert.setHeaderText(null);
                    alert.showAndWait();
                });
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        if (Controller.gender.equalsIgnoreCase("Male")) {
            System.out.println(getClass().getResource("../"));
            image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/icons/user.png")).toExternalForm(), false);
        } else {
            image = new Image(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("/com/example/icons/female.png")).toExternalForm()), false);
            proImage.setImage(image);
        }
        //
        DatabaseManager databaseManager = new DatabaseManager();
        String imageUrl = databaseManager.getImageUser(Controller.username);
        if (imageUrl != null) {
            showProPic.setFill(new ImagePattern(new Image(imageUrl)));
            proImage.setImage(new Image(imageUrl));
//            image = new Image(imageUrl);/
        } else {
            showProPic.setFill(new ImagePattern(image));
            proImage.setImage(image);
        }
//        showProPic.setFill(new ImagePattern(image));
        clientName.setText(Controller.username);
        connectSocket();
    }

}
