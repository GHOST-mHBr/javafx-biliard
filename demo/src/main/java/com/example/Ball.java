package com.example;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Ball extends StackPane {

    public static final int DEFAULT_SCORE = 15;
    private static final float DEFAULT_ACCEL = -1f;
    public static final int DEFAULT_RADIUS = 30;

    private static final Font textFont = Font.font("Arial", FontWeight.BOLD, 30);

    private Vector2D speed = new Vector2D();

    private final Color color;
    private final String text;
    private final int indexText;
    private final Pane pane;
    // private Vector2D center = new Vector2D();

    private final void setText() {
        if (!text.equals("")) {
            Text mText = new Text(text);
            mText.setFill(Color.WHITE);
            // mText.setStroke(Color.BLACK);
            // mText.setStrokeType(StrokeType.OUTSIDE);
            // mText.setStrokeWidth(1);
            mText.setFont(textFont);
            pane.getChildren().add(mText);
        }

    }

    private final void setCircle() {
        Circle circle = new Circle(DEFAULT_RADIUS / 2, DEFAULT_RADIUS / 2, DEFAULT_RADIUS, color);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(1.5);
        circle.setStrokeType(StrokeType.INSIDE);
        pane.getChildren().add(circle);
    }

    public Ball(String text, Color color) {

        if (!text.equals("")) {
            int index = Integer.parseInt(text);
            indexText = index;
            this.text = String.valueOf(index);
        } else {
            this.text = text;
            indexText = 0;
        }

        this.color = color;

        pane = new StackPane();
        // the order is important in the following lines. because the circle places
        // behind text
        setCircle();
        setText();
    }

    public Node getNode() {
        return pane;
    }

    public Vector2D getCenter() {
        return new Vector2D(getCenterX(), getCenterY());
    }

    public void setCenterX(float x) {
        pane.setLayoutX(x-DEFAULT_RADIUS);
    }

    public void setCenterY(float y) {
        pane.setLayoutY(y-DEFAULT_RADIUS);
    }

    public void setCenterX(double x) {
        pane.setLayoutX(x-DEFAULT_RADIUS);
    }

    public void setCenterY(double y) {
        pane.setLayoutY(y-DEFAULT_RADIUS);
    }

    public void setCenterXY(float value) {
        setCenterX(value);
        setCenterY(value);
    }

    public void setCenter(Vector2D newCenter) {
        setCenterX(newCenter.getX());
        setCenterY(newCenter.getY());
    }

    public float getCenterX() {
        return (float) pane.getLayoutX() + DEFAULT_RADIUS;
    }

    public float getCenterY() {
        return (float) pane.getLayoutY() + DEFAULT_RADIUS;
    }

    public int getScore() {
        return indexText * DEFAULT_SCORE;
    }

    public Color getColor() {
        return color;
    }

    public Vector2D getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2D newSpeed) {
        speed = newSpeed;
    }

    public void move() {
        if (speed.getLength() > 0.1f) {
            // center = center.plus(speed);
            speed = speed.multiplyIn(0.95f);
            pane.setLayoutX(pane.getLayoutX() + speed.getX());
            pane.setLayoutY(pane.getLayoutY() + speed.getY());

            // pane.setLayoutY(center.getY() + speed.getY() - DEFAULT_RADIUS);
            // pane.relocate(center.getX(), center.getY());
        }
        Rectangle t = App.YARD_RECT;
        
        if (pane.getLayoutX() + 2 * DEFAULT_RADIUS > t.getX() + t.getWidth()) {
            speed.setX(speed.getX() * -1.0f);
            setCenterX(t.getWidth() + t.getX() - DEFAULT_RADIUS);
        }
        if (pane.getLayoutX() < t.getX()) {
            speed.setX(speed.getX() * -1.0f);
            setCenterX(t.getX()+DEFAULT_RADIUS);
        }
        if (pane.getLayoutY() < t.getY()) {
            speed.setY(speed.getY() * -1.0f);
            setCenterY(DEFAULT_RADIUS+t.getY());
        }
        if (pane.getLayoutY() + 2*DEFAULT_RADIUS > t.getY() + t.getHeight()) {
            speed.setY(speed.getY() * -1.0f);
            setCenterY(t.getY()+t.getHeight()-DEFAULT_RADIUS);
        }
    }

}