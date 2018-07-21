package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by luism on 5/28/2018.
 */

public  class MovieUtilities {
    public static String BASE_MOVIE_URL="https://api.themoviedb.org/3";
    public static String CONSTRUCT_IMAGES_URL="https://image.tmdb.org/";
    public static String SORT_BY="sort_by";
    public static String DISCOVER="discover";
    public static String PARAM_QUESTION="?";
    public static String PARAM_AND="&";
    public static String BACKSLASH="/";
    public static String MOVIE ="movie";
    public static String REVIEWS ="reviews";
    public static String EQUAL="=";
    public static String KEY="api_key";
    public static String QUERY = "";
    public static String END_LANGUAGE = "language";
    public static String LANGUAGE_END_POINT = "en-US";
    public static String END_PAGE = "page";
    public static String PAGE_NUMBER = "1";

    //sorting params
    public static String POPULARDESC="popularity.desc";




    //https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=e9825d3aecdf8950a5ff95458c8e9445
    //this method will create an url to get the most popular movies and will accept api key as a parameter to complete the search
    //http://api.themoviedb.org/3/discover/movie?api_key=e9825d3aecdf8950a5ff95458c8e9445
    public static URL getMostPopularMovies(String apikey,String sort_by) {
        if(sort_by.equals("popularity.desc"))
        {
            QUERY = "popular";
        }else{
            QUERY = "top_rated";
        }


        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(MOVIE)
                .appendPath(QUERY)
                .appendQueryParameter(SORT_BY,sort_by)
                .appendQueryParameter(KEY,apikey)
                .appendQueryParameter(END_LANGUAGE,LANGUAGE_END_POINT)
                .appendQueryParameter(END_PAGE,PAGE_NUMBER)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            //Log.v("url",url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL getMoviewReviewsUrl(String apikey,String movieId) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(MOVIE)
                .appendPath(movieId)
                .appendPath(REVIEWS)
                .appendQueryParameter(KEY,apikey)
                .appendQueryParameter(END_LANGUAGE,LANGUAGE_END_POINT)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            //Log.v("url",url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL getMoviesUrl(String img)
    {


        Uri builtUri = Uri.parse(CONSTRUCT_IMAGES_URL).buildUpon()
                .appendPath("t")
                .appendPath("p")
                .appendPath("w500")
                .appendEncodedPath(img)
            .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    //lets check if we have internet network available to avoid user connectivity exceptions such as app crashes
    //we are accepting context as parameter since we need to get a hold of our current activity
    public static boolean isThereNetworkAvailable(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }




}
