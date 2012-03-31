package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

/**
 * ViewPagerIndicator, indikuje na ktorej strane sa ViewPager aktualne nachadza
 * @author Vlastimil Brecka
 *
 */
public class ViewPagerIndicator extends View {
	
	/**
	 * Farba aktualnej stranky
	 */
	private static final int SELECTED_COLOR = 0xFF424242;
	
	/**
	 * Farba neaktualnej stranky
	 */
	private static final int DESELECTED_COLOR = 0xFFBDBDBD;

	/**
	 * Paint
	 */
	private Paint paint;
	
	/**
	 * Pole obdlznikov
	 */
	private Rect[] array;
	
	/**
	 * Index aktualnej stranky
	 */
	private int currentPageIndex;

	/**
	 * Sirka obdlznika
	 */
	private int width = 30;
	
	/**
	 * Vyska obdlznika
	 */
	private int height = 5;
	
	/**
	 * Odstup medzi obdlznikmi
	 */
	private int margin = 10;
	
	/**
	 * Pocet obdlznikov
	 */
	private int count;
	

	/**
	 * Konstruktor 
	 * @param context Kontext
	 * @param attrs Atributy
	 */
	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}

	/**
	 * Vytvoria sa obdlzniky
	 */
	private void initialize() {
		array = new Rect[count];
		for (int i = 0; i < count; i++) {
			array[i] = new Rect(i * (margin + width), 0, i * (margin + width) + width, height);
		}
		currentPageIndex = 0;
	}
	
	/**
	 * Obnovenie
	 * @param index Index aktualnej stranky
	 */
	private void update(int index) {
		this.currentPageIndex = index;
		this.invalidate();
	}

	/**
	 * Metoda onDraw
	 */
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

	/**
	 * Metoda onMesaure
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension((width + margin) * count, height);
	}

	/**
	 * Nastavi viewPager
	 * @param pager ViewPager na ktory chceme dat indikator
	 */
	public void setViewPager(ViewPager pager) {
		//this.pager = pager;
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
