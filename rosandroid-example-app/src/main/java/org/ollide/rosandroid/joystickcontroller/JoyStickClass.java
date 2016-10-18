package org.ollide.rosandroid.joystickcontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class JoyStickClass {

	private float maxdst_scalar = 0.7f;
	private int STICK_ALPHA = 200;
	private int LAYOUT_ALPHA = 200;

	private Context mContext;
	private ViewGroup mLayout;
	private LayoutParams params;
	private int stick_width, stick_height;
	
	private float position_x = 0, position_y = 0;
	private float distance = 0, angle = 0;
	
	private DrawCanvas draw;
	private Paint paint;
	private Bitmap stick;
	
	private boolean x_move = true, y_move = true;
	
	public JoyStickClass (Context context, ViewGroup layout, int stick_res_id) {
		mContext = context;

		stick = BitmapFactory.decodeResource(mContext.getResources(),
				stick_res_id);
		
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(mContext);
        paint = new Paint();
		mLayout = layout;
		params = mLayout.getLayoutParams();
	}

	public void update(MotionEvent arg1) {
		if(arg1 != null && arg1.getAction() == MotionEvent.ACTION_UP) {
			arg1 = null;
		}

		if(x_move && arg1 != null) position_x = arg1.getX(); else position_x = params.width/2;
		if(y_move && arg1 != null) position_y = arg1.getY(); else position_y = params.height/2;

		distance = (float) Math.sqrt(
				Math.pow(position_x - params.width/2, 2) + Math.pow(position_y - params.height/2, 2));
		if(distance > params.width/2*maxdst_scalar)
			distance = params.width/2*maxdst_scalar;

		if(distance > 5) {
			angle = (float) cal_angle(position_x - params.width / 2, position_y - params.width / 2);

			position_x = (float) (Math.cos(Math.toRadians(angle)) * distance) + params.width / 2;
			position_y = (float) (Math.sin(Math.toRadians(angle)) * distance) + params.height / 2;
		}

		draw.position(position_x, position_y);

		try {
			mLayout.removeView(draw);
		} catch (Exception e) { }
		mLayout.addView(draw);
	}

	public int getX() {
		return (int) position_x - params.width/2;
	}
	
	public int getY() {
		return (int) position_y - params.height/2;
	}

	public int getXvalue(int bound) {
		return (int) (getX() / (params.width/2*maxdst_scalar)*bound);
	}

	public int getYvalue(int bound) {
		return (int) (getY() / (params.width/2*maxdst_scalar)*bound);
	}
	
	public float getAngle() {
		return angle;
	}
	
	public float getDistance() {
		return distance;
	}

	public void setStickAlpha(int alpha) {
		STICK_ALPHA = alpha;
		paint.setAlpha(alpha);
	}
	
	public int getStickAlpha() {
		return STICK_ALPHA;
	}
	
	public void setLayoutAlpha(int alpha) {
		LAYOUT_ALPHA = alpha;
		mLayout.getBackground().setAlpha(alpha);
	}
	
	public int getLayoutAlpha() {
		return LAYOUT_ALPHA;
	}
	
	public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
	}
	
	public void setStickWidth(int width) {
        stick = Bitmap.createScaledBitmap(stick, width, stick_height, false);
        stick_width = stick.getWidth();
	}
	
	public void setStickHeight(int height) {
        stick = Bitmap.createScaledBitmap(stick, stick_width, height, false);
        stick_height = stick.getHeight();
	}
	
	public int getStickWidth() {
		return stick_width;
	}
	
	public int getStickHeight() {
		return stick_height;
	}
	
	public void setLayoutSize(int width, int height) {
		params.width = width;
		params.height = height;
	}

	public int getLayoutWidth() {
		return params.width;
	}

	public int getLayoutHeight() {
		return params.height;
	}
	
	private double cal_angle(float x, float y) {
		if(x >= 0 && y >= 0)
			return Math.toDegrees(Math.atan(y / x));
	    else if(x < 0 && y >= 0)
	    	return Math.toDegrees(Math.atan(y / x)) + 180;
	    else if(x < 0 && y < 0)
	    	return Math.toDegrees(Math.atan(y / x)) + 180;
	    else if(x >= 0 && y < 0) 
	    	return Math.toDegrees(Math.atan(y / x)) + 360;
		return 0;
	}

	public void setPossibleMove(boolean x, boolean y) {
		x_move = x;
		y_move = y;
	}
	 
	private class DrawCanvas extends View{
	 	float x, y;
	 	
	 	private DrawCanvas(Context mContext) {
	         super(mContext);
	     }
	     
	     public void onDraw(Canvas canvas) {
	         canvas.drawBitmap(stick, x, y, paint);
	     }
	     
	     private void position(float pos_x, float pos_y) {
	     	x = pos_x - (stick_width / 2);
	     	y = pos_y - (stick_height / 2);
	     }
	 }
}
