package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Vlastnı view, štvorec, odráka pri tlaèidle v resulte
 * @author Vlastimil Breèka
 *
 */
public class RectangleView extends View {

	/**
	 * Paint
	 */
	private Paint paint;
	
	/**
	 * Objekt obdånika pre vykreslenie
	 */
	private Rect rect;
	
	/**
	 * Ve¾kos strany
	 */
	private int size;

	
	/**
	 * Konštruktor, vypoèíta ve¾kos pre dané rozlíšenie
	 * @param context
	 * @param attrs
	 */
	public RectangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int displayWidth = display.getWidth();
		
		size = (int)((40 / 480f) * displayWidth);
		
		init();
	}

	/**
	 * Metóda onDraw
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(rect, paint);

	}

	/**
	 * Inicializácia, nastaví farbu a vytvorí sa objekt obdånika
	 */
	private void init() {
		paint = new Paint();
		paint.setColor(0xFF2B5BE5);
		rect = new Rect(0, 0, size, size);
	}

	/**
	 * Metóda onMeasure
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(size, size);
	}

}
