package com.example;

import com.example.App.GameState;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameOver {
    @FXML
    Button quitButton;


    
    @FXML
    private void initialize(){
        quitButton.setOnMouseClicked(e -> App.setState(GameState.END));
        // againButton.setOnMouseClicked(e -> App.setState(App.GameState.PLAYING));
    }
}
