package com.example.mmuazekici.imdb250;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;
import com.example.mmuazekici.imdb250.UsersFriends.FriendsActivity;
import com.example.mmuazekici.imdb250.UsersFriends.UsersActivity;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.ItemAdapterOnClickHandler{

    DatabaseHelper myDbHelper;

    MoviesAdapter mAdapter;
    String username;
    String userID;

    Cursor requestCursor;

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
        username = prefs.getString("username", null);
        userID = prefs.getString("userID", null);
        if (username != null){
            usernameTextField.setText("Welcome " + username);
        }else{
            usernameTextField.setText("Welcome");
        }

        Cursor mCursor = myDbHelper.query(DatabaseConract.MoviesTable.TABLE_NAME, null, null, null, DatabaseConract.MoviesTable.COLUMN_IMDB_SCORE+ " DESC");
        mAdapter.swapCursor(mCursor);


        //TODO: Check the friendship request
        int requestCount = checkRequests();
        requestCursor.moveToFirst();
        for (int index = 0; index<requestCount; index++){
            String requestID = requestCursor.getString(requestCursor.getColumnIndex("userID"));
            String requestName = requestCursor.getString(requestCursor.getColumnIndex("username"));
            sendRequestResponseAlert(requestID, requestName);
            requestCursor.moveToNext();
        }
    }



    public int checkRequests() {

        String sql = "SELECT Users.userID, Users.username " +
                     "FROM Users " +
                     "INNER JOIN User_Friends ON Users.userID = User_Friends.userID " +
                     "WHERE User_Friends.friendID=? AND stateID=1 " +
                     "ORDER BY Users.username DESC;";

        requestCursor = myDbHelper.rawQuery(sql, new String[]{userID});

        return requestCursor.getCount();
    }


    public void sendRequestResponseAlert(final String requestID, final String requestUsername){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this , android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Friendship Request")
                .setMessage(requestUsername +" sent you friendship request. Do you want to be friend with him/her?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateFriendshipDb(requestID, 3);
                        Toast.makeText(MovieListActivity.this, "You are friend with " + requestUsername + " now!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateFriendshipDb(requestID, 2);
                        Toast.makeText(MovieListActivity.this, "You rejected friendship request of " + requestUsername + "!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(R.drawable.users)
                .setCancelable(false)
                .show();
    }

    public void updateFriendshipDb(String requestID, int stateID){

        ContentValues friendValues = new ContentValues();
        friendValues.put("stateID", stateID);

        myDbHelper.update(friendValues, "User_Friends", "userID=? and friendID=?", new String[] {requestID, userID});
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
