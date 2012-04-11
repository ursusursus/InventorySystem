package sk.tuke.ursus.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Tlaèidlo s vlastným fontom pisma
 * @author Vlastimil Breèka
 *
 */
public class MyButton extends Button {

	/**
	 * Konštruktor
	 * @param context Kontext
	 * @param attrs Atribúty
	 */
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LABTOP__.ttf"));
	}

}
