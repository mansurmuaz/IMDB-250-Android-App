package com.example.mmuazekici.imdb250;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MoviesAdapter.ItemAdapterOnClickHandler {

    Spinner searchSpinner;
    Spinner itemSpinner;

    EditText searchBarTextField;

    RecyclerView rvMovies;

    Button searchButton;

    String selectedSearchString;
    String selectedItemString;
    int selectedItemPosition;

    DatabaseHelper myDbHelper;

    MoviesAdapter mAdapter;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchSpinner = findViewById(R.id.search_spinner);
        itemSpinner = findViewById(R.id.item_spinner);

        searchBarTextField = findViewById(R.id.et_searchBar);

        rvMovies = findViewById(R.id.rv_movies);

        searchButton = findViewById(R.id.searchButton);

        setSearchBySpinner();

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mAdapter);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMoviesInDB();
            }
        });

    }


    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){
            Intent startChildActivityIntent = new Intent(this, MovieDetailsActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_UID, c.getString(c.getColumnIndex("movieID")));
            startActivity(startChildActivityIntent);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



        switch(parent.getId()) {
            case R.id.search_spinner:

                searchBarTextField.setText("");
                selectedSearchString = parent.getItemAtPosition(position).toString();

                if (selectedSearchString.equals("Movie Name") || selectedSearchString.equals("Star") ||
                        selectedSearchString.equals("Director") || selectedSearchString.equals("Studio")){

                    searchBarTextField.setVisibility(View.VISIBLE);
                    itemSpinner.setVisibility(View.INVISIBLE);
                    searchBarTextField.setHint(selectedSearchString);

                } else {
                    itemSpinner.setVisibility(View.VISIBLE);
                    searchBarTextField.setVisibility(View.INVISIBLE);
                    searchBarTextField.setText("");

                    String[] itemsArray;

                    if (selectedSearchString.equals("Genre")) {
                        itemsArray = getResources().getStringArray(R.array.genre);
                    } else if (selectedSearchString.equals("PG")) {
                        itemsArray = getResources().getStringArray(R.array.pg);
                    } else if (selectedSearchString.equals("IMDB Score")) {
                        itemsArray = getResources().getStringArray(R.array.imdb);
                    } else {
                        itemsArray = getResources().getStringArray(R.array.year);
                    }

                    setItemSpinner(itemsArray);
                }
                break;
            case R.id.item_spinner:

                selectedItemString = parent.getItemAtPosition(position).toString();
                selectedItemPosition = parent.getSelectedItemPosition();

                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void setSearchBySpinner(){

        String[] searchByItems = getResources().getStringArray(R.array.search_by);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                searchByItems);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchSpinner.setAdapter(spinnerAdapter);
        searchSpinner.setOnItemSelectedListener(this);
    }

    public void setItemSpinner(String[] items){
        ArrayAdapter<String> itemSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                items);

        itemSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemSpinner.setAdapter(itemSpinnerAdapter);
        itemSpinner.setOnItemSelectedListener(this);
    }


    public void searchMoviesInDB(){

        String searchingString = "%"+searchBarTextField.getText().toString()+"%";
        String[] searchingArray = new String[]{"%"+searchBarTextField.getText().toString()+"%"};

        String itemString = selectedItemString;
        String[] itemArray = new String[] {selectedItemString};

        if (selectedSearchString.equals("Movie Name")){

            String sql = "SELECT * " +
                         "FROM Movies " +
                         "WHERE movieName LIKE ?;";

            mCursor = myDbHelper.rawQuery(sql, searchingArray);

        } else if (selectedSearchString.equals("Star")){

            String sql = "SELECT * " +
                    "FROM Casts " +
                    "INNER JOIN Movies ON Casts.movieID = Movies.movieID " +
                    "INNER JOIN People ON Casts.personID = People.personID " +
                    "WHERE People.name LIKE ? OR People.surname LIKE ? OR Casts.role LIKE ? " +
                    "GROUP BY Movies.movieName;";


            mCursor = myDbHelper.rawQuery(sql, new String[] {searchingString, searchingString, searchingString});

        } else if (selectedSearchString.equals("Director")) {

            String sql = "SELECT * " +
                    "FROM Directs " +
                    "INNER JOIN Movies ON Directs.movieID = Movies.movieID " +
                    "INNER JOIN People ON Directs.personID = People.personID " +
                    "WHERE People.name LIKE ? OR People.surname LIKE ?" +
                    "GROUP BY Movies.movieName;";

            mCursor = myDbHelper.rawQuery(sql, new String[] {searchingString, searchingString});

        } else if (selectedSearchString.equals("Studio")) {

            String sql = "SELECT * " +
                    "FROM Movie_Studios " +
                    "INNER JOIN Movies ON Movie_Studios.movieID = Movies.movieID " +
                    "INNER JOIN Studios ON Movie_Studios.studioID = Studios.studioID " +
                    "WHERE Studios.studioName LIKE ? " +
                    "GROUP BY Movies.movieName;";

            mCursor = myDbHelper.rawQuery(sql, searchingArray);
        } else if (selectedSearchString.equals("Genre")){



            String sql = "SELECT * " +
                    "FROM Movie_Genres " +
                    "INNER JOIN Movies ON Movie_Genres.movieID = Movies.movieID " +
                    "INNER JOIN Genres ON Movie_Genres.genreID = Genres.genreID " +
                    "WHERE Genres.genreName =? " +
                    "GROUP BY Movies.movieName;";

            mCursor = myDbHelper.rawQuery(sql, itemArray);
        } else if (selectedSearchString.equals("PG")){

            String sql = "SELECT * " +
                    "FROM Movie_ParentalGuides " +
                    "INNER JOIN Movies ON Movie_ParentalGuides.movieID = Movies.movieID " +
                    "INNER JOIN ParentalGuides ON Movie_ParentalGuides.parentalGuideID = ParentalGuides.ParentalGuideID " +
                    "WHERE ParentalGuides.ParentalGuideName =? " +
                    "GROUP BY Movies.movieName;";

            mCursor = myDbHelper.rawQuery(sql, itemArray);

        }else if (selectedSearchString.equals("IMDB Score")){

            String sql = "SELECT * " +
                    "FROM Movies " +
                    "WHERE imdbScore =?;";

            mCursor = myDbHelper.rawQuery(sql, itemArray);

        }else if (selectedSearchString.equals("Year")){

            String baseSql = "SELECT * " +
                    "FROM Movies " +
                    "WHERE date ";

            String year;


            if (selectedItemPosition == 0){
                year = ">= 2000;";
            }else if (selectedItemPosition == 1){
                year = ">= 1990 AND date <= 1999;";
            }else if (selectedItemPosition == 2){
                year = ">= 1980 AND date <= 1989;";
            }else if (selectedItemPosition == 3){
                year = ">= 1970 AND date <= 1979;";
            }else if (selectedItemPosition == 4){
                year = ">= 1960 AND date <= 1969;";
            }else {
                year = ">= 1950 AND date <= 1959;";
            }

            String sql = baseSql + year;
            mCursor = myDbHelper.rawQuery(sql, null);

        }


        mAdapter.swapCursor(mCursor);
    }
}
