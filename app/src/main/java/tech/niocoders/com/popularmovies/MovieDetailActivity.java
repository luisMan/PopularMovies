package tech.niocoders.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;

/**
 * Created by luism on 5/28/2018.
 */

public class MovieDetailActivity extends AppCompatActivity implements videoAdapter.GridItemClickListener{
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
    private int width,height;
    private ArrayList<videos> trailers;

    //our recycler view to show movie trailers from youtube api
    private RecyclerView movieTrailers;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_details);





        wrapper = findViewById(R.id.wrapper);
        slideShow = findViewById(R.id.slideShowView);
        title = findViewById(R.id.movieTitle);
        voteCounts = findViewById(R.id.voteCount);
        releaseDate = findViewById(R.id.movieReleasedDate);
        voteAverage  = findViewById(R.id.voteAverage);
        description = findViewById(R.id.description);
        language =  findViewById(R.id.language);
        progressBar = findViewById(R.id.popularityBar);
        progressBarPercent = findViewById(R.id.popularityPercent);
        backDropPath = findViewById(R.id.backDropPath);
        movie = (Movies) getIntent().getParcelableExtra("description");
        if(movie ==null)
        {
            finish();
        }

        //change the title of the activity
        ((MovieDetailActivity) this).getSupportActionBar().setTitle(movie.getTitle());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //use picasso to load image into view
        PicassoSingleton.populateImageView(movie.getPosterPath(),wrapper,width,height);
        PicassoSingleton.populateImageView(movie.getPosterPath(),slideShow,width,height/3);
        PicassoSingleton.populateImageView(movie.getBackDropPath(),backDropPath,width,height/3);

        //set text utilities
        title.setText(movie.getTitle());
        releaseDate.setText("Released : "+movie.getReleaseDate());
        voteCounts.setText("Votes : "+movie.getVoteCount());
        voteAverage.setText("VoteAverage : "+movie.getVoteAverage());
        description.setText(movie.getOverviewDescription());
        language.setText("Language : "+movie.getLanguage());

        double popularity = (movie.getPopularity()/1000)*100;
        progressBar.setMax((int)popularity);
        progressBarPercent.setText("%"+movie.getPopularity());

        //lets get a reference id of our trailer recicle
        movieTrailers = findViewById(R.id.trailersRecycle);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieTrailers.setLayoutManager(layoutManager);


        //lets just do some text to see if we can retrieve any youtube videos for given movie title
        trailers = new ArrayList<>();
        new YoutubeVideosTask(this).execute(movie.getTitle());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the parent activity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(int clickedItemGrid) {
        //lets provide click listener to all our videos and play it on our own youtube player
        //without having the user to navigate out of my app :-)
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, getResources().getString(R.string.youtube_api_key), getTrailers().get(clickedItemGrid).getVideoId());
        startActivity(intent);


    }


    //nice this is so sweet I managed to make youtube video api worked with this minor asynctask class sweet
    //hope this contribution surprise you ... am so happy to be part of this class and hopefully with all the skills I am gaining obtain a job



    //getter to obtain the trailers
    public ArrayList<videos> getTrailers()
    {return trailers;}

    //pupulate trailers
    public void pupolateTrailers(String json)
    {
        trailers = JsonParserSingleton.getMovieTrailers(MovieDetailActivity.this,json);
        if(trailers!=null)
        {
            Toast.makeText(getApplicationContext(),"trailer size : "+trailers.size(),Toast.LENGTH_LONG).show();
            videoAdapter video = new videoAdapter(trailers.size(),MovieDetailActivity.this);
            movieTrailers.setAdapter(video);
        }
    }
}
