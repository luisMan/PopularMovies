package tech.niocoders.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

//Am glad to be part of this amazing opportunity
//am also illustrating some really nice UI
//I was  out of the country and just got married.. but hope that this nice features
//included on my app surprise you. Thank you for this great projects

public class MovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
   ImageAdapter.GridItemClickListener, DrawerLayout.DrawerListener {

    public Bundle globalSavedInstanceState;
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
    public  int spinnerSelection;
    //our Recycle view object references
    private ImageAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView favoriteRecyclerView;
    private Toast mToast;

    //android DrawerLayout variables
    private DrawerLayout mDrawerLayout;
    private NavigationView favoriteView;
    //private ActionBarDrawerToggle mToggle;

    //our private FavoriteLoader for navigationView
    private favoriteLoader favLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        //toolbar.setSubtitle("Subtitle");
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(this);

       /* favoriteView =  findViewById(R.id.favorite_views);
        favoriteView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return true;
            }
        });*/

        recyclerView = findViewById(R.id.movieRecycle);
        favoriteRecyclerView = findViewById(R.id.favoriteRecycle);

        int posterWidth =Integer.parseInt(getResources().getString(R.string.screen_grid_dimension_width).toString());
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, calculateBestSpanCount(posterWidth));
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);

        //lets assign main favorite cursor loader to our custom GridLoader
        //init favLoader
        this.favLoader =  new favoriteLoader(this, favoriteRecyclerView);


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


        getSupportActionBar().setHomeButtonEnabled(false);

      if(savedInstanceState!=null)
      {
          this.globalSavedInstanceState = savedInstanceState;
      }
    }

    //base on code review I copy your method and implemented it to my code to make my app more compatible with screens
    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
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
        //if there is any additional documentation for me to get knowledge on finding a better solution
        //please don't hesitate on letting me know.



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
            {

               //lets make the calls base on item selected
                if(spinner.getItemAtPosition(position).toString().equals("Top rated"))
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    favoriteRecyclerView.setVisibility(View.INVISIBLE);
                    SORT_BY = "vote_average.desc";
                    makeMoviesSearch();
                }else if(spinner.getItemAtPosition(position).toString().equals("Most popular")){
                    recyclerView.setVisibility(View.VISIBLE);
                    favoriteRecyclerView.setVisibility(View.INVISIBLE);
                    SORT_BY = "popularity.desc";
                    makeMoviesSearch();

                }else if(spinner.getItemAtPosition(position).toString().equals("Favorite")){
                    recyclerView.setVisibility(View.INVISIBLE);
                    favoriteRecyclerView.setVisibility(View.VISIBLE);
                    favLoader.LoadDataBaseFavoriteList();
                 }else {
                    //no action
                  }

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

       switch(id)
       {
          // case R.id.favorites:
               //here we will show the data base with some class with cursor loaders activity
               //since my own ui didn't pass then am letting this commented and will implement the code base on coding task from
               //project rubrics
              // mDrawerLayout.openDrawer(Gravity.START);
           //    return true;
       }

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

    public void RemovedNetworkErrorMessage()
    {
        if(!MovieUtilities.isThereNetworkAvailable(this)) {
            loaderIndicator.setVisibility(View.INVISIBLE);
            networkError.setVisibility(View.INVISIBLE);
        }

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
        //Toast.makeText(getApplicationContext(),"Activity Reenter",Toast.LENGTH_LONG).show();
    }

    //pupulate movie ui
    public void populateMoviesGridUi()
    {

            mAdapter = new ImageAdapter(movieData.size(), this);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            RemovedNetworkErrorMessage();
    }//close populate movie


    @Override
    public void onGridItemClick(int clickedItemGrid) {
        Intent childActivity = new Intent(MovieActivity.this, MovieDetailActivity.class);
        childActivity.putExtra("description", movieData.get(clickedItemGrid)); // using the (String name, Parcelable value) overload!
        startActivity(childActivity);

    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
            //Toast.makeText(getApplicationContext()," Drawer just open ", Toast.LENGTH_LONG).show();
             //favLoader.LoadDataBaseFavoriteList();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

           // Toast.makeText(getApplicationContext()," Drawer just Closed ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
