package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class RectangleView extends View {

	private Paint paint;
	private Rect rect;
	private int size = 40;

	public RectangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		//Size sa musi pocitat z velkosti okna ... asi, ak bude cas
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(rect, paint);

	}

	private void init() {
		paint = new Paint();
		paint.setColor(0xFF2B5BE5);
		rect = new Rect(0, 0, size, size);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(size, size);
	}

}
