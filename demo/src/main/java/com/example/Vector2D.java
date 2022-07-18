package com.example;

import java.util.Vector;

public class Vector2D {
    private float x = 0f;
    private float y = 0f;

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other instanceof Vector2D) {
            Vector2D casted = (Vector2D) other;
            Vector2D distVector = new Vector2D(casted.getX() - x, casted.getY() - y);
            if (distVector.getLength() < 40) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public Vector2D() {
    }

    public Vector2D(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2D(float value) {
        x = value;
        y = value;
    }

    public Vector2D(Vector2D other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D multiplyIn(float number) {
        return new Vector2D(x * number, y * number);
    }

    public Vector2D divideBy(float number) {
        Vector2D res = new Vector2D();
        if (Math.abs(number) > 0) {
            res.setX(x / number);
            res.setY(y / number);
        }
        return res;
    }

    public Vector2D plus(Vector2D other) {
        Vector2D res = new Vector2D();
        res.setX(x + other.getX());
        res.setY(other.getY() + y);
        return res;
    }

    public Vector2D minus(Vector2D other) {
        return new Vector2D(x - other.getX(), y - other.getY());
    }

    public void setXY(float value) {
        x = value;
        y = value;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLength() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public double getAngleWith(Vector2D other) {
        if (getLength() == 0 || other.getLength() == 0) {
            return 0;
        }
        return Math.acos(this.dot(other) / (getLength() * other.getLength()));
    }

    public float dot(Vector2D other) {
        return (x * other.getX() + y * other.getY());
    }
}
