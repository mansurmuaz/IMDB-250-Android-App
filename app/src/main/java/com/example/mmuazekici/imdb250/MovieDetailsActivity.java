package com.example.mmuazekici.imdb250;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;

public class MovieDetailsActivity extends AppCompatActivity {


    private TextView tv_Name;
    private TextView tv_Year;
    private TextView tv_Score;
    private TextView tv_Duration;
    private TextView tv_Topic;
    private TextView tv_Director;
    private TextView tv_Stars;
    private TextView tv_Genres;
    private TextView tv_PG;
    private TextView tv_Studios;

    private FloatingActionButton favoriteButton;

    private String movieID;

    DatabaseHelper myDbHelper;

    private boolean isFavorite;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        tv_Name = findViewById(R.id.tv_movieName);
        tv_Year = findViewById(R.id.tv_movieYear);
        tv_Score = findViewById(R.id.tv_movieScore);
        tv_Duration = findViewById(R.id.tv_movieDuration);
        tv_Topic = findViewById(R.id.tv_movieTopic);
        tv_Director = findViewById(R.id.tv_movieDirector);
        tv_Stars = findViewById(R.id.tv_movieStars);
        tv_Genres = findViewById(R.id.tv_movieGenres);
        tv_PG = findViewById(R.id.tv_moviePG);
        tv_Studios = findViewById(R.id.tv_movieStudios);

        favoriteButton = findViewById(R.id.fab_favoriteButton);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        userID = prefs.getString("userID", null);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_UID)) {

            movieID = intentThatStartedThisActivity.getExtras().getString(Intent.EXTRA_UID);

            getMovieTable();
            getGenreTable();
            getPGTable();
            getDirectsTable();
            getStarsTable();
            getStudiosTable();
            getFavoriteTable();
        }


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFavorite){

                    myDbHelper.delete("User_Favorites", "movieID=? AND userID=?", new String[] {movieID, userID});

                }else{

                    ContentValues favoriteValues = new ContentValues();
                    favoriteValues.put("userID", userID);
                    favoriteValues.put("movieID", movieID);

                    myDbHelper.insert("User_Favorites", favoriteValues);

                }

                getFavoriteTable();
            }
        });

    }


    public void getMovieTable(){
        Cursor movieCursor = myDbHelper.query("Movies",
                null,
                "movieID=?",
                new String[]{movieID},
                null);

        if (movieCursor.getCount() != 0) {

            movieCursor.moveToFirst();

            tv_Name.setText(movieCursor.getString(movieCursor.getColumnIndex("movieName")));
            tv_Year.setText(movieCursor.getString(movieCursor.getColumnIndex("date")));
            tv_Score.setText(movieCursor.getString(movieCursor.getColumnIndex("imdbScore")));
            tv_Duration.setText(movieCursor.getString(movieCursor.getColumnIndex("duration")));
            tv_Topic.setText(movieCursor.getString(movieCursor.getColumnIndex("topic")));

        }
    }

    public void getGenreTable(){

        String sql = "SELECT Genres.genreName " +
                     "FROM Genres " +
                     "INNER JOIN Movie_Genres ON Genres.genreID = Movie_Genres.GenreID "+
                     "WHERE movieID=? ;";
        Cursor genreCursor = myDbHelper.rawQuery(sql, new String[]{movieID});

        if (genreCursor.getCount() != 0) {

            genreCursor.moveToFirst();

            String genres = "";

            for (int i = 0; i<genreCursor.getCount(); i++){
                genres = genres + genreCursor.getString(genreCursor.getColumnIndex("genreName")) + "\n";
                genreCursor.moveToNext();
            }
            tv_Genres.setText(removeLastChar(genres));

        }
    }

    public void getPGTable(){

        String sql = "SELECT ParentalGuides.ParentalGuideName " +
                "FROM ParentalGuides " +
                "INNER JOIN Movie_ParentalGuides ON ParentalGuides.ParentalGuideID = Movie_ParentalGuides.ParentalGuideID "+
                "WHERE movieID=? ;";
        Cursor pGCursor = myDbHelper.rawQuery(sql, new String[]{movieID});

        if (pGCursor.getCount() != 0) {

            pGCursor.moveToFirst();

            String pG = "";

            for (int i = 0; i<pGCursor.getCount(); i++){
                pG = pG + pGCursor.getString(pGCursor.getColumnIndex("ParentalGuideName")) + "\n";
                pGCursor.moveToNext();
            }
            tv_PG.setText(removeLastChar(pG));

        }
    }


    public void getDirectsTable(){

        String sql = "SELECT People.name, People.surname " +
                "FROM People " +
                "INNER JOIN Directs ON People.personID = Directs.personID "+
                "WHERE movieID=? ;";

        Cursor directsCursor = myDbHelper.rawQuery(sql, new String[]{movieID});

        if (directsCursor.getCount() != 0) {

            directsCursor.moveToFirst();

            String director = "";

            for (int i = 0; i<directsCursor.getCount(); i++){
                director = director + directsCursor.getString(directsCursor.getColumnIndex("name")) + " " +
                        directsCursor.getString(directsCursor.getColumnIndex("surname")) + "\n";
                directsCursor.moveToNext();
            }
            tv_Director.setText(removeLastChar(director));

        }
    }


    public void getStarsTable(){

        String sql = "SELECT People.name, People.surname, Casts.role " +
                "FROM People " +
                "INNER JOIN Casts ON People.personID = Casts.personID "+
                "WHERE movieID=? ;";

        Cursor castsCursor = myDbHelper.rawQuery(sql, new String[]{movieID});

        if (castsCursor.getCount() != 0) {

            castsCursor.moveToFirst();

            String director = "";

            for (int i = 0; i<castsCursor.getCount(); i++){

                String role = castsCursor.getString(castsCursor.getColumnIndex("role"));

                if (role.equals("Null")){
                    role = "";
                }

                director = director + castsCursor.getString(castsCursor.getColumnIndex("name")) + " " +
                        castsCursor.getString(castsCursor.getColumnIndex("surname")) + " \t|\t " + role + "\n";
                castsCursor.moveToNext();
            }
            tv_Stars.setText(removeLastChar(director));

        }
    }

    public void getStudiosTable(){

        String sql = "SELECT Studios.studioName " +
                     "FROM Studios " +
                     "INNER JOIN Movie_Studios ON Studios.studioID = Movie_Studios.studioID " +
                     "WHERE movieID=? ;";

        Cursor studiosCursor = myDbHelper.rawQuery(sql, new String[] {movieID});

        if (studiosCursor.getCount() != 0) {
            studiosCursor.moveToFirst();

            String studios = "";

            for (int i = 0; i<studiosCursor.getCount(); i++){
                studios = studios + studiosCursor.getString(studiosCursor.getColumnIndex("studioName")) + "\n";
                studiosCursor.moveToNext();
            }
            tv_Studios.setText(removeLastChar(studios));
        }
    }



    public void getFavoriteTable(){

        if (userID != null){

            Cursor favoriteCursor = myDbHelper.query("User_Favorites",
                    null,
                    "movieID=? AND userID=?",
                    new String[] {movieID, userID},
                    null);

            if (favoriteCursor.getCount() == 0) {
                // Not favorite
                isFavorite = false;
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            }else{
                //Favorite
                isFavorite = true;
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
        }
    }




    public String removeLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
