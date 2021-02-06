package com.example.alpha.Events;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alpha.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterEvent extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference dbRef;

    private CircleImageView eventImageView;
    Button btnSelectImage;
    EditText edtLocation, edtDate, edtAvailibility, edtStartHour, edtEndHour, edtSalary;
    Button btnRegisterEvent;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        //mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Event");

        loadingBar = new ProgressDialog(this);

        edtLocation = findViewById(R.id.Et_TakingPlace);
        edtDate = findViewById(R.id.Et_Date);
        edtAvailibility = findViewById(R.id.Et_Availability);
        edtStartHour = findViewById(R.id.Et_StartHour);
        edtEndHour = findViewById(R.id.Et_EndHour);
        edtSalary = findViewById(R.id.Et_EventSalary);

        btnSelectImage=findViewById(R.id.btn_select_Imgae);

        btnRegisterEvent = findViewById(R.id.btn_uploadEvent);
        btnRegisterEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = edtLocation.getText().toString();
                String date = edtDate.getText().toString();
                String availibility = edtAvailibility.getText().toString();
                String startHour = edtStartHour.getText().toString();
                String endHour = edtEndHour.getText().toString();
                String salary = edtSalary.getText().toString();

                registerEvent(location,date,availibility,startHour,endHour,salary);
            }
        });



    }

    private void registerEvent(String location, String date, String availibility, String startHour, String endHour, String salary) {

        if(location.isEmpty()){
            this.edtLocation.setError("Location is required!");
            this.edtLocation.requestFocus();
            return;
        }

        if(date.isEmpty()){
            this.edtDate.setError("Date is required!");
            this.edtDate.requestFocus();
            return;
        }

        if(availibility.isEmpty()){
            this.edtAvailibility.setError("Availibility is required!");
            this.edtAvailibility.requestFocus();
            return;
        }

        if(startHour.isEmpty()){
            this.edtStartHour.setError("StartHour is required!");
            this.edtStartHour.requestFocus();
            return;
        }

        if(endHour.isEmpty()){
            this.edtEndHour.setError("EndHour is required!");
            this.edtEndHour.requestFocus();
            return;
        }

        if(salary.isEmpty()){
            this.edtSalary.setError("Salary is required!");
            this.edtSalary.requestFocus();
            return;
        }



        else{

            loadingBar.setTitle("Registration");
            loadingBar.setMessage("Please wait, while you are being register");
            loadingBar.show();


            btnRegisterEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = new Event();

                    event.setLocation(edtLocation.getText().toString());
                    event.setDate(edtDate.getText().toString());
                    event.setAvailability(edtAvailibility.getText().toString());
                    event.setStartHour(edtStartHour.getText().toString());
                    event.setEndHour(edtEndHour.getText().toString());
                    event.setEventSalary(edtSalary.getText().toString());

                    dbRef.push().setValue(event);

                    //use these to  EDIT specific EVENT!!!!
//                    dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(event)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(RegisterEvent.this, "Event Register successfully", Toast.LENGTH_SHORT).show();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(RegisterEvent.this, "Failed to register", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
                    loadingBar.dismiss();
                }
        });
    }
}
}