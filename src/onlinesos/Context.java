package onlinesos;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.scene.image.ImageView;

/**
 *
 * @author user
 */
public class Context {
    private final static Context instance = new Context();
    
    private ImageView game=new ImageView();
    private ImageView person=new ImageView();
    private ImageView settings=new ImageView();
    
    
    public static Context getInstance() {
        return instance;
    }

    public ImageView getGame() {
        return game;
    }

    public ImageView getPerson() {
        return person;
    }

    public ImageView getSettings() {
        return settings;
    }

    public void setGame(ImageView game) {
        this.game = game;
    }

    public void setPerson(ImageView person) {
        this.person = person;
    }

    public void setSettings(ImageView settings) {
        this.settings = settings;
    }
    
    
}
