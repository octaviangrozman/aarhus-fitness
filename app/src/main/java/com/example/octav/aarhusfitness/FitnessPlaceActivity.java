package com.example.octav.aarhusfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
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
            actionBar.setTitle("Fitness");
        }

        this.invalidateOptionsMenu();

        // firebase auth
        mAuth = FirebaseAuth.getInstance();
        // firebase database
        database = FirebaseDatabase.getInstance();

        // intent extras (params)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fitnessPlace = getIntent().getExtras().getString("fitnessPlace");
        }
        fetchData();

        // ui
        TextView textViewFitnessPlace = (TextView) findViewById(R.id.textViewFitnessPlace);
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
        this.currentUser = mAuth.getCurrentUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MenuItem accountMenuItem = menu.findItem(R.id.action_account);
        if (accountMenuItem != null) accountMenuItem.setVisible(false);
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
                mAuth.signOut();
                Toast.makeText(this, "You are signed out", Toast.LENGTH_SHORT).show();
                startMapActivity();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void fetchData() {
        database.getReference("trainings").child(fitnessPlace).child(getFirebaseDate()).addValueEventListener(new ValueEventListener() {
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

    public void joinTraining() {
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
        datePicker.setTitle("Pick a date for your next training");
        datePicker.setVersion(DatePickerDialog.Version.VERSION_1);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePicker = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        timePicker.setTitle("Pick a time for your next training");
        timePicker.setVersion(TimePickerDialog.Version.VERSION_1);
        timePicker.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.i("DATEPICKER", date);
        selectedDate = new MyDate(dayOfMonth, monthOfYear, year);
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Log.i("TIMEPICKER", time);
        selectedTime = new MyTime(hourOfDay, minute, second);
        saveTraining();
        fetchData();
    }

    private void saveTraining() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userUid = user.getUid();

        if (userUid == null) {
            Toast.makeText(this, "You need to be authenitcated!", Toast.LENGTH_SHORT).show();
        }
        if (selectedDate == null) {
            Toast.makeText(this, "You need to provide a date!", Toast.LENGTH_SHORT).show();
        }
        if (selectedTime == null) {
            Toast.makeText(this, "You need to provide time!", Toast.LENGTH_SHORT).show();
        }
        Log.d("date", selectedDate.toString());
        if (userUid != null && selectedTime != null && selectedTime != null && fitnessPlace != null) {
            DatabaseReference userRef = database.getReference("trainings")
                    .child(fitnessPlace)
                    .child(selectedDate.toString())
                    .child(userUid);
            Log.i("TIME", selectedTime.toString());
            userRef.setValue(selectedTime.toString());
            Toast.makeText(FitnessPlaceActivity.this, "You succesfully joined training",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fail! Try to select fitness location again!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void startMapActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startPeopleTrainingActivity(View view) {
        Intent intent = new Intent(this, PeopleTrainingActivity.class);
        intent.putExtra("fitnessPlace", fitnessPlace);
        intent.putExtra("firebaseDate", getFirebaseDate());
        startActivity(intent);
    }
}
