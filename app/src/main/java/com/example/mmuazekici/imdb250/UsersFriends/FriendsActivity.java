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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.FavoritesActivity;
import com.example.mmuazekici.imdb250.MovieDetailsActivity;
import com.example.mmuazekici.imdb250.MovieListActivity;
import com.example.mmuazekici.imdb250.ProfileActivity;
import com.example.mmuazekici.imdb250.R;
import com.example.mmuazekici.imdb250.SearchActivity;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;

public class FriendsActivity extends AppCompatActivity implements UsersAdapter.ItemAdapterOnClickHandler{


    DatabaseHelper myDbHelper;

    UsersAdapter mAdapter;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        RecyclerView rvUsers = findViewById(R.id.rv_friends);
        TextView noFriendsTextView = findViewById(R.id.tv_noFriends);

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setHasFixedSize(true);

        mAdapter = new UsersAdapter(this, FriendsActivity.this);
        rvUsers.setAdapter(mAdapter);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        userID = prefs.getString("userID", null);


        String sql = "SELECT Users.userID, Users.username " +
                     "FROM Users " +
                     "INNER JOIN User_Friends ON Users.userID = User_Friends.friendID " +
                     "WHERE User_Friends.userID=? AND stateID=3 " +
                     "ORDER BY Users.username DESC;";

        Cursor mCursor = myDbHelper.rawQuery(sql, new String[] {userID});
        mAdapter.swapCursor(mCursor);


        if (mCursor.getCount() == 0){
            noFriendsTextView.setVisibility(View.VISIBLE);
        }else{
            noFriendsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){
            //TODO: send to friends favorites

            Intent startChildActivityIntent = new Intent(FriendsActivity.this, FriendFavoritesActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_UID, c.getString(c.getColumnIndex("userID")));
            startChildActivityIntent.putExtra("username", c.getString(c.getColumnIndex("username")));
            startActivity(startChildActivityIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.users){

            Intent startChildActivityIntent = new Intent(this, UsersActivity.class);
            startActivity(startChildActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent startChildActivityIntent = new Intent(this, MovieListActivity.class);
        startActivity(startChildActivityIntent);
    }
}
