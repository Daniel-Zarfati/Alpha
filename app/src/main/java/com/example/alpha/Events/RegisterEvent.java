package com.example.alpha.Events;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alpha.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public  class RegisterEvent extends AppCompatActivity {   // Adding img to firebase not working

    FirebaseDatabase db;
    DatabaseReference dbRef;


    EditText edtLocation, edtDate, edtAvailibility, edtStartHour, edtEndHour, edtSalary;
    Button btnSelectImage,btnRegisterEvent;
    ProgressDialog loadingBar;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private CircleImageView eventImageView;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Event");
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Event Pic");
        eventImageView = findViewById(R.id.TakingPlace_img);
        btnSelectImage=findViewById(R.id.btn_select_Imgae);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(RegisterEvent.this);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Event");

        loadingBar = new ProgressDialog(this);

        edtLocation = findViewById(R.id.Et_TakingPlace);
        edtDate = findViewById(R.id.Et_Date);
        edtAvailibility = findViewById(R.id.Et_Availability);
        edtStartHour = findViewById(R.id.Et_StartHour);
        edtEndHour = findViewById(R.id.Et_EndHour);
        edtSalary = findViewById(R.id.Et_EventSalary);


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


        getEventInfo();

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
                    //event.setAvailability(Integer.parseInt(edtAvailibility.getText().toString()));  // Convert to int ?

                    // ERORR EVENT IS REGISTER BEFOR PHOTO IS UPLOADED
                    uploadEventImage();
                    String eventId = dbRef.push().getKey();
                    event.setEventUid(eventId);
                    dbRef.child(eventId).setValue(event);
                    loadingBar.dismiss();


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


                }
            });
        }
    }

    private void getEventInfo() {
            databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.getChildrenCount() > 0){

                        if(snapshot.hasChild("image"))
                        {
                            String image = snapshot.child("image").getValue().toString();
                            Picasso.get().load(image).into(eventImageView);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            eventImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }





    private void uploadEventImage() {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Set your profile");
            progressDialog.setMessage("Please wait, while the data is being set");
            progressDialog.show();

            if (imageUri != null) {
                final StorageReference fileRef = storageProfilePicRef
                        .child(mAuth.getCurrentUser().getUid() + ".jpg");
                uploadTask = fileRef.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task <Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUri = downloadUrl.toString();

                            Picasso.get().load(myUri).into(eventImageView);

//                            HashMap<String , Object> EventMap = new HashMap<>();
//                            EventMap.put("image",myUri);
//
//                            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(EventMap);
//
//                            progressDialog.dismiss();

//                            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            databaseReference.updateChildren(userMap);


                            //databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(myUri);
                            databaseReference.child("Event").child(mAuth.getCurrentUser().getUid()).setValue(myUri);
//                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myUri);
                            progressDialog.dismiss();

                        }
                    }
                });
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }

}