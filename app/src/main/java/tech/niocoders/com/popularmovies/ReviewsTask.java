package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.net.URL;

/**
 * Created by luism on 6/20/2018.
 */

public class ReviewsTask extends AsyncTask<String, Void, String> {

    private Context myContext;
    public ReviewsTask(Context context)
    {
        this.myContext = context;

    }
    @Override
    protected String doInBackground(String... strings) {
        String id  =  strings[0];
        if(TextUtils.isEmpty(id))
            return null;
        try {
            //lets create our url path
            URL pathUrl =  MovieUtilities.getMoviewReviewsUrl(BuildConfig.e9825d3aecdf8950a5ff95458c8e9445,id);
            String reviewsResult = MovieUtilities.getResponseFromHttpUrl(pathUrl);
            return reviewsResult;

        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //lets notify our movieDetailActivity that we have some reviews available so we can show them to the viewer
        if(s!=null && !TextUtils.isEmpty(s))
        {
            MovieDetailActivity movie = (MovieDetailActivity)myContext;
            movie.populateReviews(s);
        }

    }
}
