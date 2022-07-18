package com.example;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.scene.input.MouseEvent;

public class Cue {
    public static final int WIDTH = 390;
    public static final int HEIGHT = 14;
    private ImageView image = new ImageView();
    private static Cue instance = null;

    private Rotate rotation = new Rotate();

    private Cue() {
        image.setImage(new Image(getClass().getResource("pictures/cue.png").toExternalForm()));
    }

    
    public void setRotation(Rotate newRotation){
        this.rotation = newRotation;
        image.getTransforms().clear();
        image.getTransforms().add(rotation);
    }

    public Rotate getRotation(){
        return rotation;
    }

    public ObservableList<Transform> getTransforms(){
        return image.getTransforms();
    }

    public Node getNode(){
        return image;
    }

    public static Cue getInstance() {
        if (instance == null) {
            instance = new Cue();
        }
        return instance;
    }

    public void setTipPlace(Vector2D newPlace){
        // image.setX(newPlace.getX());
        // image.setY(newPlace.getY());
        image.relocate(newPlace.getX(), newPlace.getY()-HEIGHT/2);
    }
}
