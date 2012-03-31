package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Vlastny TextView s vlastnym fontom pisma
 * @author Vlastimil Brecka
 *
 */
public class MyTextView extends TextView {

	/**
	 * Konstruktor
	 * @param context Kontext
	 * @param attrs Atributy
	 */
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LABTOP__.ttf"));
	}

}
