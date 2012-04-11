package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView s vlastným fontom písma
 * @author Vlastimil Breèka
 *
 */
public class MyTextView extends TextView {

	/**
	 * Konštruktor
	 * @param context Kontext
	 * @param attrs Atribúty
	 */
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LABTOP__.ttf"));
	}

}
