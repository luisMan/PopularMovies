package tech.niocoders.com.popularmovies;

/**
 * Created by luism on 6/20/2018.
 */

public class reviews {
    private String reviewId;
    private String reviewAuthor;
    private String reviewComment;

    public reviews(String reviewId,String reviewAuthor,String reviewComment)
    {
        this.reviewId = reviewId;
        this.reviewAuthor = reviewAuthor;
        this.reviewComment = reviewComment;
    }

    //getters
    public String getReviewId()
    {return this.reviewId;}
    public String getReviewAuthor()
    {return this.reviewAuthor;}
    public String getReviewComment()
    {return this.reviewComment;}

    //setters
    public void setReviewId(String id)
    {this.reviewId= id;}
    public void setReviewAuthor(String author)
    {this.reviewAuthor=author;}
    public void setReviewComment(String comment)
    {this.reviewComment = comment;}

    public String toString()
    {return "review id : "+this.reviewId
            +"\nreview author : "+this.reviewAuthor
            +"\nreview comment : "+this.reviewComment;}
}
