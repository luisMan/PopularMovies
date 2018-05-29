package tech.niocoders.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by luism on 5/28/2018.
 */

public class MovieDetailActivity extends AppCompatActivity{
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

    }




}
