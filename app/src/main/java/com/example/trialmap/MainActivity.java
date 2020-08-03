package com.example.trialmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



public class MainActivity extends AppCompatActivity {

    private Button mLoginButton;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ImageView mGoogleImage;
    private TextView mForgotPassword;


    static final int GOOGLE_SIGN = 123;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignIn;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLoginButton = (Button) findViewById(R.id.logIn_btn);
        mSignUpButton = (Button) findViewById(R.id.Signup_btn);
        mEmailEditText = (EditText) findViewById(R.id.email_et);
        mPasswordEditText=(EditText) findViewById(R.id.password_et);
        mGoogleImage = (ImageView) findViewById(R.id.Google_image);
        progressBar = (ProgressBar) findViewById(R.id.loading_pg);
        mForgotPassword = (TextView) findViewById(R.id.forgotPassword);



        mAuth = FirebaseAuth.getInstance();

        //if user is already registered
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        }

        /*GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignIn = GoogleSignIn.getClient(this,googleSignInOptions);

        */

        mSignUpButton.setOnClickListener(v -> tryToSignUp());
        mLoginButton.setOnClickListener(v -> tryToLogin());
        mForgotPassword.setOnClickListener(v ->  resetPassword());
    }


//validation
    private void validation(){
        String email = mEmailEditText.getText().toString().trim();  // trim skip spaces
        String password = mPasswordEditText.getText().toString().trim();
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
        if(password.length()<6){
            mPasswordEditText.setError("Hasło musi posiadać co najmniej 6 znaków");
            failed = true;
        }
        // Sign up correct
        if(!failed) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
//Sign up
    private void tryToSignUp() {
        String email = mEmailEditText.getText().toString().trim();  // trim skip spaces
        String password = mPasswordEditText.getText().toString().trim();
        validation();
        signUp(email,password);
    }
    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {

                //veryfication email
                FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Veryfication mail has been sent to your email address", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this,"error" + e.getMessage(),Toast.LENGTH_SHORT).show();
                });
                //new Activity
                Toast.makeText(MainActivity.this, "User created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//Login
    private void  tryToLogin(){
        String email = mEmailEditText.getText().toString().trim();  // trim skip spaces
        String password = mPasswordEditText.getText().toString().trim();
        validation();
        login(email,password);
    }
    private void login(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(MainActivity.this, "User login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

//Google
    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignIn.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);

    }
//resetPassword
    private void resetPassword(){
        String email = mEmailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            mEmailEditText.setError("Pole nie może być puste");
        }
        // mail body doesn't fit
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailEditText.setError("Pole musi być adresem E-mail");
        }
        else{
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if( account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("Tag","sigin success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        UpdateUI(user);
                    }
                    else{
                        progressBar.setVisibility(View.VISIBLE);
                        Log.w("TAG", "sigin failure",task.getException());

                        Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                        UpdateUI(null);
;                    }
                });

    }

    private void UpdateUI(FirebaseUser user) {
        if(user != null ){
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            text.append("Info: \n");
            text.append(name + "\n");
            text.append(email);



        }else{
            text.setText("Firebase Login \n");
            mGoogleImage.setVisibility(View.INVISIBLE);
        }
    }
}
