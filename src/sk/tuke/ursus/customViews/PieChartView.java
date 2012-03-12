package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.view.View;

public class PieChartView extends View {

	private ShapeDrawable sd1;
	private ShapeDrawable sd2;
	private int offset = 0;
	private int size = 300;
	private float startingAngle1, startingAngle2, sweepAngle1, sweepAngle2;
	private ShapeDrawable sd3;
	private ShapeDrawable sd4;
	private int height = 200;
	private int width = 200;
	private int y;
	private int x;

	public PieChartView(Context context, float percentage) {
		super(context);

		calculateAngles(percentage);
		init();
	}

	private void init() {

		x = 0;
		y = 0;
		width = 200;
		height = 200;

		/*
		 * if(270 + angle >= 360){ angle2 = 10; }else{ angle2 = 270 + angle; }
		 */

		sd1 = new ShapeDrawable(new ArcShape(startingAngle1, sweepAngle1));
		sd1.getPaint().setColor(0xFF0101DF);
		sd1.setBounds(x + offset, y - offset, x + width + offset, y + height - offset);

		sd2 = new ShapeDrawable(new ArcShape(startingAngle2, sweepAngle2));
		sd2.getPaint().setColor(0xFF2E9AFE);
		sd2.setBounds(x, y, x + width, y + height);
	}

	protected void onDraw(Canvas canvas) {

	/*	int x = 20;
		int y = 20;
		while (y > 0) {
			sd1 = new ShapeDrawable(new ArcShape(startingAngle1, sweepAngle1));
			sd1.getPaint().setColor(0xFF0101DF);
			sd1.setBounds(x, y, x + width, y + height);

			sd2 = new ShapeDrawable(new ArcShape(startingAngle2, sweepAngle2));
			sd2.getPaint().setColor(0xFF2E9AFE);
			sd2.setBounds(x, y, x + width, y + height);
			sd1.draw(canvas);
			sd2.draw(canvas);
			y--;
			if(y%2==0){
			x--;
			}
			
		}
		Paint paint = new Paint();
		paint.setColor(0xFF000000);
		//paint.s
		canvas.drawCircle(50, 50, 50, paint);*/
		sd1.draw(canvas);
		sd2.draw(canvas);

	}

	private void calculateAngles(float percentage) {
		sweepAngle1 = (360 * percentage) / 100;
		sweepAngle2 = 360 - sweepAngle1;

		startingAngle1 = 270;
		startingAngle2 = startingAngle1 + sweepAngle1;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		this.setMeasuredDimension(size, size);
	}

}
