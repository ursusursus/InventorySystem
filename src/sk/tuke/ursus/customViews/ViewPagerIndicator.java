package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * ViewPagerIndicator, indikuje na ktorej strane sa ViewPager aktuálne nachádza
 * @author Vlastimil Brecka
 *
 */
public class ViewPagerIndicator extends View {
	
	/**
	 * Farba aktuálnej stránky
	 */
	private static final int SELECTED_COLOR = 0xFF424242;
	
	/**
	 * Farba neaktuálnej stránky
	 */
	private static final int DESELECTED_COLOR = 0xFFBDBDBD;

	/**
	 * Paint
	 */
	private Paint paint;
	
	/**
	 * Pole obdåžnikov
	 */
	private Rect[] array;
	
	/**
	 * Index aktuálnej stránky
	 */
	private int currentPageIndex;

	/**
	 * Šírka obdåžnika
	 */
	private int width;
	
	/**
	 * Výška obdåžnika
	 */
	private int height;
	
	/**
	 * Odstup medzi obdåžnikmi
	 */
	private int margin;
	
	/**
	 * Poèet obdåžnikov
	 */
	private int count;

	/**
	 * Konštruktor, vypoèíta rozmery pre dané rozlíšenie
	 * @param context Kontext
	 * @param attrs Atribúty
	 */
	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int displayHeight = display.getHeight();
		int displayWidth = display.getWidth();
		
		width = (int)((30 / 480f) * displayWidth);
		margin = (int)((10 / 480f) * displayWidth);
		height = (int)((5 / 800f) * displayHeight);
		
		paint = new Paint();
	}

	/**
	 * Vytvoria sa obdåžniky
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
	 * @param index Index aktuálnej stránky
	 */
	private void update(int index) {
		this.currentPageIndex = index;
		this.invalidate();
	}

	/**
	 * Metóda onDraw
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
	 * Metóda onMesaure
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension((width + margin) * count, height);
	}

	/**
	 * Nastaví viewPager
	 * @param pager ViewPager, na ktorý chceme da indikátor
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
