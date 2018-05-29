package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by luism on 5/28/2018.
 */

public class MovieImageAdapter extends BaseAdapter{
    private Context context ;

    public MovieImageAdapter(Context context)
    {this.context = context;}


    @Override
    public int getCount() {
        return ((MovieActivity)context).getMovieData().size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView imageView;
        TextView title;
        MovieActivity activity = (MovieActivity)context;
        final Movies movie =  activity.getMovieData().get(i);
        if (view == null && movie!=null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.movie_poster,null);


            imageView = view.findViewById(R.id.moviePoster);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(650, 650));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            title = view.findViewById(R.id.MovieTitle);
            PicassoSingleton.populateImageView(movie.getPosterPath(),imageView,500,500);

            title.setText(movie.getTitle());
        } else {
            imageView = (ImageView) view;
        }


        return imageView;
    }


}
