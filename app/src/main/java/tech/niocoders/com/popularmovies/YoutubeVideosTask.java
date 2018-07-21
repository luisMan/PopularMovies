package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luism on 5/31/2018.
 * this asyntack class will be use to extract images and videos ids from youtube api to show trailers within my app
 * I push myself a little further even though this wasn't required I love challenges.
 *
 */


public class YoutubeVideosTask extends AsyncTask<String,Void, List<SearchResult>> {

    private Context context;
    //youtube api
    static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    static final JsonFactory jsonFactory = new GsonFactory();

    public YoutubeVideosTask(Context context)
    {
        this.context = context;

    }

    @Override
    protected  List<SearchResult> doInBackground(String... voids) {
        try {
            YouTube youtube = new YouTube.Builder(transport, jsonFactory, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(MovieUtilities.class.getName()).build();


            //the type of data to return as list
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "id,snippet");
            parameters.put("maxResults", "25");
            parameters.put("q",voids[0] );
            parameters.put("type", "video");
            YouTube.Search.List search = youtube.search().list(parameters.get("part").toString());
            //set the key
            search.setKey(context.getResources().getString(R.string.youtube_api_key));

            if (parameters.containsKey("maxResults")) {
                search.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
            }

            if (parameters.containsKey("q") && parameters.get("q") != "") {
                search.setQ(parameters.get("q").toString());
            }

            if (parameters.containsKey("type") && parameters.get("type") != "") {
                search.setType(parameters.get("type").toString());
            }
            //the fields to return for the trailers :-)
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            Log.v("video_list",searchResponse.toString());
            List<SearchResult> searchResultList = searchResponse.getItems();

            if(searchResultList!=null)
            {
                return searchResultList;
            }



        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<SearchResult> searchResults) {
        super.onPostExecute(searchResults);
        if(null!=searchResults)
        {
            MovieDetailActivity movie = (MovieDetailActivity)context;
            Log.v("videos",searchResults.toString());
            movie.pupolateTrailers(searchResults.toString());
        }

    }

}
