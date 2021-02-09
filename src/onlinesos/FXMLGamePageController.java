/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLGamePageController implements Initializable {

    Game game;
    boolean sIsSelected;
    String soundName;
    AudioInputStream audioInputStream;
    Clip clip;
    @FXML
    JFXButton playAgain, exit, leaveGameButton;
    @FXML
    AnchorPane variablePane, gamePane, messagePane;
    @FXML
    GridPane imagesPane;
    @FXML
    ImageView oImage, sImage, player1TurnImage, player2TurnImage;
    @FXML
    Label player1Turn, player2Turn, player1NameAtScore, player2NameAtScore, player1Score, player2Score, message;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        game = new Game(GameDetails.player1, GameDetails.Player2);
        player2TurnImage.setVisible(false);
        player1NameAtScore.setText(GameDetails.player1);
        player2NameAtScore.setText(GameDetails.Player2);
        player2Score.setText("0");
        player1Score.setText("0");
        player1Turn.setText(GameDetails.player1);
        player2Turn.setText(GameDetails.Player2);
        sIsSelected = true;
        soundName = "media/success.wav";
        messagePane.setVisible(false);
        if (SocketManger.getSocket().isConnected()) {
            try {
                SocketManger.getSocket().close();
            } catch (IOException ex) {
                Logger.getLogger(FXMLGamePageController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    private void onLeaveGameButtonClick(ActionEvent event){
        AnchorPane pane=null;
        try {
            SocketManger.initSocket();
            File file = new File("logindetails.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String content = reader.readLine();
            String username = content.split(":")[1];
            JSONObject obj = new JSONObject();
            obj.put("Type", "BACK_ONLINE");
            obj.put("UserName", username);
            new MessageSender().sendMessage(obj);
            pane = FXMLLoader.load(getClass().getResource("FXMLSelectionPage.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(FXMLGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
          variablePane.getChildren().setAll(pane);
    }

    @FXML
    private void handlePlayAgainEnter(MouseEvent event) {
        playAgain.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    @FXML
    private void onLeaveGameButtonEnter(MouseEvent event) {
        leaveGameButton.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
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
    private void onLeaveGameButtonExit(MouseEvent event) {
        leaveGameButton.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void handlePlayAgainClick(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLGamePage.fxml"));
        variablePane.getChildren().setAll(pane);
    }

    @FXML
    private void handleExitButtonClick(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handlePlayAgainExit(MouseEvent event) {
        playAgain.setBackground(new Background(new BackgroundFill(Color.web("#3D4956"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void onCellClicked(MouseEvent event) throws LineUnavailableException, IOException {

        ImageView img = (ImageView) event.getTarget();
        String id = event.getTarget().toString().substring(13, 16);
        int i = Integer.parseInt(id.charAt(1) + "");
        int j = Integer.parseInt(id.charAt(2) + "");
        if (game.getValueAt(i, j) != 0) {
            return;
        }
        addCellImage(img);
        short value = sIsSelected ? (short) 1 : 2;
        ArrayList<Location[]> strikedLocs = game.changeState(i, j, value);

        if (strikedLocs.size() != 0) {
            updateStrikedCells(strikedLocs);
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
            if (game.currentPlayer.equals(GameDetails.player1)) {
                player1Score.setText(game.getPlayer1Points() + "");
            } else {
                player2Score.setText(game.getPlayer2Points() + "");
            }
        } else {
            if (game.getCurrentPlayer().equals(GameDetails.player1)) {
                player1TurnImage.setVisible(true);
                player2TurnImage.setVisible(false);
            } else {
                player1TurnImage.setVisible(false);
                player2TurnImage.setVisible(true);
            }
        }
        if (game.getCount() == 49) {
            if (game.getPlayer1Points() > game.getPlayer2Points()) {
                message.setText(GameDetails.player1 + " won the clash!");
            } else if (game.getPlayer2Points() > game.player1Points) {
                message.setText(GameDetails.Player2 + " won the clash!");
            } else {
                message.setText("Oops! It's a draw");
            }
            messagePane.setVisible(true);
        }
    }

    private void addCellImage(ImageView img) {
        if (sIsSelected) {
            if (game.getCurrentPlayer().equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_s_black_36.png"));
                img.setImage(simg);
            }
        } else {
            if (game.getCurrentPlayer().equals(GameDetails.player1)) {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_orange_36.png"));
                img.setImage(simg);
            } else {
                Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/empty_o_black_36.png"));
                img.setImage(simg);
            }
        }
    }

    private void updateStrikedCells(ArrayList<Location[]> strikedLocs) {
        ImageView imageLoc;
        System.out.println(strikedLocs);
        for (int i = 0; i < strikedLocs.size(); i++) {
            Location loc[] = strikedLocs.get(i);
            for (int j = 0; j < 3; j++) {
                imageLoc = getGridElementById("_" + loc[j].getX() + loc[j].getY());

                if (game.getValueAt(loc[j].getX(), loc[j].getY()) == 1) {
                    Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_s_green1_36.png"));
                    imageLoc.setImage(simg);
                } else {
                    Image simg = new Image(FXMLDocumentController.class.getResourceAsStream("images/filled_o_green1_36.png"));
                    imageLoc.setImage(simg);
                }

            }
        }
    }

    private ImageView getGridElementById(String id) {
        System.out.println("getGrid Id " + id);
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
