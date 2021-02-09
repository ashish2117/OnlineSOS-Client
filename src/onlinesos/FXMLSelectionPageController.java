/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.awt.AWTException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLSelectionPageController implements Initializable {

    @FXML
    JFXButton mainButton, selectUserButton;
    @FXML
    JFXButton exit, okayButton;
    @FXML
    JFXRadioButton online;
    @FXML
    JFXRadioButton offline;
    @FXML
    TextField player1Nick, player2Nick;
    @FXML
    ToggleGroup mode;
    @FXML
    AnchorPane variablePane, messagePane;
    @FXML
    Label message, recInvitLabel;
    @FXML
    ListView<String> invitationList;
    @FXML
    ImageView rightImage, leftImage, centerImage;
    String selctedItem;
    private String selectedItem;
    ReadInvitations invThread;
    boolean exitloop;
    
    private void addPendingInvitations() {
        File file = new File("invitations.txt");

        Scanner sc = null;
        try {
            file.createNewFile();
            sc = new Scanner(file);
            JSONObject invitation;
            while (sc.hasNextLine()) {
                invitation = new JSONObject(sc.nextLine());
                if (invitation.get("Type").equals("INVITATION")) {
                    invitationList.getItems().add(invitation.getString("UserName")+ " Best Score:" + invitation.getInt("HighestScore")+ " Games Played:" + invitation.getInt("NumberOfGames"));
                }

            }

        } catch (JSONException ex) {
            Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        sc.close();
    }

    /**
     * Thread to read invitations //
     */
    private class ReadInvitations extends Thread {

        JSONObject obj;

        @Override
        public void run() {

            BufferedWriter writer = null;
            PrintWriter printwriter = null;
            exitloop=false;
            while (!exitloop) {
                try {
                    String message = SocketManger.messageReader.readMessage();
                    System.out.println("Read a message in selection");
                    System.out.println("Actual message "+ message);
//                    if (message.charAt(0) != '{') {
//                        message = "{" + message;
//                    }
//                    if (message.charAt(message.length() - 1) == '{') {
//                        message = message.substring(0, message.length() - 1);
//                    }
                    System.out.println("modified message "+message);
                    obj = new JSONObject(message);
                    if (obj.getString("Type").equals("INVITATION")) {
                        writer = new BufferedWriter(new FileWriter("invitations.txt", true));
                        printwriter = new PrintWriter(writer);
                        System.out.println(message);
                        printwriter.println(message);
                        printwriter.flush();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    invitationList.getItems().add(obj.getString("UserName")+ " Best Score:" + obj.getInt("HighestScore")+ " Games Played:" + obj.getInt("NumberOfGames"));
                                } catch (JSONException ex) {
                                    Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        new SendNotification().displayTray("New Game Request");
                    } else if (obj.getString("Type").equals("START_GAME_RESPONSE")) {
                        if (obj.getString("Response").equals("START_PLAYING")) {
                            if (obj.getString("Turn").equals(GameDetails.myUsername)) {
                                GameDetails.player1 = GameDetails.myUsername;
                                GameDetails.Player2 = obj.getString("UserName");
                            } else {
                                GameDetails.Player2 = GameDetails.myUsername;
                                GameDetails.player1 = obj.getString("UserName");
                            }
                            new SendNotification().displayTray("Game Started");
                            new SendNotification().displayTray("Game Started");
                            Context.getInstance().getPerson().setVisible(false);
                            Context.getInstance().getSettings().setVisible(false);
                            exitloop=true;
                            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLOnlineGamePage.fxml"));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    variablePane.getChildren().setAll(pane);
                                    
                                }
                            });
                        } else {
                            new SendNotification().displayTray(obj.getString("Response"));
                        }
                    } else if (obj.getString("Type").equals("INVITATION_ACCEPTED")) {
                        if (obj.getString("Turn").equals(GameDetails.myUsername)) {
                            GameDetails.player1 = GameDetails.myUsername;
                            GameDetails.Player2 = obj.getString("UserName");
                        } else {
                            GameDetails.Player2 = GameDetails.myUsername;
                            GameDetails.player1 = obj.getString("UserName");
                        }
                        new SendNotification().displayTray(obj.getString("UserName") + " accepted yor request");
                        new SendNotification().displayTray("Game Started");
                            Context.getInstance().getPerson().setVisible(false);
                            Context.getInstance().getSettings().setVisible(false);
                            exitloop=true;
                            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLOnlineGamePage.fxml"));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    variablePane.getChildren().setAll(pane);
                                    
                                }
                            });
                    }

                }catch (JSONException ex) {
                    System.out.println("JSON Exception");
                } catch (IOException ex) {
                    System.out.println("JSON Exception");
                } catch (AWTException ex) {
                    System.out.println("JSON Exception");
                }catch(Exception ex){
                    System.out.println("Caughtttt");
                    SocketManger.initSocket();
                    exitloop=true;  
                }
            }
            System.out.println("loop exit");
        }
    }

    /*
      Thread to animate icons
     */
    private class AnimateIcons extends Thread {

        boolean leftIswhite, centerIsWhite, rightIsWhite;

        public AnimateIcons() {
            leftIswhite = false;
            centerIsWhite = true;
            rightIsWhite = false;
        }

        @Override
        public void run() {
            short counter = 0;
            while (!exitloop) {
                try {
                    counter = (short) ((counter + 1) % 3);
                    Thread.sleep(500);
                    if (counter == 0) {
                        toggleLeftImage();
                        toggleCenterImage();
                    } else if (counter == 1) {
                        toggleCenterImage();
                        toggleRightImage();
                    } else if (counter == 2) {
                        toggleLeftImage();
                        toggleRightImage();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private void toggleLeftImage() {
            Image img;
            if (leftIswhite) {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_orange_70.png"));
            } else {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_white_70.png"));
            }
            leftImage.setImage(img);
            leftIswhite = !leftIswhite;
        }

        private void toggleCenterImage() {
            Image img;
            if (centerIsWhite) {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_orange_70.png"));
            } else {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_white_70.png"));
            }
            centerImage.setImage(img);
            centerIsWhite = !centerIsWhite;
        }

        private void toggleRightImage() {
            Image img;
            if (rightIsWhite) {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_orange_70.png"));
            } else {
                img = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_white_70.png"));
            }
            rightImage.setImage(img);
            rightIsWhite = !rightIsWhite;
        }

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(SocketManger.getSocket().isConnected()){
            try {
                SocketManger.getSocket().close();
            } catch (IOException ex) {
                Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        SocketManger.initSocket();
        System.out.println("Inside init");
        mode = new ToggleGroup();
        online.setToggleGroup(mode);
        offline.setToggleGroup(mode);
        player2Nick.setVisible(false);
        messagePane.setVisible(false);
        player1Nick.setVisible(false);
        online.setSelected(true);
        mainButton.setText("Search");
        new AnimateIcons().start();
        invThread = new ReadInvitations();
        invThread.start();
        Context.getInstance().getPerson().setVisible(true);
        Context.getInstance().getSettings().setVisible(true);
        invitationList.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            selectedItem = newValue;
            selectUserButton.setDisable(false);
        });
        addPendingInvitations();
        
    }

    @FXML
    private void onSelectUserButtonClick(ActionEvent event) {
        
        System.out.println(selectedItem.split(" ")[0]);
        
        JSONObject acceptMessage=new JSONObject();
        try {
            acceptMessage.put("Type", "START_GAME");
            acceptMessage.put("UserName",selectedItem.split(" ")[0]);
            SocketManger.messageSender.sendMessage(acceptMessage);
        } catch (JSONException ex) {
            Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLSelectionPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleButtonEnter(MouseEvent event) {
        mainButton.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleOkayButtonClick(ActionEvent event) {

        messagePane.setVisible(false);
        player1Nick.setDisable(false);
        player2Nick.setDisable(false);
        exit.setDisable(false);
        mainButton.setDisable(false);

    }

    @FXML
    private void handleButtonExit(MouseEvent event) {
        mainButton.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleSelectButtonEnter(MouseEvent event) {
        selectUserButton.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));

    }

    @FXML
    private void handleSelectButtonExit(MouseEvent event) {
        selectUserButton.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));

    }

    @FXML
    private void handleExitButtonEnter(MouseEvent event) {
        exit.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleExitButtonExit(MouseEvent event) {
        exit.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleOkayButtonEnter(MouseEvent event) {
        okayButton.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleOkayButtonExit(MouseEvent event) {
        okayButton.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleMainButtonClick(ActionEvent event) throws IOException {
        if (mainButton.getText().equals("Start Playing")) {
            if (player1Nick.getText().isEmpty()) {
                showMessage("Please Enter Your Nickname");
                return;
            } else if (player1Nick.getText().length() > 9 || player2Nick.getText().length() > 9) {
                showMessage("Nickname Can't be longer than 9 characters");
                return;
            } else if (!(offline.isSelected() || online.isSelected())) {
                showMessage("Please Select Game Mode");
                return;
            } else if (player2Nick.getText().isEmpty() && !online.isSelected()) {
                showMessage("Please Enter Friend's Nickname");
                return;
            } else if (offline.isSelected()) {
                SocketManger.getSocket().close();
                GameDetails.player1 = player1Nick.getText();
                GameDetails.Player2 = player2Nick.getText();
                Context.getInstance().getPerson().setVisible(false);
                Context.getInstance().getSettings().setVisible(false);
                AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLGamePage.fxml"));
                variablePane.getChildren().setAll(pane);
            }
        } else {
            SocketManger.getSocket().close();
            
            GameDetails.player1 = player1Nick.getText();
            GameDetails.Player2 = player2Nick.getText();
            Context.getInstance().getPerson().setVisible(false);
            Context.getInstance().getSettings().setVisible(false);
            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLUserList.fxml"));
            variablePane.getChildren().setAll(pane);
            
        }

    }

    @FXML
    private void handleExitButtonClick(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleOfflineClick(ActionEvent event) {

        player2Nick.setVisible(true);
        player1Nick.setVisible(true);
        mainButton.setText("Start Playing");
        invitationList.setVisible(false);
        recInvitLabel.setText("");
        selectUserButton.setVisible(false);
    }

    @FXML
    private void handleOnlineClick(ActionEvent event) {

        player2Nick.setVisible(false);
        player1Nick.setVisible(false);
        mainButton.setText("Search");
        invitationList.setVisible(true);
        recInvitLabel.setText("Received Invitations :");
        selectUserButton.setVisible(true);
    }

    private void showMessage(String text) {
        message.setText(text);
        messagePane.setVisible(true);
        player1Nick.setDisable(true);
        player2Nick.setDisable(true);
        exit.setDisable(true);
        mainButton.setDisable(true);
    }
}
