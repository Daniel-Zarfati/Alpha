package com.example.alpha.FragmentsClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alpha.Model.GlobalVar;
import com.example.alpha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SalaryFragment extends Fragment {

    TextView salaryUser,tvsalary;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_salary,container,false);

        salaryUser = view.findViewById(R.id.Et_UserSalary);
        tvsalary = view.findViewById(R.id.tv_UserSalary);

        salaryUser.setText(GlobalVar.currentUser.getSalary());

        return view;
    }

}
