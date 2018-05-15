package com.example.mmuazekici.imdb250;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;

import java.io.IOException;

public class MovieListActivity extends AppCompatActivity{

    Cursor mCursor = null;
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

        mAdapter = new MoviesAdapter(this);
        rvMovies.setAdapter(mAdapter);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username != null){
            usernameTextField.setText("Welcome " + username);
        }else{
            usernameTextField.setText("Welcome");
        }



        Cursor mCursor = myDbHelper.query(DatabaseConract.MoviesTable.TABLE_NAME, null, null, null, null);

        mAdapter.swapCursor(mCursor);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_movie_list, menu);


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

        }
        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onBackPressed() {
    }

    //    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Uri userURI = DatabaseConract.MoviesTable.CONTENT_URI;
//
//        return new CursorLoader(this,
//                userURI,
//                null,
//                null,
//                null,
//                null);
//
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAdapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
//    }
}
