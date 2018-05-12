package com.example.mmuazekici.imdb250;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;

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

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this);
        rvMovies.setAdapter(mAdapter);


        Cursor mCursor = myDbHelper.query(DatabaseConract.MoviesTable.TABLE_NAME, null, null, null, null);

        mAdapter.swapCursor(mCursor);
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
