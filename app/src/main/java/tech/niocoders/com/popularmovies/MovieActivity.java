package tech.niocoders.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
//the Fresh start of my movie app I have only three days to submit
//finally finish with school finals will provide much better timing as of 5/27/2018
//Am glad to be part of this amazing opportunity
//am also illustrating some really nice UI



public class MovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
   ImageAdapter.GridItemClickListener {

    private static final String SEARCH_QUERY_URL_EXTRA = "movies";
    private static final String SEARCH_SORT_BY_QUERY = "sort_by";
    private static final String SPINER_POSITION = "spinner";
    private static final int SEACH_QUERY_MOVIE_ID=22;
    public  static String movie_json="";
    //progressbar and network text notification
    private TextView networkError;
    private ProgressBar loaderIndicator;
    private ArrayList<Movies> movieData;
    private static String SORT_BY ="";
    private Spinner spinner;
    //our Recycle view object references
    private ImageAdapter mAdapter;
    private RecyclerView recyclerView;
    private Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        //toolbar.setSubtitle("Subtitle");

        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(android.R.drawable.ic_dialog_alert);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(MovieActivity.this, "Toolbar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        //Gridlayout

        recyclerView = findViewById(R.id.movieRecycle);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //EditText network error
        networkError = findViewById(R.id.networkError);
        //ProgreeBar loader indicator
        loaderIndicator =  findViewById(R.id.searching_movies);

        //lets instanciate movieData
        movieData =  new ArrayList<>();


        //lets just check if there is any network available if not lets save our url to bundle
        if (savedInstanceState != null) {
            SORT_BY = savedInstanceState.getString(SEARCH_SORT_BY_QUERY);
            movie_json = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            if (MovieUtilities.isThereNetworkAvailable(MovieActivity.this)) {
                getSupportLoaderManager().initLoader(SEACH_QUERY_MOVIE_ID, savedInstanceState, this);
            }
        }else{
            makeMoviesSearch();
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_movies, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        //since screen rotation happens my spinner was refreshing my recycler view and adapter
        //i needed to find a lot of solutions but it was hard to deal with Bundles since always i was returning null to check for extra values
        //then I figure that the spinner should always preserve a text noAction when there is screen rotation
        //if there is any additional documentation for me to get knowledge on findind a better solution
        //please don't hesitate on letting me know.
        spinner.setSelection(2);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
            {
               //lets make the calls base on item selected
                if(spinner.getItemAtPosition(position).toString().equals("top rated"))
                {
                    SORT_BY = "vote_average.desc";
                    makeMoviesSearch();
                }else if(spinner.getItemAtPosition(position).toString().equals("most popular")){

                    SORT_BY = "popularity.desc";
                    makeMoviesSearch();

                }else{
                    //no action
                  }
                //lets proceed to the search

            }

            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

   //getter to obtain movieData from current parent activity
    public  ArrayList<Movies> getMovieData()
    {return this.movieData;}

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        //here we will launch a new asyntask Loader and ask for movie data
        return new AsyncTaskLoader<String>(this) {
            String json;
            @Override
            protected void onStartLoading() {
                //Log.v("url_onStartLoading","onStartLoadingCalled");
                if(args==null && !MovieUtilities.isThereNetworkAvailable(MovieActivity.this) )
                {
                    showNetworkErrorMessage();
                    return;
                }



                if (null!= json ) {
                   // Log.v("url_results",json);
                    loaderIndicator.setVisibility(View.INVISIBLE);
                    deliverResult(json);
                } else {
                    loaderIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }


            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                 //Log.v("url_back",searchQueryUrlString);
                /* If the user didn't enter anything, there's nothing to search for */
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                /* Parse the URL from the passed in String and perform the search */
                try {
                    URL moviesUrl = new URL(searchQueryUrlString);
                    String moviesSearchResults = MovieUtilities.getResponseFromHttpUrl(moviesUrl);
                    return moviesSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }



            @Override
            public void deliverResult(String data) {
                 json = data;
                super.deliverResult(data);
            }
        };
    }

    //makeMoviesearch this method will be updated to make search dynamic base on user preferences on his searching criteria
    public void makeMoviesSearch()
    {
        //lets check is there is network availabilities if not lets show the error
        if(!MovieUtilities.isThereNetworkAvailable(this))
        {
          showNetworkErrorMessage();
          return;
        }


        //Toast.makeText(getApplicationContext(),"sorty by :"+SORT_BY,Toast.LENGTH_LONG).show();
        URL moviesUrl = MovieUtilities.getMostPopularMovies(getResources().getString(R.string.movie_api_key_v3),SORT_BY);

        Bundle movieBundle = new Bundle();
        movieBundle.putString(SEARCH_QUERY_URL_EXTRA, moviesUrl.toString());
        movieBundle.putString(SEARCH_SORT_BY_QUERY, SORT_BY);


        LoaderManager loaderManager =   getSupportLoaderManager();
        Loader<String> movieSearch = loaderManager.getLoader(SEACH_QUERY_MOVIE_ID);

        if(movieSearch==null)
        {
           // Log.v("url_create_loader","created loader");
            loaderManager.initLoader(SEACH_QUERY_MOVIE_ID, movieBundle, this);

        }else{
           // Log.v("url_restart_loader","restarted loader");
            loaderManager.restartLoader(SEACH_QUERY_MOVIE_ID,movieBundle,this);
        }

    }

    public void showNetworkErrorMessage()
    {

        loaderIndicator.setVisibility(View.INVISIBLE);
        networkError.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        loaderIndicator.setVisibility(View.INVISIBLE);
        /*
         * If the results are null, we assume an error has occurred. There are much more robust
         * methods for checking errors, but we wanted to keep this particular example simple.
         */
        if (null == data) {
            Log.d("jsonError","no data has return host!");
        } else {
           // Log.v("url_onloadFinished","onLoadCalled");
            movie_json =  data;
            movieData =  JsonParserSingleton.getMovieDataFromJeson(movie_json);
            populateMoviesGridUi();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //this will be implemented in future updates if necessary
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
              loaderIndicator.setVisibility(View.INVISIBLE);
        Log.v("url_savedInstance",SORT_BY);
              outState.putString(SEARCH_QUERY_URL_EXTRA, movie_json);
              outState.putString(SEARCH_SORT_BY_QUERY, SORT_BY);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie_json = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
        SORT_BY = savedInstanceState.getString(SEARCH_SORT_BY_QUERY);
        Log.v("url_restoreInstance",SORT_BY);
        loaderIndicator.setVisibility(View.INVISIBLE);
        movieData =  JsonParserSingleton.getMovieDataFromJeson(movie_json);
        populateMoviesGridUi();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        Toast.makeText(getApplicationContext(),"Activity Reenter",Toast.LENGTH_LONG).show();
    }

    //pupulate movie ui
    public void populateMoviesGridUi()
    {

            mAdapter = new ImageAdapter(movieData.size(), this);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

    }//close populate movie


    @Override
    public void onGridItemClick(int clickedItemGrid) {
        Intent childActivity = new Intent(MovieActivity.this, MovieDetailActivity.class);
        childActivity.putExtra("description", movieData.get(clickedItemGrid)); // using the (String name, Parcelable value) overload!
        startActivity(childActivity);

    }
}
