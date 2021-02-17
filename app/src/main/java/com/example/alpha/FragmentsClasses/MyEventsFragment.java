package com.example.alpha.FragmentsClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alpha.Events.Event;
import com.example.alpha.R;
import com.example.alpha.Recview.MyadapterUser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {

    RecyclerView recview;
    MyadapterUser adapter;

    FirebaseAuth auth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events,container,false);

        auth = FirebaseAuth.getInstance();

        recview = view.findViewById(R.id.recviewMyEvents);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FirebaseRecyclerOptions<Event> options=
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Event"),Event.class)
                        .build();

        adapter = new MyadapterUser(options);
        databaseReference.child(auth.getUid()).child("My events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> events = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()) {
                    String event = child.getValue(String.class);
                    events.add(event);
                }

                adapter.setEvents(events);
                recview.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
