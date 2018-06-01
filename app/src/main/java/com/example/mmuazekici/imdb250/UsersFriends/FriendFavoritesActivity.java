package com.example.mmuazekici.imdb250.UsersFriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.FavoritesActivity;
import com.example.mmuazekici.imdb250.MovieDetailsActivity;
import com.example.mmuazekici.imdb250.MoviesAdapter;
import com.example.mmuazekici.imdb250.R;

public class FriendFavoritesActivity extends AppCompatActivity implements MoviesAdapter.ItemAdapterOnClickHandler{

    DatabaseHelper myDbHelper;

    MoviesAdapter mAdapter;

    String friendUserID;
    String friendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_favorites);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        RecyclerView rvMovies = findViewById(R.id.rv_movies);

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this, FriendFavoritesActivity.this);
        rvMovies.setAdapter(mAdapter);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_UID)) {

            friendUserID = intentThatStartedThisActivity.getExtras().getString(Intent.EXTRA_UID);
            friendUsername = intentThatStartedThisActivity.getExtras().getString("username");

            setTitle(friendUsername + "'s Favorite Movies");
            getFriendsFavoritesTable(friendUserID);
        }
    }

    public void getFriendsFavoritesTable(String friendUserID){

        String sql = "SELECT Movies.movieName, Movies.date, Movies.imdbScore, Movies.duration, Movies.movieID " +
                "FROM Movies " +
                "INNER JOIN User_Favorites ON Movies.movieID = User_Favorites.movieID " +
                "WHERE userID=? " +
                "ORDER BY Movies.imdbScore DESC;";

        Cursor favoritesCursor = myDbHelper.rawQuery(sql, new String[] {friendUserID});

        mAdapter.swapCursor(favoritesCursor);
    }

    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){
            Intent startChildActivityIntent = new Intent(FriendFavoritesActivity.this, MovieDetailsActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_UID, c.getString(c.getColumnIndex("movieID")) + "");
            startActivity(startChildActivityIntent);
        }
    }
}
