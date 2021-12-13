package com.othello.game.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

public class Pointer {
    private static int pointerCnt = 0;
    private int x;
    private int y;

    public ModelInstance modelInstance;
    public static final float boardScale = 0.91f;
    public static final float zPosShifting = -9f * boardScale;
    public static final float xPosShifting = -1f * boardScale;
    public static final float yPosShifting = 1.2f;

    public Pointer(int x, int y, ModelInstance modelInstance) {
        this.x = x;
        this.y = y;
        this.modelInstance = modelInstance;

        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        modelInstance.calculateTransforms();
        pointerCnt++;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
}
