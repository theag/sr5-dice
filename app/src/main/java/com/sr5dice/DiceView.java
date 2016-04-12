package com.sr5dice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nbp184 on 2016/04/12.
 */
public class DiceView extends View {

    private static final float DOT_RADIUS_DP = 3f;
    private static final float DICE_SIZE_DP = DOT_RADIUS_DP * 10;

    private DiceRoll diceRoll;
    private float dpToPx;

    public DiceView(Context context) {
        super(context);
        init();
    }

    public DiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        diceRoll = null;
        dpToPx = getContext().getResources().getDisplayMetrics().densityDpi/160f;
    }

    public void setDiceRoll(DiceRoll diceRoll) {
        this.diceRoll = diceRoll;
        invalidate();
    }

    public void update() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(diceRoll != null) {
            canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            canvas.translate(getPaddingLeft(), getPaddingTop());
            int width = getWidth() - getPaddingLeft() - getPaddingRight();
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            Paint normal = new Paint();
            Paint hit = new Paint();
            Paint exploding = new Paint();
            Paint secondChance = new Paint();
            secondChance.setStrokeWidth(2*dpToPx);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                normal.setColor(getContext().getColor(R.color.normal));
                hit.setColor(getContext().getColor(R.color.hit));
                exploding.setColor(getContext().getColor(R.color.exploding));
                secondChance.setColor(getContext().getColor(R.color.secondChance));
            } else {
                normal.setColor(getResources().getColor(R.color.normal));
                hit.setColor(getResources().getColor(R.color.hit));
                exploding.setColor(getResources().getColor(R.color.exploding));
                secondChance.setColor(getResources().getColor(R.color.secondChance));
            }
            float dice_size = DICE_SIZE_DP * dpToPx;
            float dot_radius = DOT_RADIUS_DP * dpToPx;
            float x = 0;
            float y = 0;
            int roll;
            Paint using;
            for(int i = 0; i < diceRoll.rollCount(); i++) {
                roll = diceRoll.getRoll(i);
                if(diceRoll.isLimitPushed(i) && roll == 6) {
                    using = exploding;
                } else if(roll >= 5) {
                    using = hit;
                } else {
                    using = normal;
                }
                using.setStyle(Paint.Style.STROKE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(x, y, x + dice_size, y + dice_size, dot_radius, dot_radius, using);
                } else {
                    canvas.drawRect(x, y, x + dice_size, y + dice_size, using);
                }
                drawDice(canvas, roll, x, y, using);
                x += dice_size + 2*dot_radius;
                if(x+dice_size > width) {
                    x = 0;
                    y += dice_size + dot_radius;
                }
            }
            if(diceRoll.isLimitPushed()) {
                for (int i = 0; i < diceRoll.explodedRollCount(); i++) {
                    if (x > 0) {
                        x = 0;
                        y += dice_size + dot_radius;
                    }
                    y += dot_radius;
                    exploding.setStrokeWidth(2*dpToPx);
                    canvas.drawLine(x, y, x + width, y, exploding);
                    exploding.setStrokeWidth(dpToPx);
                    y += 2 * dpToPx + dot_radius;
                    for (int j = 0; j < diceRoll.explodedRollCount(i); j++) {
                        roll = diceRoll.getExplodedRoll(i, j);
                        if(diceRoll.isLimitPushed() && roll == 6) {
                            using = exploding;
                        } else if(roll >= 5) {
                            using = hit;
                        } else {
                            using = normal;
                        }
                        using.setStyle(Paint.Style.STROKE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            canvas.drawRoundRect(x, y, x + dice_size, y + dice_size, dot_radius, dot_radius, using);
                        } else {
                            canvas.drawRect(x, y, x + dice_size, y + dice_size, using);
                        }
                        drawDice(canvas, roll, x, y, using);
                        x += dice_size + 2 * dot_radius;
                        if (x + dice_size > width) {
                            x = 0;
                            y += dice_size + dot_radius;
                        }
                    }
                }
            }
            if(diceRoll.isSecondChanced()) {
                if (x > 0) {
                    x = 0;
                    y += dice_size + dot_radius;
                }
                y += dot_radius;
                canvas.drawLine(x, y, x + width, y, secondChance);
                y += 2 * dpToPx + dot_radius;
                for (int i = 0; i < diceRoll.secondChanceCount(); i++) {
                    roll = diceRoll.getSecondChanceRoll(i);
                    if(diceRoll.isLimitPushed() && roll == 6) {
                        using = exploding;
                    } else if(roll >= 5) {
                        using = hit;
                    } else {
                        using = normal;
                    }
                    using.setStyle(Paint.Style.STROKE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        canvas.drawRoundRect(x, y, x + dice_size, y + dice_size, dot_radius, dot_radius, using);
                    } else {
                        canvas.drawRect(x, y, x + dice_size, y + dice_size, using);
                    }
                    drawDice(canvas, roll, x, y, using);
                    x += dice_size + 2 * dot_radius;
                    if (x + dice_size > width) {
                        x = 0;
                        y += dice_size + dot_radius;
                    }
                }
            }
        }
    }

    private void drawDice(Canvas canvas, int roll, float x, float y, Paint using) {
        float dot_radius = DOT_RADIUS_DP * dpToPx;
        using.setStyle(Paint.Style.FILL_AND_STROKE);
        switch(roll) {
            case 1:
                canvas.drawCircle(x + 5*dot_radius, y + 5*dot_radius, dot_radius, using);
                break;
            case 2:
                canvas.drawCircle(x + 2*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 8*dot_radius, dot_radius, using);
                break;
            case 3:
                canvas.drawCircle(x + 2*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 5*dot_radius, y + 5*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 8*dot_radius, dot_radius, using);
                break;
            case 4:
                canvas.drawCircle(x + 2*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 2*dot_radius, y + 8*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 8*dot_radius, dot_radius, using);
                break;
            case 5:
                canvas.drawCircle(x + 2*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 2*dot_radius, y + 8*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 8*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 5*dot_radius, y + 5*dot_radius, dot_radius, using);
                break;
            case 6:
                canvas.drawCircle(x + 2*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 2*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 2*dot_radius, y + 5*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 5*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 2*dot_radius, y + 8*dot_radius, dot_radius, using);
                canvas.drawCircle(x + 8*dot_radius, y + 8*dot_radius, dot_radius, using);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = ceil(DICE_SIZE_DP * dpToPx * 2);
            if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST && height > MeasureSpec.getSize(heightMeasureSpec)) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            }
            setMeasuredDimension(width, height);
        }*/
    }

    private int floor(float v) {
        int rv;
        if(v >= 0) {
            for(rv = 0; rv <= v; rv++);
            rv--;
        } else {
            for(rv = 0; rv > v; rv--);
        }
        return rv;
    }

    public static int ceil(float v) {
        int rv;
        if(v >= 0) {
            for(rv = 0; rv < v; rv++);
        } else {
            for(rv = 0; rv >= v; rv--);
            rv++;
        }
        return rv;
    }

}
