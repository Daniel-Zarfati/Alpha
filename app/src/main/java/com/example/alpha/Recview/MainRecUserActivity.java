package com.example.alpha.Recview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alpha.Events.Event;
import com.example.alpha.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MainRecUserActivity extends AppCompatActivity {   /// Supposed to be the user events that he booked !!

    RecyclerView recviewuser;
    MyadapterUser adapterUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rec_user);


        recviewuser =(RecyclerView)findViewById(R.id.recviewUser);
        recviewuser.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Event> options=
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Event"),Event.class)
                        .build();

        adapterUser = new MyadapterUser(options);
        recviewuser.setAdapter(adapterUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterUser.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterUser.stopListening();
    }

}