package DataBase;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.niocoders.com.popularmovies.MovieActivity;
import tech.niocoders.com.popularmovies.PicassoSingleton;
import tech.niocoders.com.popularmovies.R;

/**
 * Created by luism on 7/20/2018.
 */

public class DataBaseAdapter extends RecyclerView.Adapter<DataBaseAdapter.movieView> {

    // Class variables for the Cursor that holds task data and the Context
    public Cursor mCursor;
    private Context mContext;

    final private GridItemClickListener listener;


    private static int viewHolderCount;


    public interface GridItemClickListener{
        void onGridItemClick(int clickedItemGrid);
    }
    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public DataBaseAdapter(Context mContext, GridItemClickListener listener) {
        this.mContext = mContext; this.viewHolderCount =0; this.listener = listener;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public movieView onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        //attach to parent immediately boolean
        boolean shouldAttachToParentImmediately = false;
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_poster, parent, shouldAttachToParentImmediately);
        DataBaseAdapter.movieView movie =  new DataBaseAdapter.movieView(context,view);
        movie.movieViewIndex = viewHolderCount;

        viewHolderCount++;

        return movie;
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(movieView holder, int position) {


        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        int id = mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
        String img = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTERPATH));
        String headline =  mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        //Set values
        MovieActivity activity = (MovieActivity)mContext;
        holder.itemView.setTag(id);
        holder.movieId = id;
        holder.title.setText(headline);
        holder.link = img;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        int height = displayMetrics.heightPixels/2;
        int width = displayMetrics.widthPixels/3;
        PicassoSingleton.populateImageView(img, holder.poster, width, height);

        //GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority


    }



    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class movieView extends RecyclerView.ViewHolder  implements View.OnClickListener{

        public int movieViewIndex;
        public TextView title;
        public ImageView poster;
        private Context context;
        public int movieId;
        public String link;

        public movieView(Context context, View itemView) {
            super(itemView);
            this.context = context;

            title = itemView.findViewById(R.id.MovieTitle);
            poster = itemView.findViewById(R.id.moviePoster);
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poster.setPadding(8, 8, 8, 8);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onGridItemClick(clickedPosition);
        }

    }
}
