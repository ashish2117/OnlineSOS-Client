/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLOnlineGamePageController implements Initializable {

    private boolean sIsSelected;
    Socket socket;

    String currentPlayer;
    String player1;
    String player2;
    short arr[][];

    @FXML
    private AnchorPane variablePane;
    @FXML
    private AnchorPane gamePane;
    @FXML
    private GridPane imagesPane;
    @FXML
    private ImageView player1TurnImage;
    @FXML
    private ImageView player2TurnImage;
    @FXML
    private Label player1Turn;
    @FXML
    private Label player2Turn;
    @FXML
    private Label player1NameAtScore;
    @FXML
    private Label player2NameAtScore;

    @FXML
    private ImageView sImage;
    @FXML
    private ImageView oImage;
    @FXML
    private AnchorPane messagePane;

    @FXML
    private Label messageLable;
    @FXML
    private JFXButton exit;
    ArrayList<Location[]> list;

    String resultMessage;
    String soundName;
    AudioInputStream audioInputStream;
    Clip clip;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arr = new short[7][7];
        player1 = GameDetails.player1;
        player2 = GameDetails.Player2;
        currentPlayer = player1;
        player1Turn.setText(player1);
        player2Turn.setText(player2);
        player1NameAtScore.setText(player1 + " : 0");
        player2NameAtScore.setText(player2 + " : 0");

        if (!player1.equals(GameDetails.myUsername)) {
            imagesPane.setDisable(true);
        }
        player2TurnImage.setVisible(false);
        JSONObject gameStartAck = new JSONObject();
        try {
            gameStartAck.put("Type", "GAME_STARTED");
            SocketManger.messageSender.sendMessage(gameStartAck);
        } catch (JSONException ex) {
            Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        messagePane.setVisible(false);
        new ReadMessages().start();
        sIsSelected = true;
        soundName = "media/success.wav";
    }

    private class ReadMessages extends Thread {

        boolean gameOver = false;
        JSONObject jsonMessage;

        @Override
        public void run() {
            try {
                while (!gameOver) {
                    String message = SocketManger.messageReader.readMessage();
                    System.out.println("Mesage Read" + message);
                    jsonMessage = new JSONObject(message);
                    if (jsonMessage.getString("Type").equals("CELLCLICKED")) {
                        ImageView image = getGridElementById("_" + jsonMessage.getInt("iVal") + jsonMessage.getInt("jVal"));
                        arr[jsonMessage.getInt("iVal")][jsonMessage.getInt("jVal")] = (short) jsonMessage.getInt("Val");
                        addCellImageRec(image, jsonMessage.getInt("Val"));

                        if (player1.equals(GameDetails.myUsername)) {
                            player1TurnImage.setVisible(true);
                            player2TurnImage.setVisible(false);
                            System.out.println("Player1 turn");
                        } else {
                            player1TurnImage.setVisible(false);
                            player2TurnImage.setVisible(true);
                            System.out.println("Player2 turn");
                        }
                        imagesPane.setDisable(false);
                        currentPlayer = GameDetails.myUsername;
                    } else if (jsonMessage.getString("Type").equals("STRIKEDLOCS")) {
                        try {
                            audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                        } catch (UnsupportedAudioFileException ex) {
                            System.out.print(ex.getMessage());
                        } catch (IOException ex) {
                            System.out.print(ex.getMessage());
                        }
                        clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                        clip.start();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!gameOver) {
                                        player1NameAtScore.setText(player1 + " : " + jsonMessage.getInt("player1"));
                                        player2NameAtScore.setText(player2 + " : " + jsonMessage.getInt("player2"));
                                    }
                                } catch (JSONException ex) {
                                    Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });

                        arr[jsonMessage.getInt("iVal")][jsonMessage.getInt("jVal")] = (short) jsonMessage.getInt("Val");
                        JSONArray array = new JSONArray(jsonMessage.getString("STRIKED_LOCS_ARRAY"));
                        JSONObject strike;
                        Location loc;
                        ArrayList<Location> strikedLocs;
                        for (int i = 0; i < array.length(); i++) {
                            strike = array.getJSONObject(i);
                            strikedLocs = new ArrayList<>();
                            String locations[] = strike.getString("STRIKES").split(" ");
                            for (int k = 0; k < 6; k += 2) {
                                loc = new Location(Integer.parseInt(locations[k]), Integer.parseInt(locations[k + 1]));
                                strikedLocs.add(loc);

                            }
                            updateStrikedCells(strikedLocs);
                        }
                    } else if (jsonMessage.getString("Type").equals("GAME_RESULT")) {
                        gameOver = true;
                        String winner = jsonMessage.getString("Winner");
                        if (winner.equals("draw")) {
                            resultMessage = "Oops! It's a draw!";
                        } else if (winner.equals(GameDetails.myUsername)) {
                            resultMessage = "Congrats! You won the clash!";
                        } else {
                            resultMessage = "Ohh! " + winner + " won the clash!";
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                messageLable.setText(resultMessage);
                                imagesPane.setDisable(true);
                                messagePane.setVisible(true);
                            }
                        });
                    } else if (jsonMessage.getString("Type").equals("ERROR")) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    messageLable.setText(jsonMessage.getString("Message"));
                                } catch (JSONException ex) {
                                    Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                messagePane.setVisible(true);
                                imagesPane.setDisable(true);
                            }
                        });

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onCellClicked(MouseEvent event) {
        ImageView img = (ImageView) event.getTarget();
        String id = img.getId();
        int i = Integer.parseInt(id.charAt(1) + "");
        int j = Integer.parseInt(id.charAt(2) + "");
        JSONObject obj = new JSONObject();
        try {
            obj.put("Type", "CELLCLICKED");
            obj.put("iVal", i);
            obj.put("jVal", j);
            obj.put("Val", sIsSelected ? 1 : 2);
            obj.put("UserName", GameDetails.myUsername);
            SocketManger.messageSender.sendMessage(obj);
            System.out.println("Sent message " + obj.toString());
            arr[i][j] = (short) (sIsSelected ? 1 : 2);
            addCellImage(img);
            if (player1.equals(GameDetails.myUsername)) {
                player1TurnImage.setVisible(false);
                player2TurnImage.setVisible(true);
                System.out.println("Player2 turn");
            } else {
                player1TurnImage.setVisible(true);
                player2TurnImage.setVisible(false);
                System.out.println("Player1 turn");
            }
            imagesPane.setDisable(true);

            currentPlayer = GameDetails.myUsername.equals(player1) ? player2 : player1;
        } catch (JSONException ex) {
            Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLOnlineGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void selectionImageClicked(MouseEvent event) {
        if (event.getTarget() == sImage) {
            Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_s_white_40.png"));
            Image oimg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_white_40.png"));
            sImage.setImage(simg);
            oImage.setImage(oimg);
            sIsSelected = true;
        } else if (event.getTarget() == oImage) {
            Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_white_40.png"));
            Image oimg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_o_white_40.png"));
            sImage.setImage(simg);
            oImage.setImage(oimg);
            sIsSelected = false;
        }
    }

    @FXML
    private void handleExitButtonExit(MouseEvent event) {
        exit.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleExitButtonEnter(MouseEvent event) {
        exit.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handleExitButtonClick(ActionEvent event) {
        System.exit(0);
    }

    private void addCellImage(ImageView img) {
        if (sIsSelected) {
            if (currentPlayer.equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_black_36.png"));
                img.setImage(simg);
            }
        } else {
            if (currentPlayer.equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_black_36.png"));
                img.setImage(simg);
            }
        }
    }

    private void addCellImageRec(ImageView img, int val) {

        if (val == 1) {
            if (currentPlayer.equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_black_36.png"));
                img.setImage(simg);
            }
        } else {
            if (currentPlayer.equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_black_36.png"));
                img.setImage(simg);
            }
        }
    }

    private void updateStrikedCells(ArrayList<Location> strikedLocs) {
        ImageView imageLoc;
        System.out.println(strikedLocs);
        for (int j = 0; j < 3; j++) {
            Location loc = strikedLocs.get(j);
            imageLoc = getGridElementById("_" + loc.getX() + loc.getY());

            if (arr[loc.getX()][loc.getY()] == 1) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_s_green1_36.png"));
                imageLoc.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_o_green1_36.png"));
                imageLoc.setImage(simg);
            }

        }

    }

    private ImageView getGridElementById(String id) {
        ObservableList<Node> list = imagesPane.getChildren();
        ImageView imageLoc = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                imageLoc = (ImageView) list.get(i);
                break;
            }
        }
        return imageLoc;
    }

}
