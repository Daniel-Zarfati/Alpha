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
import com.example.alpha.Recview.Myadapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class FutureEventsFragment extends Fragment {


    RecyclerView recview;
    Myadapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_future_events, container, false);

        recview = view.findViewById(R.id.recviewFuture);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FirebaseRecyclerOptions<Event>options=
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Event"),Event.class)
                        .build();

        adapter = new Myadapter(options);
        recview.setAdapter(adapter);
        return view;
    }

    @Override
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