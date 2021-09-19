package com.example.alpha.FragmentsClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.alpha.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleFMapFragment extends Fragment {

    com.google.android.gms.maps.GoogleMap map;


    public GoogleFMapFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mapFragment.getMapAsync(this);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_f_map, container, false);
    }


    public void onMapReady(@NonNull com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;

        LatLng Israel = new LatLng(31.4117257, 35.0818155);
        map.addMarker(new MarkerOptions().position(Israel).title("Israel"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Israel));
    }
}