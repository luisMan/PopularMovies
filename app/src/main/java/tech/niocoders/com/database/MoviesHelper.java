package tech.niocoders.com.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by luism on 6/29/2018.
 */

public class MoviesHelper extends SQLiteOpenHelper {
    public static final String MOVIES_SCHEMA = "MoviesDb.db";
    public static int VERSION = 1;
    public Context context;

    MoviesHelper(Context context)
    {
        super(context,MOVIES_SCHEMA,null,VERSION); this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            final String TABLE_QUERY = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "("
                    + MovieContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY,"
                    + MovieContract.MovieEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_VOTEAVERAGE + " DECIMAL,"
                    + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_VOTECOUNT + " INTEGER,"
                    + MovieContract.MovieEntry.COLUMN_POPULARITY + " DECIMAL,"
                    + MovieContract.MovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_FORMATURE + " INTEGER,"
                    + MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL,"
                    + MovieContract.MovieEntry.COLUMN_BACKDROPPATH + " TEXT NOT NULL"
                    + ");";

            sqLiteDatabase.execSQL(TABLE_QUERY);


        }catch (SQLException e) {

            Toast.makeText(context, " Error mysqli !"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
