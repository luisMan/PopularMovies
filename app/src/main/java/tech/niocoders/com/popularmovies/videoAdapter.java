package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by luism on 5/31/2018.
 */

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.movieView> {

    public static final String TAG = ImageAdapter.class.getName();


    final private videoAdapter.GridItemClickListener listener;


    private static int viewHolderCount;

    private int mNumberItems;


    public interface GridItemClickListener{
        void onGridItemClick(int clickedItemGrid);
    }


    public videoAdapter(int itemSize, videoAdapter.GridItemClickListener listener)
    {
        this.mNumberItems = itemSize;
        this.listener = listener;
        viewHolderCount = 0;
    }
    @Override
    public videoAdapter.movieView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForImageItems = R.layout.video_poster;
        LayoutInflater inflater =  LayoutInflater.from(context);


        //attach to parent immediately boolean
        boolean shouldAttachToParentImmediately = false;

        View view  = inflater.inflate(layoutIdForImageItems,parent,shouldAttachToParentImmediately);
        videoAdapter.movieView movie =  new videoAdapter.movieView(context,view);
        movie.movieViewIndex = viewHolderCount;

        viewHolderCount++;

        return movie;
    }

    @Override
    public void onBindViewHolder(videoAdapter.movieView holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class movieView extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public int movieViewIndex;
        public TextView title;
        public ImageView poster;
        private Context context;

        public movieView(Context context,View itemView) {
            super(itemView);
            this.context = context;
            title = itemView.findViewById(R.id.videoTitle);
            poster = itemView.findViewById(R.id.videoThumb);
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);


            itemView.setOnClickListener(this);
        }


        void bind(int listIndex) {
            MovieDetailActivity activity = (MovieDetailActivity)context;
            final videos video =  activity.getTrailers().get(listIndex);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


            int height = displayMetrics.heightPixels/3;
            int width = displayMetrics.widthPixels;


            if(video!=null) {
                title.setText(video.getVideoTitle());
                PicassoSingleton.populateImageView(video.getThumbnailsUrl(), poster,width,height);

            }

        }


        // COMPLETED (6) Override onClick, passing the clicked item's position (getAdapterPosition()) to mOnClickListener via its onListItemClick method
        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onGridItemClick(clickedPosition);
        }
    }
}