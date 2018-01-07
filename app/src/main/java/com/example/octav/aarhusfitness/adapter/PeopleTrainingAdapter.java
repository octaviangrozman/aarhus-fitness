package com.example.octav.aarhusfitness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.octav.aarhusfitness.R;
import com.example.octav.aarhusfitness.model.PersonTraining;

import java.util.List;

/**
 * Created by Octav on 1/7/2018.
 */

public class PeopleTrainingAdapter extends RecyclerView.Adapter<PeopleTrainingAdapter.ViewHolder> {

    private List<PersonTraining> people;

    public PeopleTrainingAdapter(List<PersonTraining> people) {
        this.people = people;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonTraining person = people.get(position);
        holder.personName.setText(person.getDisplayName());
        holder.personEmail.setText(person.getEmail());
        holder.trainingTime.setText(person.getTime());
    }

    @Override
    public int getItemCount() {
        if (people == null)
            return 0;
        return people.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        ImageView personPhoto;
        TextView personEmail;
        TextView trainingTime;

        ViewHolder(View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.person_name);
            personPhoto = itemView.findViewById(R.id.person_photo);
            personEmail = itemView.findViewById(R.id.person_email);
            trainingTime = itemView.findViewById(R.id.training_time);
        }
    }
}
