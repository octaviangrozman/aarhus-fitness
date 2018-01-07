package com.example.octav.aarhusfitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.octav.aarhusfitness.adapter.PeopleTrainingAdapter;
import com.example.octav.aarhusfitness.model.PersonTraining;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PeopleTrainingActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private List<PersonTraining> peopleTraining;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_training);

        auth = FirebaseAuth.getInstance();
        // firebase database
        database = FirebaseDatabase.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("These people train here today");
        }

        peopleTraining = new ArrayList<>();

        recyclerView = findViewById(R.id.people_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        PeopleTrainingAdapter adapter = new PeopleTrainingAdapter(peopleTraining);
        recyclerView.setAdapter(adapter);

        // intent extras (params)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fitnessPlace = getIntent().getExtras().getString("fitnessPlace");
            String firebaseDate = getIntent().getExtras().getString("firebaseDate");
            fetchData(fitnessPlace, firebaseDate);
        }
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
                auth.signOut();
                Toast.makeText(this, "You are signed out", Toast.LENGTH_SHORT).show();
                startMapActivity();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void fetchData(String fitnessPlace, String firebaseDate) {
        final HashMap<String, String> userIdTime = new HashMap<>();
        database.getReference("trainings").child(fitnessPlace).child(firebaseDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                        userIdTime.put(userIdSnapshot.getKey(), userIdSnapshot.getValue(String.class));
                    }
                    Iterator it = userIdTime.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry pair = (Map.Entry) it.next();
                        database.getReference("users").child(String.valueOf(pair.getKey())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    PersonTraining personTraining = dataSnapshot.getValue(PersonTraining.class);
                                    if (personTraining != null) {
                                        personTraining.setTime(String.valueOf(pair.getValue()));
                                    }
                                    peopleTraining.add(personTraining);
                                    Log.i("peopleTraining", peopleTraining.toString());
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
