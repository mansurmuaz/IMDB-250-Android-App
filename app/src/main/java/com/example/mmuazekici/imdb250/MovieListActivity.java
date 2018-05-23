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
import android.widget.TextView;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;
import com.example.mmuazekici.imdb250.UsersFriends.FriendsActivity;
import com.example.mmuazekici.imdb250.UsersFriends.UsersActivity;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.ItemAdapterOnClickHandler{

    DatabaseHelper myDbHelper;

    MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        RecyclerView rvMovies = findViewById(R.id.rv_movies);
        TextView usernameTextField = findViewById(R.id.tv_welcome);

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this, MovieListActivity.this);
        rvMovies.setAdapter(mAdapter);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username != null){
            usernameTextField.setText("Welcome " + username);
        }else{
            usernameTextField.setText("Welcome");
        }



        Cursor mCursor = myDbHelper.query(DatabaseConract.MoviesTable.TABLE_NAME, null, null, null, DatabaseConract.MoviesTable.COLUMN_IMDB_SCORE+ " DESC");

        mAdapter.swapCursor(mCursor);
    }


    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){
            Intent startChildActivityIntent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_UID, c.getString(c.getColumnIndex(DatabaseConract.MoviesTable.COLUMN_MOVIE_ID)) + "");
            startActivity(startChildActivityIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_movie_list, menu);


        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.favorites){

            Intent startChildActivityIntent = new Intent(this, FavoritesActivity.class);
            startActivity(startChildActivityIntent);

        } else if (id == R.id.search) {

            Intent startChildActivityIntent = new Intent(this, SearchActivity.class);
            startActivity(startChildActivityIntent);

        } else if (id == R.id.userProfile) {

            Intent startChildActivityIntent = new Intent(this, ProfileActivity.class);
            startActivity(startChildActivityIntent);


        } else if (id == R.id.logout) {

            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.clear().apply();


            Intent startChildActivityIntent = new Intent(this, LogInActivity.class);
            startActivity(startChildActivityIntent);

        } else if (id == R.id.users){
            Intent startChildActivityIntent = new Intent(this, UsersActivity.class);
            startActivity(startChildActivityIntent);
        } else if (id == R.id.friends){
            Intent startChildActivityIntent = new Intent(this, FriendsActivity.class);
            startActivity(startChildActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onBackPressed() {
    }


}
