/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
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
public class FXMLUserListController implements Initializable {

    Socket socket;
    //private String data;
    private String selectedItem;
    BufferedReader reader;
    @FXML
    private TextField searchBar;
    @FXML
    private JFXListView<String> userListt;
    @FXML
    private JFXButton exitButton, goOfflineButton;
    @FXML
    private JFXButton selectUserButton;
    @FXML
    private JFXCheckBox onlineCheckBox, allCheckBox, offlineCheckBox;
    @FXML
    AnchorPane rootPane;
    boolean isSearching;
    ReadMessages messageReader;
    String message;
    boolean exitloop;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
            
        userListt.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            selectedItem = newValue;
            if (newValue != null && !newValue.contains("unavailable")) {
                selectUserButton.setDisable(false);
            } else {
                selectUserButton.setDisable(true);
            }
        });

        try {

            SocketManger.messageSender = new MessageSender();
            socket = SocketManger.getSocket();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messageReader = new ReadMessages();
            messageReader.start();
            JSONObject obj = new JSONObject();
            obj.put("Type", "BACK_ONLINE");
            obj.put("UserName", GameDetails.myUsername);
            SocketManger.messageSender.sendMessage(obj);
        } catch (ConnectException ex) {
            System.out.print("Server is Offline");
        } catch (IOException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private class ReadMessages extends Thread {

        JSONObject invitation;

        @Override
        public void run() {

            BufferedWriter writer = null;
            PrintWriter printwriter = null;
            exitloop=false;
            while (!exitloop) {
                try {
                    message = SocketManger.messageReader.readMessage();
                    System.out.println("Read a message in Userlist");
                    System.out.println("Actual Message"+message);
//                    if (message.charAt(0) != '{' && message.charAt(0) != '{') {
//                        message = "{" + message;
//                    }
//                    if (message.charAt(message.length() - 1) == '{') {
//                        message = message.substring(0, message.length() - 1);
//                    }
//                    System.out.println(message);
                    invitation = new JSONObject(message);
                    if (invitation.getString("Type").equals("INVITATION")) {
                        writer = new BufferedWriter(new FileWriter("invitations.txt", true));
                        printwriter = new PrintWriter(writer);
                        System.out.println(message);
                        printwriter.println(message);
                        printwriter.flush();
                        new SendNotification().displayTray("New Game Request");
                    } else if (invitation.getString("Type").equals("INVITATION_RESPONSE")) {
                        new SendNotification().displayTray(invitation.getString("Response"));
                    } else if (invitation.getString("Type").equals("START_GAME_RESPONSE")) {
                        if (invitation.getString("Response").equals("START_PLAYING")) {
                            if (invitation.getString("Turn").equals(GameDetails.myUsername)) {
                                GameDetails.player1 = GameDetails.myUsername;
                                GameDetails.Player2 = invitation.getString("UserName");
                            } else {
                                GameDetails.Player2 = GameDetails.myUsername;
                                GameDetails.player1 = invitation.getString("UserName");
                            }
                            
                            new SendNotification().displayTray("Game Started");
                            Context.getInstance().getPerson().setVisible(false);
                            Context.getInstance().getSettings().setVisible(false);
                            exitloop=true;
                            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLOnlineGamePage.fxml"));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    rootPane.getChildren().setAll(pane);
                                    
                                }
                            });
                        } else {
                            new SendNotification().displayTray(invitation.getString("Response"));
                        }
                    } else if (invitation.getString("Type").equals("INVITATION_ACCEPTED")) {
                        if (invitation.getString("Turn").equals(GameDetails.myUsername)) {
                            GameDetails.player1 = GameDetails.myUsername;
                            GameDetails.Player2 = invitation.getString("UserName");
                        } else {
                            GameDetails.Player2 = GameDetails.myUsername;
                            GameDetails.player1 = invitation.getString("UserName");
                        }
                        new SendNotification().displayTray(invitation.getString("UserName") + " accepted yor request");
                            Context.getInstance().getPerson().setVisible(false);
                            Context.getInstance().getSettings().setVisible(false);
                            exitloop=true;
                            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLOnlineGamePage.fxml"));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    rootPane.getChildren().setAll(pane);
                                    
                                }
                            });
                    }
                }catch(SocketException ex){
                    
                }
                catch (JSONException ex) {
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println(message);
                                JSONArray users = new JSONArray(message);
                                inflateList(users);
                            } catch (JSONException ex1) {
                                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                    });
                } catch (Exception ex) {
                    if(SocketManger.getSocket().isConnected()){
                        try {
                            SocketManger.getSocket().close();
                        } catch (IOException ex1) {
                            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    return;
                }
            }
            System.out.println("Loop exit");
        }
    }

    @FXML
    private void onGoOfflineButtonClick(ActionEvent event) {
        try {
            SocketManger.getSocket().close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        AnchorPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getResource("FXMLSelectionPage.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void onSearchBarAction(ActionEvent event) {
        populateList();
    }

    @FXML
    private void onSearchBarImageClick(MouseEvent event) {
        populateList();
    }

    @FXML
    private void listExit(MouseEvent event) {

    }

    @FXML
    private void listEnter(MouseEvent event) {
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        JFXButton button = (JFXButton) event.getTarget();
        button.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void onButtonEnter(MouseEvent event) {
        JFXButton button = (JFXButton) event.getTarget();
        button.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void onExitButtonClick(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onSelectUserButtonClick(ActionEvent event) {
        String userrr;
        String gamesPlayed;
        String bestScore;
        System.out.println(selectedItem);
        String str[] = selectedItem.split("   ");
        userrr = str[0].split(" ")[0];
        gamesPlayed = (str[2].split(":"))[1];
        bestScore = (str[1].split(":"))[1];
        JSONObject invitation = new JSONObject();
        try {
            invitation.put("Type", "INVITATION");
            invitation.put("UserName", userrr);
            invitation.put("NumberOfGames", gamesPlayed);
            invitation.put("HighestScore", bestScore);
            System.out.println(invitation.toString());
            SocketManger.messageSender.sendMessage(invitation);

        } catch (JSONException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onAllCheckBoxClick(MouseEvent event) {
        if (allCheckBox.isSelected()) {
            onlineCheckBox.setSelected(true);
            offlineCheckBox.setSelected(true);
        } else {
            onlineCheckBox.setSelected(false);
            offlineCheckBox.setSelected(false);
        }
        if (!searchBar.getText().isEmpty()) {
            userListt.getItems().clear();
            populateList();
        }

    }

    @FXML
    private void onOfflineCheckBoxClick(MouseEvent event) {
        onlineCheckBox.setSelected(false);
        allCheckBox.setSelected(false);
        if (!searchBar.getText().isEmpty()) {
            userListt.getItems().clear();
            populateList();
        }
    }

    @FXML
    private void onOnlineCheckBoxClick(MouseEvent event) {
        offlineCheckBox.setSelected(false);
        allCheckBox.setSelected(false);
        if (!searchBar.getText().isEmpty()) {
            userListt.getItems().clear();
            populateList();
        }
    }

    private void populateList() {
        if (searchBar.getText().isEmpty()) {
            return;
        }
        if (!(onlineCheckBox.isSelected() || offlineCheckBox.isSelected() || allCheckBox.isSelected())) {
            onlineCheckBox.setSelected(true);
        }

        JSONObject obj = new JSONObject();
        if (onlineCheckBox.isSelected() && !allCheckBox.isSelected()) {
            try {
                obj.put("Type", "GET_ONLINE_USERS");
                obj.put("Key", searchBar.getText());
                SocketManger.messageSender.sendMessage(obj);

            } catch (JSONException ex) {
                try {
                    obj.put("Type", "GET_ONLINE_USERS");
                    obj.put("Key", searchBar.getText());
                    SocketManger.messageSender.sendMessage(obj);

                } catch (JSONException ex1) {
                    Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (IOException ex1) {
                    Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (IOException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (offlineCheckBox.isSelected() && !allCheckBox.isSelected()) {
            try {
                obj.put("Type", "GET_OFFLINE_USERS");
                obj.put("Key", searchBar.getText());
                SocketManger.messageSender.sendMessage(obj);

            } catch (JSONException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                obj.put("Type", "GET_ALL_USERS");
                obj.put("Key", searchBar.getText());
                SocketManger.messageSender.sendMessage(obj);

            } catch (JSONException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void inflateList(JSONArray users) {
        JSONObject obj;
        ObservableList<String> list = FXCollections.observableArrayList();
        userListt.getItems().clear();
        for (int i = 0; i < users.length(); i++) {
            try {

                obj = users.getJSONObject(i);
                if(!obj.getString("UserName").equals(GameDetails.myUsername)){
                if (obj.getBoolean("IsPlaying") || !obj.getBoolean("IsOnline")) {
                    list.add(obj.getString("UserName") + " (unavailable)"
                            + "   Best Score:" + obj.getInt("HighestScore")
                            + "   Games Played:" + obj.getInt("NumberOfGames"));
                } else {
                    list.add(obj.getString("UserName") + " (available)"
                            + "   Best Score:" + obj.getInt("HighestScore")
                            + "   Games Played:" + obj.getInt("NumberOfGames"));
                }
            }
            }catch (JSONException ex) {
                Logger.getLogger(FXMLUserListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        userListt.getItems().addAll(list);
    }

}
