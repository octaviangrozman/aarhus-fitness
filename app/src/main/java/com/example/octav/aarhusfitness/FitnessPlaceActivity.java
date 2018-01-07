package com.example.octav.aarhusfitness;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class FitnessPlaceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private String fitnessPlace = "";
    private FirebaseUser currentUser;

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
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void joinTraining() {
        showDatePicker();
//        database.getReference("trainings").child(new Date().toString()).push();
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
        showTimePicker();
    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Log.i("TIMEPICKER", time);
    }
}
