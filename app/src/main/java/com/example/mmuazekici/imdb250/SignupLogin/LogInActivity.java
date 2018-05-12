package com.example.mmuazekici.imdb250.SignupLogin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
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


    public void LogInButtonTapped() {


        if (!username.equals("")) {
            if (!password.equals("")) {


                Cursor mCursor = myDbHelper.query(DatabaseConract.UsersTable.TABLE_NAME,
                        new String[]{DatabaseConract.UsersTable.COLUMN_USER_ID, DatabaseConract.UsersTable.COLUMN_USER_NAME,DatabaseConract.UsersTable.COLUMN_PASSWORD},
                        "username=? and password=?",
                        new String[]{username, password},
                        null);


                if (mCursor.getCount() != 0){

                    mCursor.moveToFirst();
                    String userName = mCursor.getString(mCursor.getColumnIndex(DatabaseConract.UsersTable.COLUMN_USER_NAME));
                    String userID = mCursor.getString(mCursor.getColumnIndex(DatabaseConract.UsersTable.COLUMN_USER_ID));

                    Toast.makeText(LogInActivity.this, "Success :) Welcome  " + userName, Toast.LENGTH_SHORT).show();
                    //TODO: Go to welcome page with ID

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
