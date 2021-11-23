package com.othello.game.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import javafx.animation.Animation;

public class Disc {
    private static int discCnt = 0;
    private int discID;
    private int x;
    private int y;
    private int upColor;
    public ModelInstance modelInstance;
    public AnimationController animationController;

    public Disc(int x, int y, int upColor, ModelInstance modelInstance, AnimationController animationController) {
        this.x = x;
        this.y = y;
        this.upColor = upColor;
        if (upColor == OthelloConstants.DiscType.WHITE) {
            modelInstance.transform = new Matrix4().setToRotation(1f, 0, 0, 180);
            modelInstance.calculateTransforms();
        }
        this.modelInstance = modelInstance;
        this.animationController = animationController;
        float zPos = -8.5f + x;
        float xPos = y - 1.5f;
        float yPos = 0.05f;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        modelInstance.calculateTransforms();
        discID = ++Disc.discCnt;
    }

    public Disc(int x, int y, ModelInstance modelInstance, AnimationController animationController) {
        this.x = x;
        this.y = y;
        this.upColor = OthelloConstants.DiscType.BLACK;
        this.modelInstance = modelInstance;
        this.animationController = animationController;
        float zPos = -8.5f + x;
        float xPos = y - 1.5f;
        float yPos = 0.05f;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        modelInstance.calculateTransforms();
        discID = ++Disc.discCnt;
    }

    public static void rotateToWhite(Disc disc) {
        if (disc.getUpColor() != OthelloConstants.DiscType.WHITE) {
            disc.modelInstance.transform = new Matrix4().setToRotation(1f, 0, 0, 180);
            disc.modelInstance.calculateTransforms();
        }

    }

    public static void rotateToBlack(Disc disc) {
        if (disc.getUpColor() != OthelloConstants.DiscType.BLACK) {
            disc.modelInstance.transform = new Matrix4().setToRotation(1f, 0, 0, 180);
            disc.modelInstance.calculateTransforms();
        }
    }

    public static void rotate(Disc disc) {
        disc.modelInstance.transform = new Matrix4().setToRotation(1f, 0, 0, 180);
        disc.modelInstance.calculateTransforms();
    }

    public static void setPosition(Disc disc, int x, int y) {
        float zPos = -8.5f + x;
        float xPos = y - 1.5f;
        float yPos = 0.05f;
        disc.modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        disc.modelInstance.calculateTransforms();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getDiscCnt() {
        return discCnt;
    }

    public int getDiscID() {
        return discID;
    }

    public int getUpColor() {
        return upColor;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public void resetDiscCnt() {
        Disc.discCnt = 0;
    }
}
