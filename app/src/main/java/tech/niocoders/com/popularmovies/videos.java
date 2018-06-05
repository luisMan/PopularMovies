package tech.niocoders.com.popularmovies;

/**
 * Created by luism on 5/31/2018.
 */

public class videos {
    private String videoId;
    private String ThumbnailsUrl;
    private String videoTitle;

    public videos(String videoId,String ThumbnailsUrl,String videoTitle)
    {
        this.videoId = videoId;
        this.ThumbnailsUrl = ThumbnailsUrl;
        this.videoTitle = videoTitle;
    }

    //getters
    public String getVideoId()
    {return this.videoId;}
    public String getThumbnailsUrl()
    {return this.ThumbnailsUrl;}
    public String getVideoTitle()
    {return this.videoTitle;}

    //setters
    public void setVideoId(String id)
    {this.videoTitle= id;}
    public void setThumbnailsUrl(String thumbnailsUrl)
    {this.ThumbnailsUrl=thumbnailsUrl;}
    public void setVideoTitle(String title)
    {this.videoTitle = title;}

    public String toString()
    {return "video Title : "+this.videoTitle
         +"\nvideo Thumbnail url : "+this.ThumbnailsUrl
         +"\nvideo id : "+this.videoId;}

}
