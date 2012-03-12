package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.View;

public class RectangleView extends View {

	private ShapeDrawable sd;
	private Paint paint;
	private Rect rect;
	private int size = 40;

	public RectangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// sd.draw(canvas);
		canvas.drawRect(rect, paint);

	}

	private void init() {
		/*
		 * sd = new ShapeDrawable(new RectShape());
		 * sd.getPaint().setColor(0xFF2B5BE5); sd.setBounds(0,0,100,50);
		 */
		paint = new Paint();
		paint.setColor(0xFF2B5BE5);

		rect = new Rect(0, 0, size, size);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		this.setMeasuredDimension(size, size);
	}

}
