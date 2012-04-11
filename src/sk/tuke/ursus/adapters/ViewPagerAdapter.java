package sk.tuke.ursus.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Adaptér ViewPageru
 * @author Vlastimil Breèka
 *
 */
public class ViewPagerAdapter extends PagerAdapter {
	
	/**
	 * Zoznam položiek
	 */
	private ArrayList<LinearLayout> viewsList;

	
	/**
	 * Konštruktor
	 * @param context Kontext
	 * @param viewsList ID .xml resource
	 */
	public ViewPagerAdapter(Context context, ArrayList<LinearLayout> viewsList) {
		this.viewsList = viewsList;

	}

	/**
	 * Metóda destroyItem
	 */
	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((LinearLayout) object);
	}

	/**
	 * Metóda finishUpdate
	 */
	@Override
	public void finishUpdate(View arg0) {

	}

	/**
	 * Vráti poèet prvkov
	 */
	@Override
	public int getCount() {
		return viewsList.size();
	}

	/**
	 * Metóda instatiateItem
	 */
	@Override
	public Object instantiateItem(View view, int position) {
		View myView = viewsList.get(position);
		((ViewPager) view).addView(myView);
		return myView;
	}

	/**
	 * Metóda isViewFromObject
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	/**
	 * Metóda restoreState
	 */
	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	/**
	 * Metóda saveState
	 */
	@Override
	public Parcelable saveState() {
		return null;
	}

	/**
	 * Metóda startUpdate
	 */
	@Override
	public void startUpdate(View arg0) {

	}
}
