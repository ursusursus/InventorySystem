package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

public class ViewPagerIndicator extends View {

	private Paint paint = new Paint();
	private int size = 340;
	private Rect[] array;
	private int currentPageIndex;

	private int width = 30;
	private int height = 5;
	private int margin = 10;
	private int count;
	private ViewPager pager;

	private static final int SELECTED_COLOR = 0xFF424242;
	private static final int DESELECTED_COLOR = 0xFFBDBDBD;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		// this.count = 2;
		// initialize(count);

	}

	private void initialize() {
		array = new Rect[count];
		for (int i = 0; i < count; i++) {
			array[i] = new Rect(i * (margin + width), 0, i * (margin + width) + width, height);
		}
		currentPageIndex = 0;
	}
	
	private void update(int index) {
		this.currentPageIndex = index;
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int i = 0; i < count; i++) {
			if (i == currentPageIndex) {
				paint.setColor(SELECTED_COLOR);
			} else {
				paint.setColor(DESELECTED_COLOR);
			}
			canvas.drawRect(array[i], paint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension((width + margin) * count, height);
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;
		count = pager.getAdapter().getCount();

		initialize();

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				update(index);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}
