package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;

public class ReloadAnimation {
    float timeToPlay, animationTime;
    float oneRoundTime = 2.2f;
    Interpolation interpolation;
    float animationProgress;
    float stopTime = 0f, curStopTime;
    float alpha;
    public ReloadAnimation(){
        interpolation = Interpolation.linear;
    }
    public void start(float time){
        timeToPlay = time;
        animationTime = timeToPlay;
        animationProgress = 0.5f;
        curStopTime = 0;
    }
    public void stop(){
        animationTime = 0;
    }

    public boolean isFinished(){
        return animationTime <= 0;
    }

    public void updateAndTransform(float delta, Sprite sprite){
        if (animationTime <= 0) {
            return;
        }
        animationTime -= delta;
        if (curStopTime > 0){
            curStopTime -= delta;
            return;
        }
        animationProgress += delta/oneRoundTime;
        animationProgress = Math.min(1, animationProgress);
        alpha = interpolation.apply(animationProgress);

        sprite.setRotation(360 * (alpha - 0.5f));

        if (animationProgress >= 1) {
            curStopTime = stopTime;
            animationProgress = 0;
        }
    }

    class MyInterpolation extends Interpolation {
        @Override
        public float apply(float t) {
            return (float) Math.sin(((t/1.4f + 0.46) * 3.72))/2 - 0.5f + 1.97f*t;
        }
    }
}
