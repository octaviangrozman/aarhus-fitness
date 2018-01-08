package com.example.octav.aarhusfitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "UserSignUp";
    private static final String PREFS = "prefs";
    public static final String SUCCESS_MESSAGE = "Your account has been succesfully created";

    private FirebaseAuth auth;

    // ui
    private AutoCompleteTextView emailAutoCompleteTextView;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // firebase auth
        auth = FirebaseAuth.getInstance();

        // init ui elements
        emailAutoCompleteTextView = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL;
            }
        });
    }

    public void signUpButtonOnClick(View view) {
        createUser(String.valueOf(emailAutoCompleteTextView.getText()), String.valueOf(passwordEditText.getText()));
    }

    private void createUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            App.getFirebaseHelper().saveUserInDatabase(user);
                            Toast.makeText(SignUpActivity.this, SUCCESS_MESSAGE,
                                    Toast.LENGTH_SHORT).show();
                            startNextActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed. " + task.getException(),
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
            Intent intent = new Intent(SignUpActivity.this, FitnessPlaceActivity.class);
            intent.putExtra(App.FITNESS_PLACE_ARG, fitnessPlace);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
