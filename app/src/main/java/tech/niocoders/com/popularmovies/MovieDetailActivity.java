package tech.niocoders.com.popularmovies;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;

import tech.niocoders.com.database.MovieContract;

/**
 * Created by luism on 5/28/2018.
 */

public class MovieDetailActivity extends AppCompatActivity implements videoAdapter.GridItemClickListener,
        View.OnClickListener {
    private ImageView slideShow;
    private TextView title;
    private TextView releaseDate;
    private TextView voteCounts;
    private TextView voteAverage;
    private TextView description;
    private TextView language;
    private ProgressBar progressBar;
    private TextView progressBarPercent;
    private ImageView wrapper;
    private ImageView backDropPath;
    private Movies movie;
    private Button favoriteButton;
    private int width, height;
    private ArrayList<videos> trailers;
    private ArrayList<reviews> movieReviews;


    //our recycler view to show movie trailers from youtube api
    private RecyclerView movieTrailers;
    //our reviews recyclerview
    private RecyclerView reviews;

    private ScrollView movieDetails;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_details);


        wrapper = findViewById(R.id.wrapper);
        movieDetails = findViewById(R.id.movieDetails);
        slideShow = findViewById(R.id.slideShowView);
        title = findViewById(R.id.movieTitle);
        voteCounts = findViewById(R.id.voteCount);
        releaseDate = findViewById(R.id.movieReleasedDate);
        voteAverage = findViewById(R.id.voteAverage);
        description = findViewById(R.id.description);
        language = findViewById(R.id.language);
        progressBar = findViewById(R.id.popularityBar);
        progressBarPercent = findViewById(R.id.popularityPercent);
        backDropPath = findViewById(R.id.backDropPath);
        favoriteButton = findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(this);
        movie =  getIntent().getParcelableExtra("description");
        if (movie == null) {
            finish();
        }

        //change the title of the activity
        this.getSupportActionBar().setTitle(movie.getTitle());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;



        //lets animate with transitions for background and title sweet thank you for all this great content am going to be an
        //amazing developer
        AnimatorSet animationSet = new AnimatorSet();

        //Translating Details_Card in Y Scale
        ObjectAnimator wrapper_y = ObjectAnimator.ofFloat(wrapper, View.TRANSLATION_Y, 70);
        wrapper_y.setDuration(2500);
        wrapper_y.setRepeatMode(ValueAnimator.REVERSE);
        wrapper_y.setRepeatCount(ValueAnimator.INFINITE);
        wrapper_y.setInterpolator(new LinearInterpolator());

        //Translating Movie_Cover in Y Scale
        ObjectAnimator poster_x = ObjectAnimator.ofFloat(slideShow, View.TRANSLATION_X, 30);
        poster_x.setDuration(3000);
        poster_x.setRepeatMode(ValueAnimator.REVERSE);
        poster_x.setRepeatCount(ValueAnimator.INFINITE);
        poster_x.setInterpolator(new LinearInterpolator());

        ObjectAnimator backdrop_x = ObjectAnimator.ofFloat(backDropPath, View.TRANSLATION_X, 30);
        backdrop_x.setDuration(3000);
        backdrop_x.setRepeatMode(ValueAnimator.REVERSE);
        backdrop_x.setRepeatCount(ValueAnimator.INFINITE);
        backdrop_x.setInterpolator(new LinearInterpolator());

        animationSet.playTogether(wrapper_y,poster_x,backdrop_x);
        animationSet.start();

        //use picasso to load image into view
        PicassoSingleton.populateImageView(movie.getPosterPath(), wrapper, width, height);
        PicassoSingleton.populateImageView(movie.getPosterPath(), slideShow, width * 2, height * 2);
        PicassoSingleton.populateImageView(movie.getBackDropPath(), backDropPath, width, height / 3);

        //set text utilities
        favoriteButton.setTag(movie.getId());
        title.setText(movie.getTitle());
        releaseDate.setText("Released : " + movie.getReleaseDate());
        voteCounts.setText("Votes : " + movie.getVoteCount());
        voteAverage.setText("VoteAverage : " + movie.getVoteAverage());
        description.setText(movie.getOverviewDescription());
        language.setText("Language : " + movie.getLanguage());

        double popularity = (movie.getPopularity() / 1000) * 100;
        progressBar.setMax((int) popularity);
        progressBarPercent.setText("%" + movie.getPopularity());

        //lets reset the favicon
        ResetFavoriteButtonIcon();

        //lets get a reference id of our trailer recicle
        movieTrailers = findViewById(R.id.trailersRecycle);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieTrailers.setLayoutManager(layoutManager);

        //lets find our reviews RecyclerView
        reviews = findViewById(R.id.ReviewsReciclerView);
        LinearLayoutManager layoutReviewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviews.setLayoutManager(layoutReviewManager);


        //lets just do some text to see if we can retrieve any youtube videos for given movie title
        trailers = new ArrayList<>();
        new YoutubeVideosTask(this).execute(movie.getTitle());
        //lets launch the asyncTask for our movie reviews base on movie id
        movieReviews = new ArrayList<>();
        new ReviewsTask(this).execute("" + movie.getId());

    }


    public void ResetFavoriteButtonIcon() {
        String stringId = favoriteButton.getTag().toString();
        Uri findId = MovieContract.MovieEntry.CONTENT_URI;
        String[] selectionArgs = {stringId};
        Cursor cursor = getContentResolver().query(findId, null, MovieContract.MovieEntry.COLUMN_ID + "=?", selectionArgs, null);

        if ((cursor != null && cursor.moveToFirst())
                && Integer.parseInt(stringId) == cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID))) {
            favoriteButton.setBackgroundResource(R.drawable.favicon2);
        } else {
            favoriteButton.setBackgroundResource(R.drawable.favicon1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the parent activity
        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            //NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(int clickedItemGrid) {
        //lets provide click listener to all our videos and play it on our own youtube player
        //without having the user to navigate out of my app :-)
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, BuildConfig.AIzaSyDU7kuvsG_RbSNnfZz_ONpJMWZaPpUtcjc, getTrailers().get(clickedItemGrid).getVideoId());
        startActivity(intent);


    }


    //nice this is so sweet I managed to make youtube video api worked with this minor asynctask class sweet
    //hope this contribution surprise you ... am so happy to be part of this class and hopefully with all the skills I am gaining obtain a job


    //getter to obtain the trailers
    public ArrayList<videos> getTrailers() {
        return trailers;
    }

    public ArrayList<reviews> getReviews() {
        return movieReviews;
    }

    //pupulate trailers
    public void pupolateTrailers(String json) {
        trailers = JsonParserSingleton.getMovieTrailers(MovieDetailActivity.this, json);

        if (trailers != null) {
            //Toast.makeText(getApplicationContext(),"trailer size : "+trailers.size(),Toast.LENGTH_LONG).show();
            videoAdapter video = new videoAdapter(trailers.size(), MovieDetailActivity.this);
            movieTrailers.setAdapter(video);
        }
    }

    //populate reviews
    public void populateReviews(String json) {
        movieReviews.clear();
        movieReviews = JsonParserSingleton.getMovieReviews(MovieDetailActivity.this, json);

        if (movieReviews != null && movieReviews.size() > 0) {
            ReviewsAdapter reviewAdapter = new ReviewsAdapter(movieReviews.size());
            reviews.setAdapter(reviewAdapter);
        } else {
            movieReviews.add(new reviews("unknown", "unknown", "There are no reviews for this movie"));
            ReviewsAdapter reviewAdapter = new ReviewsAdapter(movieReviews.size());
            reviews.setAdapter(reviewAdapter);

        }
    }


    public void removeMovieFromDBIfExists(String stringId)
    {

        // Build appropriate uri with String row id appended
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        // COMPLETED (2) Delete a single row of data using a ContentResolver
        getContentResolver().delete(uri, null, null);

    }

    //this is going to be the method that will be executing the favorite button clicks
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int tag = (int) view.getTag();
        //well well here we will check our mysqli database using content providers to see if the item
        //is already on the list. if not then we add it and refresh button icon to illustrate thaT THE ITEM has been added.
        switch (id) {
            case R.id.favorite:
                //lets check if the movie is on the database and if it is lets removed it.
                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(tag);
                Uri findId = MovieContract.MovieEntry.CONTENT_URI;
                String[] selectionArgs = {stringId};
                Cursor cursor = getContentResolver().query(findId, null, MovieContract.MovieEntry.COLUMN_ID + "=?", selectionArgs, null);

                if ((cursor != null && cursor.moveToFirst())
                        && Integer.parseInt(stringId) == cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID))) {
                    // int movieId =  cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
                    Toast.makeText(getBaseContext(), "just Removed " + movie.getTitle() + " from Favorite list", Toast.LENGTH_LONG).show();
                    removeMovieFromDBIfExists(Integer.toString(tag));
                    favoriteButton.setBackgroundResource(R.drawable.favicon1);
                } else {
                    // Insert new task data via a ContentResolver
                    // Create new empty ContentValues object
                    ContentValues contentValues = new ContentValues();
                    // Put the task description and selected mPriority into the ContentValues

                    contentValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_URL, movie.getVideoUrl());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, movie.getVoteAverage());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VOTECOUNT, movie.getVoteCount());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, movie.getPosterPath());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, movie.getLanguage());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_FORMATURE, movie.getForMaturesOnly());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, movie.getOverviewDescription());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, movie.getReleaseDate());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROPPATH, movie.getBackDropPath());
                    // Insert the content values via a ContentResolver
                    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    // Display the URI that's returned with a Toast
                    // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
                    if (uri != null) {
                        Toast.makeText(getBaseContext(), "just added " + movie.getTitle() + " to DataBase at " + uri.toString(), Toast.LENGTH_LONG).show();
                        favoriteButton.setBackgroundResource(R.drawable.favicon2);
                    }
                }//close else

                cursor.close();
                break;

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("MOVIE_SCROLL_POSITION",
                new int[]{ movieDetails.getScrollX(), movieDetails.getScrollY()});
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("MOVIE_SCROLL_POSITION");
        if(position != null)
            movieDetails.post(new Runnable() {
                public void run() {
                    movieDetails.scrollTo(position[0], position[1]);
                }
            });
    }
}
