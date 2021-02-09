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
public class FXMLLoginController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private JFXButton signInButton;
    @FXML
    private JFXButton openSignUp;
    @FXML
    private Label errorLabel;
    @FXML
    private JFXButton exit;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
     }

    @FXML
    private void onTextFieldCliclk(MouseEvent event) {
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
    private void onSignInClick(ActionEvent event) {
        try {
            
            String usernameString=username.getText().trim();
            String passwordString=password.getText().trim();
            
            if (usernameString.isEmpty()) 
                throw new MyException("Please Enter Username");
            
            if(usernameString.length()>20)
                throw new MyException("Username can't be longer than 20 characters");
            
            if (passwordString.isEmpty()) 
                throw new MyException("Please Enter Passowrd");
            
            if(passwordString.length()<6)
                throw new MyException("Password must be at least 6 characters long");
            
            if(passwordString.length()>20)
                throw new MyException("Password can't be longer than 20 characters"); 
            JSONObject obj = new JSONObject();

            obj.put("Type", "LOGIN_REQUEST");
            obj.put("UserName", username.getText().trim());
            obj.put("Password", password.getText().trim());

            SocketManger.messageSender.sendMessage(obj);
            System.out.print("message sent\n");
            String message = SocketManger.messageReader.readMessage();

            obj = new JSONObject(message);
            if (obj.getString("Type").equals("LOGIN_RESULT")) {
                if (obj.getString("Result").equals("Success")) {
                    File loginDetails = new File("logindetails.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(loginDetails));
                    writer.write("Username:" + username.getText().trim() + "\n");
                    writer.close();
                    SocketManger.getSocket().close();
                    GameDetails.myUsername=username.getText();
                    AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                    rootPane.getChildren().setAll(pane);
                } else {
                    errorLabel.setText(obj.getString("Result"));
                }
            }

        } catch (JSONException ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MyException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void onOpenSignUp(ActionEvent event) {
        AnchorPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getResource("FXMLSignUp.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void onExitButtonClick(ActionEvent event) {
        try {
            SocketManger.getSocket().getOutputStream().flush();
            SocketManger.getSocket().close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(FXMLSignUpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
