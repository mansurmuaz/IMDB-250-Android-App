package com.example.mmuazekici.imdb250;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.SignupLogin.LogInActivity;
import com.example.mmuazekici.imdb250.SignupLogin.SignUpActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {


    private TextView usernameTextView;

    private EditText passwordTextField;
    private EditText confirmTextField;

    private Button changeButton;
    private Button deleteButton;

    DatabaseHelper myDbHelper;

    String userID;
    String username;

    String password;
    String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }


        usernameTextView = findViewById(R.id.tv_pUsername);
        passwordTextField = findViewById(R.id.et_pPassword);
        confirmTextField = findViewById(R.id.et_pConfirm);
        changeButton = findViewById(R.id.btn_pChangeButton);
        deleteButton = findViewById(R.id.btn_pDeleteButton);

        SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
        userID = prefs.getString("userID", null);
        username = prefs.getString("username", null);

        usernameTextView.setText(username);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonTapped();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonTapped();
            }
        });

    }


    public void changeButtonTapped(){
        if(isInputsValid()) {

            ContentValues userValues = new ContentValues();
            userValues.put("password", password);

            myDbHelper.update(userValues, "Users","userID=?", new String[]{userID});

            Toast.makeText(ProfileActivity.this, "Password is changed! Log in again!", Toast.LENGTH_SHORT).show();



            SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
            editor.clear().apply();

            Intent startChildActivityIntent = new Intent(this, LogInActivity.class);
            startActivity(startChildActivityIntent);
        }
    }

    public void deleteButtonTapped(){



        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this , android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        myDbHelper.delete("Users", "userID=?", new String[]{userID});

                        Toast.makeText(ProfileActivity.this, "Account is removed!", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                        editor.clear().apply();

                        Intent startChildActivityIntent = new Intent(ProfileActivity.this, LogInActivity.class);
                        startActivity(startChildActivityIntent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }


    public boolean isInputsValid(){

        password = passwordTextField.getText().toString();
        confirmPassword = confirmTextField.getText().toString();

        if (!password.equals("") && password.length() >= 6){
            if (!isContainSpecialChar(password)) {
                if (password.equals(confirmPassword)) {
                    return true;
                } else {
                    Toast.makeText(ProfileActivity.this, "Passwords should be same!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(ProfileActivity.this, "Password cannot contain special characters!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(ProfileActivity.this, "Password cannot be less than 6 character!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public boolean isContainSpecialChar(String s){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean b = m.find();
        return b;
    }
}
