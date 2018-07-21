package tech.niocoders.com.popularmovies;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by luism on 5/28/2018.
 * In this class I will construct a singleton object to be use only when we need to illustrate image data to a giving activity view
 */

public class PicassoSingleton{
    //well well this picaso singleton class will only gets call to populate an imageview with given url

    public static void populateImageView(String url, ImageView view, int w,int h)
    {
        Picasso.get()
                .load(url)
                .resize(w, h)
                .into(view);
    }
}
