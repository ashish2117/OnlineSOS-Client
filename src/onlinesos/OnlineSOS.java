/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class OnlineSOS extends Application {

    private double xOffset = 0, yOffset = 0;
    

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = null;
        
        File file = new File("logindetails.txt");
        File file1=new File("invitations.txt");
        file1.delete();
        if (file.length() == 0) {
            root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String content = reader.readLine();
            String username = content.split(":")[1];
            JSONObject obj = new JSONObject();
            obj.put("Type", "BACK_ONLINE");
            obj.put("UserName", username);
            SocketManger.messageSender.sendMessage(obj);
            GameDetails.myUsername=username;
            root = root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        }
          stage.initStyle(StageStyle.TRANSPARENT);
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
