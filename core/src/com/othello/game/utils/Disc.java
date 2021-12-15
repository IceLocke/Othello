package com.othello.game.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.othello.game.Othello;

public class Disc {
    private static int discCnt = 0;
    private int discID;
    private int x;
    private int y;
    private int upColor;
    private int rotateTimes;
    public boolean animationIsOver;

    public ModelInstance modelInstance;
    public AnimationController animationController;
    public static final float boardScale = 1.05f;
    public static final float zPosShifting = -8.5f * boardScale;
    public static final float xPosShifting = -1.5f * boardScale;
    public static final float yPosShifting = 0.2f;

    public Disc(int x, int y, int upColor, ModelInstance modelInstance, AnimationController animationController) {
        this.x = x;
        this.y = y;
        this.upColor = upColor;
        this.modelInstance = modelInstance;
        this.animationController = animationController;
        rotateTimes = 0;
        animationIsOver = true;

        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
        modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        if (upColor == OthelloConstants.DiscType.BLACK) {
            modelInstance.transform.rotate(new Vector3().set(1f, 0, 0), 180);
        }
        modelInstance.calculateTransforms();
        discID = ++Disc.discCnt;
    }

    public void rotate() {
        animationIsOver = false;
        rotateTimes++;
        if (upColor == OthelloConstants.DiscType.BLACK) {
            animationController.setAnimation("disc|BlackToWhite", new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    animationIsOver = true;
                    Othello.chessSound2.play(0.1f);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {

                }
            });
            if (rotateTimes == 1)
                modelInstance.transform.rotate(new Vector3().set(1f, 0, 0), 180);
            modelInstance.calculateTransforms();
        }
        else {
            animationController.setAnimation("disc|WhiteToBlack", new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    animationIsOver = true;
                    if (!Othello.isMuted)
                        Othello.chessSound2.play(0.1f);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {
                }
            });
            modelInstance.calculateTransforms();
        }
        upColor = -upColor;
    }

    public static void setPosition(Disc disc, int x, int y) {
        float zPos = zPosShifting + x * boardScale;
        float xPos = xPosShifting + y * boardScale;
        float yPos = yPosShifting;
        disc.modelInstance.transform = new Matrix4().setToTranslation(xPos, yPos, zPos);
        disc.modelInstance.calculateTransforms();
    }

    public int getRotateTimes() {
        return rotateTimes;
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
