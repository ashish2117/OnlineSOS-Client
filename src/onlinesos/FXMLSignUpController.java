/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLSignUpController implements Initializable {

    
    @FXML
    private TextField username;
    @FXML
    private TextField fullname;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField rePassword;
    @FXML
    private JFXButton signUp;
    @FXML
    private JFXButton openLoginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private JFXButton exit;
    @FXML
    AnchorPane rootPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        errorLabel.setText("");
    }

    @FXML
    private void onTextFieldClick(ActionEvent event) {
        errorLabel.setText("");
    }

    @FXML
    private void onMouseExit(MouseEvent event) {
        JFXButton button = (JFXButton) event.getTarget();
        button.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void onMouseEnter(MouseEvent event) {
        JFXButton button = (JFXButton) event.getTarget();
        button.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void onSignUpButtonClick(ActionEvent event) {
        try {
            String usernameString = username.getText().trim();
            String nameString = fullname.getText().trim();
            String emailString = email.getText().trim();
            String passwordString = password.getText().trim();
            String rePassString = rePassword.getText().trim();

            if (usernameString.isEmpty()) {
                throw new MyException("Please enter username");
            }

            if (usernameString.length() > 20) {
                throw new MyException("Username can't be longer than 20 characters.");
            }

            if (nameString.isEmpty()) {
                throw new MyException("Please enter your name.");
            }

            if (nameString.length() > 30) {
                throw new MyException("Name can't be longer than 30 characters.");
            }
            if (emailString.isEmpty()) {
                throw new MyException("Please enter email.");
            }
            if (emailString.length()>20) {
                throw new MyException("Email can't be longer than 50 characters.");
            }
            Pattern ptr=Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            if (!ptr.matcher(emailString).matches()) {
                throw new MyException("Invalid email.");
            }
            if (passwordString.isEmpty()) {
                throw new MyException("Please enter password.");
            }
            if (passwordString.length()<6) {
                throw new MyException("Password must be at least 6 charachters long.");
            }
            if (passwordString.length()>20) {
                throw new MyException("Password can't be longer than 30 characters.");
            }
            if(!passwordString.equals(rePassString)){
                throw new MyException("Passwords didn't .");
            }
            
            JSONObject obj = new JSONObject();
            obj.put("Type", "SIGN_UP_REQUEST");
            obj.put("UserName", username.getText().trim());
            obj.put("Name", fullname.getText().trim());
            obj.put("Password", password.getText().trim());
            obj.put("Email", email.getText().trim());
            SocketManger.messageSender.sendMessage(obj);
            String message = SocketManger.messageReader.readMessage();
            obj = new JSONObject(message);
            if (obj.getString("Type").equals("SIGN_UP_RESULT")) {
                if (obj.getString("Result").equals("Success")) {
                    File loginDetails = new File("logindetails.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(loginDetails));
                    writer.write("Username:" + username.getText().trim() + "\n");
                    writer.close();
                    GameDetails.myUsername=username.getText();
                    SocketManger.getSocket().close();
                    AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                    rootPane.getChildren().setAll(pane);
                } else {
                    errorLabel.setText(obj.getString("Result"));
                }
            }

        } catch (MyException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (JSONException ex) {
            Logger.getLogger(FXMLSignUpController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLSignUpController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onOpenLoginButtonClick(ActionEvent event) {
        System.out.print("Clicked");
        AnchorPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLSignUpController.class.getName()).log(Level.SEVERE, null, ex);
        }
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void exitButtonClick(ActionEvent event) {
        try {
            SocketManger.getSocket().getOutputStream().flush();
            SocketManger.getSocket().close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(FXMLSignUpController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
