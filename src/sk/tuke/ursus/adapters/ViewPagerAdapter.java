package sk.tuke.ursus.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Adapter ViewPageru
 * @author Vlastimil Brecka
 *
 */
public class ViewPagerAdapter extends PagerAdapter {
	
	/**
	 * Zoznam poloziek
	 */
	private ArrayList<LinearLayout> viewsList;

	/**
	 * Konstruktor
	 * @param context Kontext
	 * @param viewsList ID .xml resource
	 */
	public ViewPagerAdapter(Context context, ArrayList<LinearLayout> viewsList) {
		this.viewsList = viewsList;

	}

	/**
	 * Metoda destroyItem
	 */
	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((LinearLayout) object);
	}

	/**
	 * Metoda finishUpdate
	 */
	@Override
	public void finishUpdate(View arg0) {

	}

	/**
	 * Vrati pocet prvkov
	 */
	@Override
	public int getCount() {
		return viewsList.size();
	}

	/**
	 * Metoda instatiateItem
	 */
	@Override
	public Object instantiateItem(View view, int position) {
		View myView = viewsList.get(position);
		((ViewPager) view).addView(myView);
		return myView;
	}

	/**
	 * Metoda isViewFromObject
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	/**
	 * Metoda restoreState
	 */
	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	/**
	 * Metoda saveState
	 */
	@Override
	public Parcelable saveState() {
		return null;
	}

	/**
	 * Metoda startUpdate
	 */
	@Override
	public void startUpdate(View arg0) {

	}
}
