package com.othello.game.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import javafx.animation.Animation;

public class Disc {
    private static int discCnt = 0;
    private int discID;
    private int x;
    private int y;
    private int upColor;

    public ModelInstance modelInstance;
    public AnimationController animationController;
    public static final float boardScale = 1.05f;
    public static final float zPosShifting = -8.5f * boardScale;
    public static final float xPosShifting = -1.5f * boardScale;
    public static final float yPosShifting = 0.12f;

    public Disc(int x, int y, int upColor, ModelInstance modelInstance, AnimationController animationController) {
        this.x = x;
        this.y = y;
        this.upColor = upColor;
        this.modelInstance = modelInstance;
        this.animationController = animationController;

        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        if (upColor == OthelloConstants.DiscType.WHITE)
            modelInstance.transform.rotate(new Vector3().set(1f, 0, 0), 180);
        modelInstance.calculateTransforms();
        discID = ++Disc.discCnt;
    }

    public Disc(int x, int y, ModelInstance modelInstance, AnimationController animationController) {
        this.x = x;
        this.y = y;
        this.upColor = OthelloConstants.DiscType.BLACK;
        this.modelInstance = modelInstance;
        this.animationController = animationController;
        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        modelInstance.calculateTransforms();
        discID = ++Disc.discCnt;
    }

    public static void rotate(Disc disc) {
        disc.modelInstance.transform = new Matrix4().setToRotation(1f, 0, 0, 180);
        disc.modelInstance.calculateTransforms();
    }

    public void rotate() {
        if(upColor == OthelloConstants.DiscType.BLACK)
            modelInstance.transform.rotate(new Vector3().set(1f, 0, 0), 180);
        else
            modelInstance.transform.rotate(new Vector3().set(1f, 0, 0), 0);
        modelInstance.calculateTransforms();
    }

    public static void setPosition(Disc disc, int x, int y) {
        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
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
