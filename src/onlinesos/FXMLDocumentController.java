/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.io.File;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable {
    String selected="";
    Game newGame;
    boolean down=false;
    @FXML
    AnchorPane variablePane;
    @FXML
    ImageView game;
    @FXML
    ImageView settings;
    @FXML
    ImageView person;
    @FXML
    ImageView stat;
    @FXML
    ImageView dock;
    Tooltip gameToolTip,settingsToolTip,offToolTip,dockToolTip,personToolTip;
   
    
    
    @FXML
    private void handleButtonAction(MouseEvent event) {
        if(event.getTarget()==game)
        {
            if(!selected.equals("game"))
               gameClicked();
        }
        else if(event.getTarget()==stat)
        {
            if(!selected.equals("stat"))
               statClicked();
        }
        else if(event.getTarget()==person)
        {
            if(!selected.equals("person"))
              personClicked();
        }
        else if(event.getTarget()==settings)
        {
            if(!selected.equals("settings"))
                settingsClicked();
        }
        else if(event.getTarget()==dock)
                dockClicked();
     }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
          AnchorPane pane=null;
          selected="game";
          gameToolTip= new Tooltip("Game");
          
          Tooltip.install(game, gameToolTip);
          dockToolTip=new Tooltip("Dock");
          Tooltip.install(dock, dockToolTip);
          personToolTip=new Tooltip("Profile");
          Tooltip.install(person, personToolTip);
          offToolTip=new Tooltip("Shut Down");
          Tooltip.install(stat, offToolTip);
          settingsToolTip=new Tooltip("Settings");
          Tooltip.install(settings, settingsToolTip);
        try {
            pane = FXMLLoader.load(getClass().getResource("FXMLSelectionPage.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
          variablePane.getChildren().setAll(pane);
          Context.getInstance().setGame(game);
          Context.getInstance().setPerson(person);
          Context.getInstance().setSettings(settings);
   }    

    private void gameClicked() {
        
        if(!selected.equals("game"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/game_orange.png"));
            game.setImage(img);
            toggleSelection("game");
            AnchorPane pane=null;
            try {
            pane = FXMLLoader.load(getClass().getResource("FXMLSelectionPage.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
          variablePane.getChildren().setAll(pane);
        }
    }

    private void statClicked() {
        System.exit(0);
    }

    private void settingsClicked() {
        
        if(!selected.equals("settings"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/settings_orange.png"));
            settings.setImage(img);
            toggleSelection("settings");
            AnchorPane pane=null;
            try {
            pane = FXMLLoader.load(getClass().getResource("FXMLSettings.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
          variablePane.getChildren().setAll(pane);
        
        }
    }

    private void dockClicked() {
        if(down)
        {
         Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/up.png"));
         dock.setImage(img);
         down=false;
         variablePane.setVisible(true);
        }
        else
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/down.png"));
            dock.setImage(img);
            down=true;
            variablePane.setVisible(false);
        }
    }

    private void personClicked() {
        
        if(!selected.equals("person"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/person_orange.png"));
            person.setImage(img);
            toggleSelection("person");
            AnchorPane pane=null;
            try {
            pane = FXMLLoader.load(getClass().getResource("FXMLUserProfile.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
          variablePane.getChildren().setAll(pane);
        
        }
    }

    private void toggleSelection(String newSelection) {
        if(selected.equals("game"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/game.png"));
            game.setImage(img);
        }
        else if(selected.equals("person"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/person.png"));
            person.setImage(img);
            
        }
        else if(selected.equals("settings"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/settings.png"));
            settings.setImage(img);
            
        }
        else if(selected.equals("stat"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/stat.png"));
            stat.setImage(img);
            
        }
        else if(selected.equals("dock"))
        {
            Image img=new Image(FXMLDocumentController.class.getResourceAsStream("images/down.png"));
            dock.setImage(img);
            
        }
        selected=newSelection;
    }
    
}
