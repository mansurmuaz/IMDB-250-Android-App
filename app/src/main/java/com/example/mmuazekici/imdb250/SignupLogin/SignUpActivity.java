package com.example.mmuazekici.imdb250.SignupLogin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mmuazekici.imdb250.Database.DatabaseConract;
import com.example.mmuazekici.imdb250.Database.DatabaseHelper;
import com.example.mmuazekici.imdb250.R;

public class SignUpActivity extends AppCompatActivity {


    private Button signInButton;
    private Button goToLoginButton;

    private EditText usernameTextField;
    private EditText passwordTextField;
    private EditText confirmPasswordTextField;

    private Context mContext;

    String username;
    String password;
    String confirmPassword;

    DatabaseHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        signInButton = findViewById(R.id.signUpButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        usernameTextField = findViewById(R.id.usernameTextField);
        passwordTextField = findViewById(R.id.passwordTextField);
        confirmPasswordTextField = findViewById(R.id.confirmPasswordTextField);

        mContext = getApplicationContext();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButtonTapped();
            }
        });

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            }
        });
    }

    public void signInButtonTapped(){

        if(isInputsValid()) {
            if (myDbHelper.query(DatabaseConract.UsersTable.TABLE_NAME, null, "username=?", new String[]{username}, null).getCount() == 0){

                ContentValues userValues = new ContentValues();
                userValues.put(DatabaseConract.UsersTable.COLUMN_USER_NAME, username);
                userValues.put(DatabaseConract.UsersTable.COLUMN_PASSWORD, password);

                myDbHelper.insert(DatabaseConract.UsersTable.TABLE_NAME, userValues);

                Toast.makeText(SignUpActivity.this, "Signed Up Succesfully. Please Log In!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));


            } else {
                Toast.makeText(SignUpActivity.this, "This username has taken before! Please enter new username.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean isInputsValid(){

        //TODO: Check special charackters...

        username = usernameTextField.getText().toString();
        password = passwordTextField.getText().toString();
        confirmPassword = confirmPasswordTextField.getText().toString();

        if (!username.equals("")) {
            if (!password.equals("") && password.length() >= 6){
                if (password.equals(confirmPassword)) {
                    return true;
                }else{
                    Toast.makeText(SignUpActivity.this, "Passwords should be same!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(SignUpActivity.this, "Password cannot be less than 6 character!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(SignUpActivity.this, "Please Enter Username!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
