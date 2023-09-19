package uz.qubemelon.ilearn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    /* all global field instances and variables */
    private List<Integer> images;
    private LayoutInflater inflater;
    private Context context;

    public ImageSliderAdapter(List<Integer> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        /* make imageview with current activity */
        ImageView mImageView = new ImageView(context);
        /* set centre crop scaling the imageview */
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        /* bind the image to the imageview by taking the image from the list with position */
        mImageView.setImageResource(images.get(position));

        /* set imageview to the view group */
        container.addView(mImageView, 0);

        return mImageView;

    }


    /*remove the imageview if it is already there and add the new one */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
