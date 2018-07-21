package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import DataBase.DataBaseAdapter;
import DataBase.MovieContract;

/**
 * Created by luism on 7/20/2018.
 */

public class favoriteLoader implements
        LoaderManager.LoaderCallbacks<Cursor>, DataBaseAdapter.GridItemClickListener {


    // Constants for logging and referring to a unique loader
    private static final String TAG = "Favorites";
    private static final int FAV_MOVIES_ID = 0;
    private Context context;
    private RecyclerView favRecycle;
    private DataBaseAdapter CustomCursorAdapter;

    public favoriteLoader(final Context context, RecyclerView rec)
    {
         this.context = context;
         this.favRecycle = rec;
         this.favRecycle.setLayoutManager(new LinearLayoutManager(context));

        // Initialize the adapter and attach it to the RecyclerView
        CustomCursorAdapter = new DataBaseAdapter(context,this);
        this.favRecycle.setAdapter(CustomCursorAdapter);


        //touch listener for the recycler view
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Context fContext = context;
                final FragmentActivity mainApp = (FragmentActivity) context;
                // Here is where you'll implement swipe to delete
                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                fContext.getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                mainApp.getSupportLoaderManager().restartLoader(FAV_MOVIES_ID, null, favoriteLoader.this);

            }
        }).attachToRecyclerView(favRecycle);



    }
    //this is the method to be used and load the fav movies from database
    public void LoadDataBaseFavoriteList()
    {

        FragmentActivity mainApp = (FragmentActivity) context;
        LoaderManager loaderManager =   mainApp.getSupportLoaderManager();
        Loader<Cursor> favMoviews = loaderManager.getLoader(FAV_MOVIES_ID);
        if(loaderManager==null){

            mainApp. getSupportLoaderManager().initLoader(FAV_MOVIES_ID, null, this);
        }else{
            onResume();
        }

    }

    public void onResume() {

        // re-queries for all tasks
        FragmentActivity mainApp = (FragmentActivity) context;
        mainApp.getSupportLoaderManager().restartLoader(FAV_MOVIES_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(context) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                Log.v("cursor", data.toString());
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        CustomCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //this method is implemented from DataBaseMovieAdapter and it will provide the item position from a specific cursor
    @Override
    public void onGridItemClick(int clickedItemGrid) {
        CustomCursorAdapter.mCursor.moveToPosition(clickedItemGrid);
        final Cursor custom = CustomCursorAdapter.mCursor;

        // Determine the values of the wanted data
        int id = custom.getInt(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
        String img = custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTERPATH));
        String headline =  custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        long voteCounts =  custom.getLong(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTECOUNT));
        String videoUrl = custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO_URL));
        double voteAvg = custom.getDouble(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE));
        double popularity = custom.getDouble(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
        String lang =  custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_LANGUAGE));
        int maturity = custom.getInt(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_FORMATURE));
        String desc =  custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION));
        String releaseDate = custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASEDATE));
        String backDropPath = custom.getString(custom.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROPPATH));

        Movies movie = new Movies(voteCounts,id,videoUrl,voteAvg,headline,popularity,img,lang,backDropPath,maturity,desc,releaseDate);


        Intent childActivity = new Intent(context, MovieDetailActivity.class);
        childActivity.putExtra("description",movie); // using the (String name, Parcelable value) overload!
        context.startActivity(childActivity);
       /* Toast.makeText(context,
                "Just clicked id = "
                 +id+" head line = "+headline, Toast.LENGTH_LONG).show();*/
    }
}
