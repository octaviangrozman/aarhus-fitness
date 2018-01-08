package com.example.octav.aarhusfitness;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "UserSignIn";
    private static final String PREFS = "prefs";
    private static final int RC_SIGN_IN = 77;
    public static final String GOOGLE_SIGN_IN_FAILED_MESSAGE = "Google sign in failed";

    // ui
    private AutoCompleteTextView emailAutoCompleteTextView;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase auth
        auth = FirebaseAuth.getInstance();
        // firebase database
        database = FirebaseDatabase.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailAutoCompleteTextView = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);

        TextView signUpTextView = findViewById(R.id.sign_up_text_view);
        signUpTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        SignInButton signInWithGoogleButton = findViewById(R.id.google_sign_in_button);
        signInWithGoogleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signInButtonOnClick(View view) {
        signIn(String.valueOf(emailAutoCompleteTextView.getText()), String.valueOf(passwordEditText.getText()));
    }

    private void signIn(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            App.getFirebaseHelper().saveUserInDatabase(user);
                            startNextActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void startNextActivity() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String fitnessPlace = prefs.getString(App.FITNESS_PLACE_ARG, null);
        if (fitnessPlace != null) {
            Intent intent = new Intent(LoginActivity.this, FitnessPlaceActivity.class);
            intent.putExtra(App.FITNESS_PLACE_ARG, fitnessPlace);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, GOOGLE_SIGN_IN_FAILED_MESSAGE, e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            App.getFirebaseHelper().saveUserInDatabase(user);
                            startNextActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}

