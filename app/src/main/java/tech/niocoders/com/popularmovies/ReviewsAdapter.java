package tech.niocoders.com.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by luism on 6/20/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.reviewClass> {

    private int mNumberItems;
    private int viewHolderCount;
    public ReviewsAdapter(int itemSize)
    {
        this.mNumberItems = itemSize;
        viewHolderCount = 0;
    }
    @Override
    public reviewClass onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForImageItems = R.layout.reviews_poster;
        LayoutInflater inflater =  LayoutInflater.from(context);


        //attach to parent immediately boolean
        boolean shouldAttachToParentImmediately = false;

        View view  = inflater.inflate(layoutIdForImageItems,parent,shouldAttachToParentImmediately);
        ReviewsAdapter.reviewClass reviews =  new ReviewsAdapter.reviewClass(context,view);
        reviews.movieViewIndex = viewHolderCount;

        viewHolderCount++;

       return reviews;
    }

    @Override
    public void onBindViewHolder(reviewClass holder, int position) {
        //lets bind our holder base on recycler view position
       holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class reviewClass extends RecyclerView.ViewHolder{

        private Context context;
        private TextView author;
        private TextView comment;
        public int movieViewIndex;

        public reviewClass(Context context,View itemView) {
            super(itemView);
            this.context = context;
            author = itemView.findViewById(R.id.authorTextView);
            comment = itemView.findViewById(R.id.commentTextView);

            //we don't need any listener attach to this viewHolder since we will only show text comments from users
        }

        void bind(int listIndex) {
            MovieDetailActivity activity = (MovieDetailActivity) context;
            final reviews rev =  activity.getReviews().get(listIndex);


            if(rev!=null) {
                author.setText(rev.getReviewAuthor());
                comment.setText(rev.getReviewComment());
                //comment.setText(reviews.getComment());
            }else{
                comment.setText("'There are not Reviews at the moment'");
            }

        }
    }//close inner class
}
