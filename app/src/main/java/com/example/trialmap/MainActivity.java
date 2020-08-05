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

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private Button mLoginButton,mSignUpButton;
    private EditText mEmailEditText,mPasswordEditText;
    private ImageView mGoogleImage;
    private TextView mForgotPassword;
    private ProgressBar progressBar;


    static final int GOOGLE_SIGN = 123;
    private FirebaseAuth mAuth;


    private GoogleSignInOptions gso;
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("637101485420-111bi58j947memm35u11gb6e61epvui4.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignIn = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        //if user is already registered
        if(mAuth.getCurrentUser() != null || signInAccount != null){
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        }



        mSignUpButton.setOnClickListener(v -> tryToSignUp());
        mLoginButton.setOnClickListener(v -> tryToLogin());
        mForgotPassword.setOnClickListener(v ->  resetPassword());
        mGoogleImage.setOnClickListener(v-> SignInGoogle());
    }


//validation
    private void validation(String email,String password){
        if (TextUtils.isEmpty(email)){
            mEmailEditText.setError("Pole nie może być puste");
            return;
        }
        // mail body doesn't fit
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailEditText.setError("Pole musi być adresem E-mail");
            return;
        }
        if (TextUtils.isEmpty(password)){
            mPasswordEditText.setError("Pole nie może być puste");
            return;
        }
        else if(password.length()<6){
            mPasswordEditText.setError("Hasło musi posiadać co najmniej 6 znaków");
            return;
        }
        // Sign up correct
        progressBar.setVisibility(View.VISIBLE);


    }
//Sign up
    private void tryToSignUp() {
        String email = mEmailEditText.getText().toString().trim();  // trim skip spaces
        String password = mPasswordEditText.getText().toString().trim();
        validation(email,password);
        if(progressBar.getVisibility() == View.VISIBLE){
            signUp(email,password);
        }

    }
    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                sendVerificationEmail();
                //new Activity
                Toast.makeText(MainActivity.this, "User created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//Login
    private void  tryToLogin(){
        String email = mEmailEditText.getText().toString().trim();  // trim skip spaces
        String password = mPasswordEditText.getText().toString().trim();
        validation(email,password);
        //progressbar handle event if is visible
        if(progressBar.getVisibility() == View.VISIBLE){
            login(email,password);
        }

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount googleAccount = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(),null);
                mAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(this,"Your Google account is connected", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                        }
                        else{
                            Toast.makeText(this,"Athentication failed " + task1.getException(), Toast.LENGTH_SHORT).show();
                        }
                });



            } catch (ApiException e){
                e.printStackTrace();
            }
        }
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
//verification email
    private void sendVerificationEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnSuccessListener(aVoid -> {
            Toast.makeText(MainActivity.this, "Veryfication mail has been sent to your email address", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,"error" + e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

}
