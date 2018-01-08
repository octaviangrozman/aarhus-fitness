package com.example.octav.aarhusfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octav.aarhusfitness.model.MyDate;
import com.example.octav.aarhusfitness.model.MyTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class FitnessPlaceActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String YOU_ARE_SIGNED_OUT_MESSAGE = "You are signed out";
    public static final String DATE_PICKER_TITLE = "Pick a date for your next training";
    public static final String DATEPICKERDIALOG = "Datepickerdialog";
    public static final String TIME_PICKER_TITLE = "Pick a time for your next training";
    public static final String TIMEPICKERDIALOG = "Timepickerdialog";
    public static final String YOU_NEED_TO_PROVIDE_A_DATE_MESSAGE = "You need to provide a date!";
    public static final String YOU_NEED_TO_PROVIDE_TIME_MESSAGE = "You need to provide time!";
    public static final String FAIL_MESSAGE = "Fail! Try to select fitness location again!";
    public static final String SUCCESS_MESSAGE = "You succesfully joined training";
    public static final String ACTION_BAR_TITLE = "Fitness spot";

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String fitnessPlace;
    private FirebaseUser currentUser;
    private MyDate selectedDate;
    private MyTime selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_place);

        // set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(ACTION_BAR_TITLE);
        }

        // firebase auth
        auth = FirebaseAuth.getInstance();
        // firebase database
        database = FirebaseDatabase.getInstance();

        // intent extras (params)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fitnessPlace = getIntent().getExtras().getString(App.FITNESS_PLACE_ARG);
        }
        fetchData();

        // ui
        TextView textViewFitnessPlace = findViewById(R.id.textViewFitnessPlace);
        textViewFitnessPlace.setText(fitnessPlace);

        Button buttonJoin = findViewById(R.id.buttonJoin);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    joinTraining();
                } else {
                    startLoginActivity();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.currentUser = auth.getCurrentUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_account:
                auth.signOut();
                Toast.makeText(this, YOU_ARE_SIGNED_OUT_MESSAGE, Toast.LENGTH_SHORT).show();
                startMapActivity();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void fetchData() {
        database.getReference(App.getFirebaseHelper().TRAININGS_REF)
                .child(fitnessPlace)
                .child(getFirebaseDate())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView buttonPeopleTraining = findViewById(R.id.buttonPeopleTraining);
                String intro = dataSnapshot.getChildrenCount() == 1 ? "1 person trains" : dataSnapshot.getChildrenCount() + " people train";
                buttonPeopleTraining.setText(intro + " here today");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getFirebaseDate() {
        Calendar now = Calendar.getInstance();
        return String.format("%d|%d|%d",
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.MONTH),
                now.get(Calendar.YEAR)
        );
    }

    private void joinTraining() {
        showDatePicker();
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.setTitle(DATE_PICKER_TITLE);
        datePicker.setVersion(DatePickerDialog.Version.VERSION_1);
        datePicker.show(getFragmentManager(), DATEPICKERDIALOG);
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePicker = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        timePicker.setTitle(TIME_PICKER_TITLE);
        timePicker.setVersion(TimePickerDialog.Version.VERSION_1);
        timePicker.show(getFragmentManager(), TIMEPICKERDIALOG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new MyDate(dayOfMonth, monthOfYear, year);
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        selectedTime = new MyTime(hourOfDay, minute, second);
        saveTraining();
        fetchData();
    }

    private void saveTraining() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();

            if (selectedDate == null) {
                Toast.makeText(this, YOU_NEED_TO_PROVIDE_A_DATE_MESSAGE, Toast.LENGTH_SHORT).show();
            }

            if (selectedTime == null) {
                Toast.makeText(this, YOU_NEED_TO_PROVIDE_TIME_MESSAGE, Toast.LENGTH_SHORT).show();
            }
            if (selectedTime != null && selectedTime != null && fitnessPlace != null) {
                DatabaseReference userRef = database.getReference(App.getFirebaseHelper().TRAININGS_REF)
                        .child(fitnessPlace)
                        .child(selectedDate.toString())
                        .child(userUid);
                userRef.setValue(selectedTime.toString());
                Toast.makeText(FitnessPlaceActivity.this, SUCCESS_MESSAGE,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, FAIL_MESSAGE,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startPeopleTrainingActivity(View view) {
        Intent intent = new Intent(this, PeopleTrainingActivity.class);
        intent.putExtra(App.FITNESS_PLACE_ARG, fitnessPlace);
        intent.putExtra(App.FIREBASE_DATE_ARG, getFirebaseDate());
        startActivity(intent);
    }
}
