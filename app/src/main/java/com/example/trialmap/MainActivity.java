package com.example.trialmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLoginButton;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (Button) findViewById(R.id.logIn_btn);
        mSignUpButton = (Button) findViewById(R.id.Signup_btn);
        mEmailEditText = (EditText) findViewById(R.id.email_et);
        mPasswordEditText=(EditText) findViewById(R.id.password_et);


        mLoginButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }



    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.Signup_btn: tryToSignUp(); break;
            case R.id.logIn_btn: isLoginValid(); break;
        }
    }
    //Sign up
    private void tryToSignUp() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        boolean failed = false;

        if (TextUtils.isEmpty(email)){
            mEmailEditText.setError("Pole nie może być puste");
            failed = true;
        }
        // mail body doesn't fit
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailEditText.setError("Pole musi być adresem E-mail");
            failed = true;
        }
        if (TextUtils.isEmpty(password)){
            mPasswordEditText.setError("Pole nie może być puste");
            failed = true;
        }
        // Sign up correct
        if(!failed) {
            signUp(email,password);
        }
    }

    private void signUp(String email, String password) {

    }

    //Login
    private void  isLoginValid(){

    }

}
