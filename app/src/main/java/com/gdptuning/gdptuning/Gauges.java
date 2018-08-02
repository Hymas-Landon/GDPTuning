package com.gdptuning.gdptuning;

import android.animation.ValueAnimator;
import android.widget.TextView;

public class Gauges {

    public void countUp(int speed, final TextView speedCount) {
        ValueAnimator animator = ValueAnimator.ofInt(0, speed);
        animator.setDuration(speed * 80);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                speedCount.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }
}
