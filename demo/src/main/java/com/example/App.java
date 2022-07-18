package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 * Written by GHOST mHBr just for university:(
 */
public class App extends Application {

    // ---Game state enum---
    public static enum GameState {
        PLAYING, LOCKED, GAME_OVER, WIN, END
    }

    // -----Constants------
    public static final int HITS_LIMIT = 10;
    public static final Rectangle YARD_RECT = new Rectangle(240, 40, 1020, 720);
    private static final Color[] ballsColors = { Color.RED, Color.CORNFLOWERBLUE, Color.BURLYWOOD, Color.BLUEVIOLET,
            Color.DARKKHAKI,
            Color.PURPLE, Color.CADETBLUE, Color.BLACK, Color.ORANGE, Color.INDIGO };
    private static final ArrayList<Vector2D> holes = new ArrayList<>();
    private static final Vector2D WHITE_BALL_CENTER = new Vector2D(950, 400);
    private static final Cue cue = Cue.getInstance();

    // -----Game Variables------
    private static GameState state = GameState.PLAYING;
    private static int hitsCount = 0;
    private static ControllerMain cntrlMain = ControllerMain.getInstance();

    // -------Scenes--------
    private Scene scene;
    private Scene sceneGameOver;
    private Scene sceneEnd;

    private Pane pane = new Pane();
    private Stage stage;

    // -------Balls---------
    private ArrayList<Ball> coloredBalls = new ArrayList<>();
    private ArrayList<Ball> allBalls = new ArrayList<>();

    private Ball whiteBall;
    // private static Ball blackBall;

    private Vector2D mouseCoordinates = new Vector2D();

    @Override
    public void start(Stage stage) throws IOException {
        calcHoles();
        pane.getChildren().add(cntrlMain);
        createBalls();
        initCue();
        initScenes();
        registerEvents();
        registerTimer();

        stage.setScene(scene);
        stage.show();
        this.stage = stage;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxmls/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void updateBalls() {
        moveBalls();

        for (Ball ball1 : allBalls) {
            for (Ball ball2 : allBalls) {
                if (ball1 == ball2) {
                    continue;
                }
                if (checkBallCollision(ball1, ball2)) {
                    Vector2D centersVect = ball1.getCenter().minus(ball2.getCenter());
                    float v1 = Math.abs(ball1.getSpeed().getLength()) > 0 ? ball1.getSpeed().getLength() : 1;
                    // v1*=-1;

                    Vector2D ball1CenterSpeed = centersVect.divideBy(centersVect.getLength())
                            .multiplyIn(centersVect.dot(ball1.getSpeed()) / centersVect.getLength() / v1)
                            .multiplyIn(ball1.getSpeed().getLength());

                    v1 = Math.abs(ball2.getSpeed().getLength()) > 0.01f ? ball2.getSpeed().getLength() : 1;
                    // v1*=-1f;

                    Vector2D ball2CenterSpeed = centersVect.divideBy(centersVect.getLength()).multiplyIn(
                            centersVect.dot(ball2.getSpeed()) / centersVect.getLength() / v1)
                            .multiplyIn(ball2.getSpeed().getLength());

                    Vector2D ball1NormalSpeed = ball1.getSpeed().minus(ball1CenterSpeed);
                    Vector2D ball2NormalSpeed = ball2.getSpeed().minus(ball2CenterSpeed);

                    ball1.setSpeed(ball1NormalSpeed.plus(ball2CenterSpeed));
                    // ball1.setSpeed(ball1NormalSpeed.plus(ball2CenterSpeed).divideBy(5f));
                    // ball2.setSpeed(ball2NormalSpeed.plus(ball1CenterSpeed).divideBy(5f));
                    ball2.setSpeed(ball2NormalSpeed.plus(ball1CenterSpeed));
                    while (ball1.getCenter().minus(ball2.getCenter()).getLength() < 2 * Ball.DEFAULT_RADIUS) {
                        if (ball1.getSpeed().getLength() + ball2.getSpeed().getLength() > 0.2f) {
                            ball1.move();
                            ball2.move();
                        } else {
                            Vector2D dist = ball1.getCenter().minus(ball2.getCenter());
                            dist = dist.divideBy(dist.getLength());

                            ball1.setCenter(ball1.getCenter().plus(dist));
                            ball2.setCenter(ball2.getCenter().minus(dist));
                            // ball1.setSpeed(ball1.getCenter().minus(ball2.getCenter()).divideBy(15));
                            // ball2.setSpeed(ball2.getCenter().minus(ball1.getCenter()).divideBy(15));
                            // ball1.setCenter(ball1.getCenter().plus(new Vector2D(1,0)));
                        }
                    }
                }
            }
        }

    }

    private boolean checkBallCollision(Ball ball1, Ball ball2) {
        return (ball1.getCenter().minus(ball2.getCenter()).getLength() < Ball.DEFAULT_RADIUS * 2 - 1);
    }

    private void moveBalls() {
        ArrayList<Ball> toRemoveList = new ArrayList<>();

        for (Ball ball : allBalls) {
            ball.move();
            if (checkBallVsHoles(ball)) {
                int oldScore = cntrlMain.getScore();
                cntrlMain.setScore(oldScore + ball.getScore());
                if (ball.equals(whiteBall)) {
                    state = GameState.GAME_OVER;

                    whiteBall = null;
                }
                // To suppress ConcurrentModificationException
                toRemoveList.add(ball);
            }
        }
        toRemoveList.forEach(e -> pane.getChildren().remove(e.getNode()));
        allBalls.removeAll(toRemoveList);
    }

    private boolean checkBallVsHoles(Ball ball) {
        for (Vector2D hole : holes) {
            // Ball.DEFAULT_RADIUS is only a number and may change over testing
            if (ball.getCenter().minus(hole).getLength() < 2 * Ball.DEFAULT_RADIUS) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        launch();
    }

    private void registerTimer() {

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                updateBalls();
                switch (state) {
                    case PLAYING:
                        if (!stage.getScene().equals(scene))
                            stage.setScene(scene);
                        break;

                    case END:
                        if (!stage.getScene().equals(sceneEnd)) {
                            stage.setScene(sceneEnd);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Platform.exit();
                                }

                            }.start();
                        }
                        break;

                    case GAME_OVER:
                        stage.setScene(sceneGameOver);
                        break;

                    case WIN:
                        break;
                }
            }

        };
        timer.start();
    }

    private void registerEvents() {
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                if (state == GameState.PLAYING) {
                    mouseCoordinates.setX((float) e.getX());
                    mouseCoordinates.setY((float) e.getY());
                    Vector2D speed = new Vector2D(mouseCoordinates.getX() - whiteBall.getCenterX(),
                            mouseCoordinates.getY() - whiteBall.getCenterY());
                    speed = speed.divideBy(-3);
                    trigger(speed);
                }
            }
        });

        pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                if (state == GameState.PLAYING) {
                    mouseCoordinates.setX((float) e.getX());
                    mouseCoordinates.setY((float) e.getY());
                    double angle = Math.atan2(mouseCoordinates.getY() - whiteBall.getCenterY(),
                            mouseCoordinates.getX() - whiteBall.getCenterX());
                    angle *= (180 / Math.PI);
                    cue.getTransforms().clear();
                    cue.getTransforms().add(new Rotate(angle, 0, Cue.HEIGHT / 2));
                }
            }
        });
    }

    private void initScenes() {
        scene = new Scene(pane, 1300, 800);

        try {
            sceneGameOver = new Scene(loadFXML("game_over"));
            sceneEnd = new Scene(loadFXML("end"));
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    private void initCue() {
        cue.setTipPlace(new Vector2D(950, 400));
        pane.getChildren().add(cue.getNode());
    }

    public static void setState(GameState newState) {
        // if(state!=newState && newState == GameState.PLAYING){}

        state = newState;

    }

    public void createBalls() {
        final int yOffset = 400 - 4 * Ball.DEFAULT_RADIUS;
        final int xOffset = 440;
        int index = 0;
        for (float i = 0; i < 4; i++) {
            for (float j = i + 1; j < 8 - i; j += 2) {
                float x = (1 + i * 1.732f) * Ball.DEFAULT_RADIUS;
                float y = j * Ball.DEFAULT_RADIUS;
                Ball ball = new Ball(String.valueOf(index + 1), ballsColors[index]);
                ball.setCenter(new Vector2D(x + xOffset, y + yOffset));
                coloredBalls.add(ball);
                index++;
            }
        }
        whiteBall = new Ball("", Color.WHITE);
        whiteBall.setCenter(WHITE_BALL_CENTER);
        allBalls.addAll(coloredBalls);
        allBalls.add(whiteBall);
        allBalls.forEach(e -> pane.getChildren().add(e.getNode()));
        // pane.getChildren().remove(ControllerMain.getInstance());
        // allBalls.clear();

        // pane.getChildren().add(whiteBall.getNode());
    }

    private void calcHoles() {
        for (double i = YARD_RECT.getX(); i <= YARD_RECT.getX() + YARD_RECT.getWidth(); i += YARD_RECT.getWidth() / 2) {
            for (double j = YARD_RECT.getY(); j <= YARD_RECT.getY() + YARD_RECT.getHeight(); j += YARD_RECT.getHeight()
                    / 2) {
                holes.add(new Vector2D(i, j));
            }
        }
        holes.remove(4);
    }

    public void trigger(Vector2D speed) {
        whiteBall.setSpeed(speed);
        state = GameState.LOCKED;
        if (++hitsCount > HITS_LIMIT) {
            cntrlMain.setScore(cntrlMain.getScore() - coloredBalls.get(coloredBalls.size() - 1).getScore() / 2);
        }
        if (cntrlMain.getScore() < 0) {
            state = GameState.GAME_OVER;
        }

        cntrlMain.setShotsValue(hitsCount);

        new Thread() {
            public void run() {
                boolean result = true;
                while (result) {

                    synchronized (allBalls) {
                        result = false;
                        for (Ball ball : allBalls) {
                            if (ball.getSpeed().getLength() > 0.2f) {
                                result = true;
                                break;
                            }
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    cue.setTipPlace(whiteBall.getCenter());
                } catch (Exception e) {
                    cue.setTipPlace(new Vector2D(400, 950));
                }
                if (state == GameState.LOCKED)
                    setState(App.GameState.PLAYING);
            }
        }.start();
    }
}