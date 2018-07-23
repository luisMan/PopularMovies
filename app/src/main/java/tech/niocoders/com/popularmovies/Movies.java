package tech.niocoders.com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luism on 5/28/2018.
 */

public class Movies implements Parcelable {
    private long voteCount;
    private int id;
    private String videoUrl;
    private double voteAverage;
    private String title;
    private double popularity;
    private String posterPath;
    private String language;
    private int forMaturesOnly;
    private String overviewDescription;
    private String releaseDate;
    private String backDropPath;
    private String[] slideShow;


    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };


    //constructor
    //this parameters are in order just as when we get it from json
    //vote count
    //id
    //videoUrl
    //vote average
    //title
    //popularity
    //posterpath
    //language
    //backdropPath
    //maturity
    //description
    //releasedate
    public Movies(long vc, int id, String vUrl, double vAv, String t, double pop, String ptp, String le, String backDropPath, int maturity, String description, String release) {
        this.voteCount = vc;
        this.id = id;
        this.videoUrl = vUrl;
        this.voteAverage = vAv;
        this.title = t;
        this.popularity = pop;
        this.posterPath = ptp;
        this.language = le;
        this.forMaturesOnly = maturity;
        this.overviewDescription = description;
        this.releaseDate = release;
        this.backDropPath = backDropPath;
        //instantiate the array for our new feature slide show :-)
        this.slideShow = new String[]{this.posterPath, this.backDropPath};
    }

    protected Movies(Parcel in) {
        this.voteCount = in.readLong();
        this.id = in.readInt();
        this.videoUrl = in.readString();
        this.voteAverage = in.readDouble();
        this.title = in.readString();
        this.popularity = in.readDouble();
        this.posterPath = in.readString();
        this.language = in.readString();
        this.backDropPath = in.readString();
        this.forMaturesOnly = in.readInt();
        this.overviewDescription = in.readString();
        this.releaseDate = in.readString();

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVoteCount(long v) {
        this.voteCount = v;
    }

    public void setStringVideoUrl(String video) {
        this.videoUrl = video;
    }

    public void setVoteAverage(double v) {
        this.voteAverage = v;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setPopularity(double p) {
        this.popularity = p;
    }

    public void setPosterPath(String path) {
        this.posterPath = path;
    }

    public void setLanguage(String l) {
        this.language = l;
    }

    public void setForMaturesOnly(int fm) {
        this.forMaturesOnly = fm;
    }

    public void setOverviewDescription(String overview) {
        this.overviewDescription = overview;
    }

    public void setReleaseDate(String releaseD) {
        this.releaseDate = releaseD;
    }

    public void setBackDropPath(String bdp) {
        this.backDropPath = bdp;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public long getVoteCount() {
        return this.voteCount;
    }

    public boolean getForMaturesOnly() {
        return this.forMaturesOnly == 0;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getOverviewDescription() {
        return this.overviewDescription;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public String getBackDropPath() {
        return this.backDropPath;
    }

    public String[] getSlideShow() {
        return this.slideShow;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(this.voteCount);
        parcel.writeInt(this.id);
        parcel.writeString(this.videoUrl);
        parcel.writeDouble(this.voteAverage);
        parcel.writeString(this.title);
        parcel.writeDouble(this.popularity);
        parcel.writeString(this.posterPath);
        parcel.writeString(this.language);
        parcel.writeString(this.backDropPath);
        parcel.writeInt(this.forMaturesOnly);
        parcel.writeString(this.overviewDescription);
        parcel.writeString(this.releaseDate);
    }

    public String toString() {
        return "Vote count : " + this.voteCount
                + "\nid : " + this.id
                + "\nvideo url :" + this.videoUrl
                + "\nvote average :" + this.voteAverage
                + "\ntitle :" + this.title
                + "\npopularity :" + this.popularity
                + "\nposterPath :" + this.posterPath
                + "\nlanguage :" + this.language
                + "\nbackDropPath :" + this.backDropPath
                + "\nforMaturity :" + this.getForMaturesOnly()
                + "\nOverViewDescription :" + this.overviewDescription
                + "\nReleaseDate : " + this.releaseDate;
    }
}
