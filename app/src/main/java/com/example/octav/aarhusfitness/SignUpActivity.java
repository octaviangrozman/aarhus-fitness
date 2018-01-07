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

import com.example.octav.aarhusfitness.model.PersonTraining;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "UserSignUp";
    public static final String PREFS = "prefs";
    public static final String FITNESS_PLACE = "fitnessPlace";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // ui
    private AutoCompleteTextView emailAutoCompleteTextView;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // firebase auth
        mAuth = FirebaseAuth.getInstance();
        // firebase database
        database = FirebaseDatabase.getInstance();

        // init ui elements
        // Set up the login form.
        emailAutoCompleteTextView = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
    }

    public void signUpButtonOnClick(View view) {
        createUser(String.valueOf(emailAutoCompleteTextView.getText()), String.valueOf(passwordEditText.getText()));
    }

    public void createUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserInDatabase(user);
                            Log.d("USER", user.toString());
                            Toast.makeText(SignUpActivity.this, "Your account has been succesfully created",
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

    public void startNextActivity() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String fitnessPlace = prefs.getString(FITNESS_PLACE, null);
        if(fitnessPlace != null) {
            Intent intent = new Intent(SignUpActivity.this, FitnessPlaceActivity.class);
            intent.putExtra(FITNESS_PLACE, fitnessPlace);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    public void saveUserInDatabase(FirebaseUser user) {
        PersonTraining userDetails = new PersonTraining(
                user.getEmail(),
                user.getDisplayName(),
                String.valueOf(user.getPhotoUrl()),
                user.getPhoneNumber()
        );

        database.getReference("users")
                .child(user.getUid()).setValue(userDetails);
    }
}
