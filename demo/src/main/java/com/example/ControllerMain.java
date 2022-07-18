package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ControllerMain extends HBox {

    @FXML
    private ImageView imageViewBack;

    @FXML
    private ImageView imageViewCue;

    @FXML
    private Label labelScoreText;

    @FXML
    private Label labelScoreValue;

    @FXML
    private Label labelShotsValue;

    @FXML
    private Label labelShotsText;

    private int score = 0;

    private static ControllerMain instance = null;

    private ControllerMain() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/main.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            imageViewBack.setImage(new Image(getClass().getResource("pictures/back.png").toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ControllerMain getInstance() {
        if (instance == null) {
            instance = new ControllerMain();
        }
        return instance;
    }

    public void setText(String text) {
        labelScoreText.textProperty().set(String.valueOf(text));
    }

    public void setScore(int newScore) {
        score = newScore;
        labelScoreValue.textProperty().set(String.valueOf(newScore));
    }

    public void setShotsValue(int value) {
        labelShotsValue.setText(String.valueOf(value));
        if(value > App.HITS_LIMIT){
            labelShotsText.setTextFill(Color.LIGHTCORAL);
            labelShotsValue.setTextFill(Color.LIGHTCORAL);
        }
    }

    @Deprecated
    public void setShotsColor(Color color){
        labelShotsText.setTextFill(color);
    }

    public void addShot() {
        labelShotsValue.setText((String.valueOf(Integer.parseInt(labelShotsValue.getText()) + 1)));
    }

    public int getScore() {
        return score;
    }

}
