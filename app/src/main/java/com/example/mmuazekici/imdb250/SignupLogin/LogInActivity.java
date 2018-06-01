package com.example.mmuazekici.imdb250.SignupLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.MovieListActivity;
import com.example.mmuazekici.imdb250.R;

import java.io.IOException;

public class LogInActivity extends AppCompatActivity {

    private Button logInButton;
    private Button goToSignUpButton;

    private EditText usernameTextField;
    private EditText passwordTextField;

    private Context mContext;

    String username;
    String password;

    DatabaseHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        Boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if ( isLoggedIn){
            Intent startChildActivityIntent = new Intent(this, MovieListActivity.class);
            startActivity(startChildActivityIntent);

        }else{

            myDbHelper = new DatabaseHelper(this);

            try {
                myDbHelper.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
            try {
                myDbHelper.openDataBase();
            } catch (SQLException sqle) {
                throw sqle;
            }

            logInButton = findViewById(R.id.login_LoginButton);
            goToSignUpButton = findViewById(R.id.login_goToSignUp);

            usernameTextField = findViewById(R.id.login_usernameTextField);
            passwordTextField = findViewById(R.id.login_passwordTextField);

            mContext = getApplicationContext();

            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    username = usernameTextField.getText().toString();
                    password = passwordTextField.getText().toString();

                    LogInButtonTapped();
                }
            });

            goToSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
    }



    public void LogInButtonTapped() {


        if (!username.equals("")) {
            if (!password.equals("")) {


                Cursor mCursor = myDbHelper.query("Users",
                        new String[]{"userID", "username", "password"},
                        "username=? and password=?",
                        new String[]{username, password},
                        null);


                if (mCursor.getCount() != 0){

                    mCursor.moveToFirst();
                    String userName = mCursor.getString(mCursor.getColumnIndex("username"));
                    String userID = mCursor.getString(mCursor.getColumnIndex("userID"));

                    Toast.makeText(LogInActivity.this, "Success :) Welcome  " + userName, Toast.LENGTH_SHORT).show();


                    SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                    editor.putString("username", userName);
                    editor.putString("userID", userID);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Intent startChildActivityIntent = new Intent(this, MovieListActivity.class);
                    startChildActivityIntent.putExtra(Intent.EXTRA_UID, userID);
                    startActivity(startChildActivityIntent);



                }else{
                    Toast.makeText(LogInActivity.this, "Wrong Username or password!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LogInActivity.this, "Please enter Password! " + username, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LogInActivity.this, "Please enter Username! " + username, Toast.LENGTH_SHORT).show();
        }
    }
}
