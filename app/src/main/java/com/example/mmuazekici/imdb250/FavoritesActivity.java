package com.example.mmuazekici.imdb250;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;

public class FavoritesActivity extends AppCompatActivity implements MoviesAdapter.ItemAdapterOnClickHandler{

    DatabaseHelper myDbHelper;

    MoviesAdapter mAdapter;

    String userID;
    boolean isHaveUserID = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


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

        mAdapter = new MoviesAdapter(this, FavoritesActivity.this);
        rvMovies.setAdapter(mAdapter);


        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        userID = prefs.getString("userID", null);
        isHaveUserID = true;

        getFavoritesTable(userID);

        }


    public void getFavoritesTable(String userID){

        String sql = "SELECT Movies.movieName, Movies.date, Movies.imdbScore, Movies.duration, Movies.movieID " +
                     "FROM Movies " +
                     "INNER JOIN User_Favorites ON Movies.movieID = User_Favorites.movieID " +
                     "WHERE userID=? " +
                     "ORDER BY Movies.imdbScore DESC;";

        Cursor favoritesCursor = myDbHelper.rawQuery(sql, new String[] {userID});

        mAdapter.swapCursor(favoritesCursor);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (isHaveUserID){
            getFavoritesTable(userID);
        }
    }

    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){
            Intent startChildActivityIntent = new Intent(FavoritesActivity.this, MovieDetailsActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_UID, c.getString(c.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_MOVIE_ID)) + "");
            startActivity(startChildActivityIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_favorites_list, menu);


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.userProfile) {

            //TODO: Go to user profile

        } else if (id == R.id.logout) {

            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.clear().apply();


            Intent startChildActivityIntent = new Intent(this, LogInActivity.class);
            startActivity(startChildActivityIntent);

        } else if (id == R.id.home){

            Intent startChildActivityIntent = new Intent(this, MovieListActivity.class);
            startActivity(startChildActivityIntent);

        }
        return super.onOptionsItemSelected(item);
    }

}
