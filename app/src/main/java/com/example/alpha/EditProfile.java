package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView profileImageView;
    private Button closeButton, saveButton;
    private TextView profileChangeBtn;
    private EditText edtName,edtIdNumber,edtPhoneNumber,edtCity;
    //private SwitchCompat switchID;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;


    private SwitchCompat switchGard;
    private static String MY_PREFS = "switch_prefs";
    private static String SWITCH_STATUS = "switch_status";

    boolean switch_status;

    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        profileImageView = findViewById(R.id.profile_image);
        profileChangeBtn = findViewById(R.id.change_profile_btn);
        edtName = findViewById(R.id.newName);
        edtIdNumber = findViewById(R.id.newIdNumber);
        edtPhoneNumber = findViewById(R.id.newPhoneNumber);
        edtCity = findViewById(R.id.newCity);
        closeButton = findViewById(R.id.btnClose);
        saveButton = findViewById(R.id.btnSave);

        switchGard = findViewById(R.id.SwitchGardid);
        myPreferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        myEditor = getSharedPreferences(MY_PREFS,MODE_PRIVATE).edit();
        
        switch_status = myPreferences.getBoolean(SWITCH_STATUS,false);
        switchGard.setChecked(switch_status);

        switchGard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if(buttonView.isChecked()){
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(true);
                    myEditor.putBoolean(SWITCH_STATUS,true);
                    myEditor.apply();
                    switchGard.setChecked(true);
                }else{
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(false);
                    myEditor.putBoolean(SWITCH_STATUS,false);
                    myEditor.apply();
                    switchGard.setChecked(false);
                }
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "back home", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfile.this, Home.class));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "Data saved", Toast.LENGTH_SHORT).show();

                validateAndsave();
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(EditProfile.this);
            }
        });

        getUserinfo();

    }


    private void validateAndsave() {
        if(edtName.getText().toString().isEmpty()){
            edtCity.setError("Full Name is required!");
            edtCity.requestFocus();
            return;
        }

        if(edtIdNumber.getText().toString().isEmpty()){
            edtIdNumber.setError("ID number is required!");
            edtIdNumber.requestFocus();
            return;
        }

        if(edtPhoneNumber.getText().toString().isEmpty()){
            edtPhoneNumber.setError("Phone number is required!");
            edtPhoneNumber.requestFocus();
            return;
        }

        if(edtCity.getText().toString().isEmpty()){
            edtCity.setError("City is required!");
            edtCity.requestFocus();
            return;
        }


        else{

            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("name",edtName.getText().toString());
            userMap.put("idNumber",edtIdNumber.getText().toString());
            userMap.put("phoneNumber",edtPhoneNumber.getText().toString());
            userMap.put("city",edtCity.getText().toString());

            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
            uploadProfileImage();

        }
    }



    private void getUserinfo() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){

                    // Displaying the values in edit page
                    String name = snapshot.child("name").getValue().toString();
                    String idNumber = snapshot.child("idNumber").getValue().toString();
                    String phoneNumber = snapshot.child("phoneNumber").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();

                    edtName.setText(name);
                    edtIdNumber.setText(idNumber);
                    edtPhoneNumber.setText(phoneNumber);
                    edtCity.setText(city);

                    if(snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);

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

            profileImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage() {

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

                        HashMap<String , Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);

                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();

                    }
                }
            });
        }
        else{
            progressDialog.dismiss();
            //Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

}