package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Vlastne tlacidlo s vlastnym fontom pisma
 * @author Vlastimil Brecka
 *
 */
public class MyButton extends Button {

	/**
	 * Konstruktor
	 * @param context Kontext
	 * @param attrs Atributy
	 */
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LABTOP__.ttf"));
	}

}
