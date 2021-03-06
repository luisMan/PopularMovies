package tech.niocoders.com.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by luism on 5/28/2018.
 * in this class we will get all the necessary data for our movies
 * also lets use the MovieUtitlites class to create a designated url for user references on images
 */

public class JsonParserSingleton {

    public static ArrayList<Movies> getMovieDataFromJeson(String json) {
        JSONObject parser = null;
        ArrayList<Movies> toReturn = new ArrayList<>();
        try {
            parser = new JSONObject(json);
            if (null != parser) {
                JSONArray pages = parser.getJSONArray("results");
                for (int i = 0; i < pages.length(); i++) {
                    JSONObject object = (JSONObject) pages.get(i);
                    long voteCount = Long.parseLong(object.getString("vote_count"));
                    int id = Integer.parseInt(object.getString("id"));
                    String videoUrl = object.getString("video").toString();
                    double voteAvg = Double.parseDouble(object.getString("vote_average"));
                    String title = object.getString("title");
                    double popularity = Double.parseDouble(object.getString("popularity"));
                    String posterPath = MovieUtilities.getMoviesUrl(object.getString("poster_path")).toString();
                    String originalLanguage = object.getString("original_language");
                    //for the gender ids I will just jump for now
                    String backDropPath = MovieUtilities.getMoviesUrl(object.getString("backdrop_path")).toString();
                    //lets return 1 if the content is maturity otherwise 0
                    int maturity = object.getString("adult").toString().equals("true") ? 1 : 0;

                    String overView = object.getString("overview");
                    String releaseDate = object.getString("release_date");

                    //lets construct our parceable object :-)
                    Movies movie = new Movies(voteCount, id, videoUrl, voteAvg, title, popularity, posterPath, originalLanguage, backDropPath, maturity, overView, releaseDate);
                    toReturn.add(movie);

                    //Log.d("testing", movie.toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    public static ArrayList<videos> getMovieTrailers(MovieDetailActivity view, String json) {
        ArrayList<videos> toReturn = new ArrayList<>();
        if (json != null && json.length() > 0) {
            JSONArray parser = null;
            try {
                parser = new JSONArray(json);
                for (int i = 0; i < parser.length(); i++) {
                    JSONObject object = (JSONObject) parser.get(i);
                    JSONObject ids = object.getJSONObject("id");
                    JSONObject snippet = object.getJSONObject("snippet");
                    String id = ids.getString("videoId");
                    String videoTitle = snippet.getString("title");
                    String url = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");

                    videos video = new videos(id, url, videoTitle);
                    // Log.v("video parsed ",id+"\n"+url+"\n"+videoTitle);
                    toReturn.add(video);
                }


            } catch (JSONException e) {
                Log.v("videoError", e.getMessage());
            }


        }
        return toReturn;
    }


    //our method to get the movie reviews
    public static ArrayList<reviews> getMovieReviews(MovieDetailActivity context, String json) {
        JSONObject parser = null;
        ArrayList<reviews> toReturn = new ArrayList<>();
        try {
            parser = new JSONObject(json);
            if (null != parser) {
                JSONArray pages = parser.getJSONArray("results");
                for (int i = 0; i < pages.length(); i++) {
                    JSONObject object = (JSONObject) pages.get(i);
                    String id = object.getString("id").toString();
                    String author = object.getString("author").toString();
                    String content = object.getString("content");

                    //lets construct our parceable object :-)
                    reviews review = new reviews(id, author, content);
                    toReturn.add(review);

                    //  Log.d("testing", review.toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return toReturn;

    }
}
