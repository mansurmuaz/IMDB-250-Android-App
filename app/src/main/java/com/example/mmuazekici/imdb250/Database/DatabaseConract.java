package com.example.mmuazekici.imdb250.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseConract {


    public static final class UsersTable implements BaseColumns {

        public static final String TABLE_NAME                   = "Users";

        public static final String COLUMN_USER_ID               = "userID";
        public static final String COLUMN_USER_NAME             = "username";
        public static final String COLUMN_PASSWORD              = "password";

    }

    public static final class MoviesTable implements BaseColumns {

        public static final String TABLE_NAME                   = "Movies";

        public static final String COLUMN_MOVIE_ID              = "movieID";
        public static final String COLUMN_MOVIE_NAME            = "movieName";
        public static final String COLUMN_DATE                  = "date";
        public static final String COLUMN_DURATION              = "duration";
        public static final String COLUMN_IMDB_SCORE            = "imdbScore";
        public static final String COLUMN_TOPIC                 = "topic";
    }


}
