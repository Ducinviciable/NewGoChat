package com.example.demo.controllers;

import animatefx.animation.FadeIn;
import com.example.demo.MainApplication;
import com.example.demo.db.DatabaseManager;
import com.example.demo.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public ImageView btnBack;
    @FXML
    public TextField regName;
    @FXML
    public TextField regPass;
    @FXML
    public TextField regEmail;
    @FXML
    public TextField regFirstName;
    @FXML
    public TextField regPhoneNo;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
    @FXML
    public TextField userName;
    @FXML
    public TextField passWord;
    @FXML
    public Label loginNotifier;
    @FXML
    public Label nameExists;
    @FXML
    public Label checkEmail;

    @FXML
    public Button LOGIN;

    public static String username, password, gender;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<User>();

    public Controller() {
        DatabaseManager.connect();
    }

    public void registration() {
        if (!regName.getText().equalsIgnoreCase("")
                && !regPass.getText().equalsIgnoreCase("")
                && !regEmail.getText().equalsIgnoreCase("")
                && !regFirstName.getText().equalsIgnoreCase("")
                && !regPhoneNo.getText().equalsIgnoreCase("")
                && (male.isSelected() || female.isSelected())) {

            String name = regName.getText();
            String fullName = regFirstName.getText();
            String password = regPass.getText();
            String email = regEmail.getText();
            String gender = male.isSelected() ? "Male" : "Female";
            String phoneNo = regPhoneNo.getText();

            if (!email.contains("@")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Notification");
                alert.setHeaderText(null);
                alert.setContentText("Email must contain '@'");
                alert.showAndWait();
                return;
            }

            try {
                DatabaseManager.registerUser(name, fullName, password, email, gender, phoneNo);
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Notification");
                String s = "Registration Failed! Your username already exists";
                alert.setContentText(s);
                alert.showAndWait();
            }
            goBack.setOpacity(1);
            success.setOpacity(1);
            makeDefault();
            if (controlRegLabel.getOpacity() == 1) {
                controlRegLabel.setOpacity(0);
            }
            if (nameExists.getOpacity() == 1) {
                nameExists.setOpacity(0);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists, checkEmail);
        }
    }


    private void setOpacity(Label a, Label b, Label c, Label d) {
        if (a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1 || d.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
            d.setOpacity(0);
        }
    }

    private void setOpacity(Label controlRegLabel, Label checkEmail, Label nameExists) {
        controlRegLabel.setOpacity(0);
        checkEmail.setOpacity(0);
        nameExists.setOpacity(0);
    }

    private boolean checkUser(String username) {
        for (User user : users) {
            if (user.name.equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkEmail(String email) {
        for (User user : users) {
            if (user.email.equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    private void makeDefault() {
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
        regFirstName.setText("");
        regPhoneNo.setText("");
        male.setSelected(true);
        setOpacity(controlRegLabel, checkEmail, nameExists);
    }

    public void login() {
        DatabaseManager.connect();
        username = userName.getText();
        password = passWord.getText();
        if(username.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification!");
            alert.setHeaderText(null);
            alert.setContentText("Username cannot be empty!");
            alert.showAndWait();
            return;
        }

        if(password.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification!");
            alert.setHeaderText(null);
            alert.setContentText("Password cannot be empty!");
            alert.showAndWait();
            return;
        }
        boolean isAuthenticated = DatabaseManager.authenticateUser(username, password);
        if (isAuthenticated) {
            loggedInUser.add(DatabaseManager.getUserData(username));
            gender = loggedInUser.get(0).gender;
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("Room.fxml")));
            stage.setScene(new Scene(root));
            stage.setTitle(username + "");
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnBack) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}