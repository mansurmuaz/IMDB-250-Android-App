package com.example.mmuazekici.imdb250.UsersFriends;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.MovieListActivity;
import com.example.mmuazekici.imdb250.R;

public class UsersActivity extends AppCompatActivity implements UsersAdapter.ItemAdapterOnClickHandler{


    DatabaseHelper myDbHelper;

    UsersAdapter mAdapter;
    String userID;

    String defaultMessage = "Do you want to send friendship request to ";
    String rejectedMessage = "Your request has been rejected. Do you want to send friendship request again to ";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        RecyclerView rvUsers = findViewById(R.id.rv_users);

        LinearLayoutManager layoutManager = new GridLayoutManager(this,1);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setHasFixedSize(true);

        mAdapter = new UsersAdapter(this, UsersActivity.this);
        rvUsers.setAdapter(mAdapter);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        userID = prefs.getString("userID", null);

        Cursor mCursor = myDbHelper.query("Users", null, "userID<>?", new String[] {userID}, "username"+ " DESC");
        mAdapter.swapCursor(mCursor);
    }

    @Override
    public void onClick(Cursor c, int clickedItemIndex) {
        if(c.moveToPosition(clickedItemIndex)){

            String clickedUserID = c.getString(c.getColumnIndex("userID"));
            String clickedUsername = c.getString(c.getColumnIndex("username"));

            //getFriendship data
            Cursor friendCursor = myDbHelper.query("User_Friends", null, "userID=? and friendID=?", new String[] {userID, clickedUserID}, null);

            //if cursor is empty -->> Insert data to user_friends with state -1

            if (friendCursor.getCount()==0){
                sendFriendshipRequestAlert(clickedUserID, clickedUsername, -1, defaultMessage);

            }else{

                friendCursor.moveToFirst();
                int stateID = friendCursor.getInt(friendCursor.getColumnIndex("stateID"));

                if (stateID == 0){ //Cancelled
                    sendFriendshipRequestAlert(clickedUserID, clickedUsername, 0, defaultMessage);
                }else if (stateID == 1){ //Pending
                    sendPendingAlert(clickedUserID, clickedUsername);
                }else if (stateID == 2){ //Rejected
                    sendFriendshipRequestAlert(clickedUserID, clickedUsername, 0, rejectedMessage);
                }else if (stateID == 3){ //Already Friends
                    sendFriendAlert(clickedUserID, clickedUsername);
                }

            }
        }
    }

    public void insertFriendshipDB(String clickedUserID){
        ContentValues friendValues = new ContentValues();
        friendValues.put("userID", userID);
        friendValues.put("friendID", clickedUserID);
        friendValues.put("stateID", 1);

        myDbHelper.insert("User_Friends", friendValues);
    }

    public void updateFriendshipDb(String clickedUserID, int stateID){

        ContentValues friendValues = new ContentValues();
        friendValues.put("stateID", stateID);

        myDbHelper.update(friendValues, "User_Friends", "userID=? and friendID=?", new String[] {userID, clickedUserID});
    }



    public void sendFriendshipRequestAlert(final String clickedUserID, String username, final int action, String message){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this , android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Send Friendship Request")
                .setMessage(message + username +"?")
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (action == -1){
                            insertFriendshipDB(clickedUserID);
                        }else{
                            updateFriendshipDb(clickedUserID, 1);
                        }
                        Toast.makeText(UsersActivity.this, "Your request has been sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.users)
                .show();
    }

    public void sendPendingAlert(final String clickedUserID, String username){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this , android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Pending")
                .setMessage("Your friendship request waiting for response by " + username +".")
                .setPositiveButton("Cancel Request", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        updateFriendshipDb(clickedUserID, 0);

                        Toast.makeText(UsersActivity.this, "Your request is canceled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.pending)
                .show();
    }

    public void sendFriendAlert(final String clickedUserID, final String username){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this , android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(username)
                .setMessage("You are already friend by " + username +".")
                .setPositiveButton("Remove Friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        updateFriendshipDb(clickedUserID, 0);

                        Toast.makeText(UsersActivity.this, "You are no longer friends with " + username + ".", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Display Favorites", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent startChildActivityIntent = new Intent(UsersActivity.this, FriendFavoritesActivity.class);
                        startChildActivityIntent.putExtra(Intent.EXTRA_UID, clickedUserID);
                        startChildActivityIntent.putExtra("username", username);
                        startActivity(startChildActivityIntent);
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.approved)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_users, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.friends){

            Intent startChildActivityIntent = new Intent(this, FriendsActivity.class);
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

