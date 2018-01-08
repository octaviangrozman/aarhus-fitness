package com.example.octav.aarhusfitness;

import com.example.octav.aarhusfitness.model.PersonTraining;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Octavian on 1/7/2018.
 */

public class FirebaseHelper {

    public final String USERS_REF = "users";
    public final String TRAININGS_REF = "trainings";

    public void saveUserInDatabase(FirebaseUser user) {
        PersonTraining userDetails = new PersonTraining(
                user.getEmail(),
                user.getDisplayName(),
                String.valueOf(user.getPhotoUrl()),
                user.getPhoneNumber()
        );

        FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).setValue(userDetails);
    }

}
