package uz.qubemelon.ilearn.network.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadImageService extends AsyncTask<Object, Void, Bitmap> {

    private final static String TAG = "IMAGE_GETTER";
    private LevelListDrawable list_drawable;
    private Context context;

    @Override
    protected Bitmap doInBackground(Object... params) {
        String source = (String) params[0];
        list_drawable = (LevelListDrawable) params[1];
        Log.d(TAG, "doInBackground " + source);
        try {
            InputStream input_stream = new URL(source).openStream();
            return BitmapFactory.decodeStream(input_stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d(TAG, "onPostExecute drawable " + list_drawable);
        Log.d(TAG, "onPostExecute bitmap " + bitmap);
        if (bitmap != null) {
            BitmapDrawable bitmap_drawable = new BitmapDrawable(context.getResources(), bitmap);
            list_drawable.addLevel(1, 1, bitmap_drawable);
            list_drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            list_drawable.setLevel(1);

        }
    }
}
