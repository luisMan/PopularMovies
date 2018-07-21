package DataBase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by luism on 6/29/2018.
 */

public class MovieContract  {

    public static final String AUTHORITY  = "DataBase";
    public static final Uri BASE_CONTENT_URI =  Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES  = "movies";


    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_VIDEO_URL = "video_url";
        public static final String COLUMN_VOTECOUNT = "votes_count";
        public static final String COLUMN_VOTEAVERAGE = "votes_average";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY ="popularity";
        public static final String COLUMN_POSTERPATH = "path";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_FORMATURE = "mature";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RELEASEDATE = "release";
        public static final String COLUMN_BACKDROPPATH = "back_drop";
    }
}
