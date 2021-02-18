package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private SwitchCompat switchID;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;

    private CheckBox cbGardId;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CHECKBOX = "checkbox";
    private boolean checkBoxOnOff;


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
        cbGardId = (CheckBox)findViewById(R.id.newGardId);

        closeButton = findViewById(R.id.btnClose);
        saveButton = findViewById(R.id.btnSave);


        switchID = (SwitchCompat)findViewById(R.id.SwitchGardid);
//        SharedPreferences sharedPreferences = getSharedPreferences("SAVEID",MODE_PRIVATE);
//        switchID.setChecked(sharedPreferences.getBoolean("value",false));





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
        loadData();
        updateViews();

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
//            if(cbGardId.isChecked()){
//                saveData();
//                databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(true);
//            }

            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("name",edtName.getText().toString());
            userMap.put("idNumber",edtIdNumber.getText().toString());
            userMap.put("phoneNumber",edtPhoneNumber.getText().toString());
            userMap.put("city",edtCity.getText().toString());

            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
            uploadProfileImage();



//            switchID.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(switchID.isChecked()){
//                        SharedPreferences.Editor editor = getSharedPreferences("SAVEID",MODE_PRIVATE).edit();
//                        editor.putBoolean("value",true);
//                        editor.apply();
//                        switchID.setChecked(true);
//                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(true);
//
//                    }else{
//                        SharedPreferences.Editor editor = getSharedPreferences("SAVEID",MODE_PRIVATE).edit();
//                        editor.putBoolean("value",false);
//                        editor.apply();
//                        switchID.setChecked(true);
//                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(false);
//                    }
//                }
//            });


        }
    }


    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHECKBOX,cbGardId.isChecked());

        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        checkBoxOnOff = sharedPreferences.getBoolean(CHECKBOX,false); // if nothing saved will be off
    }
    public void updateViews(){
        cbGardId.setChecked(checkBoxOnOff);
        databaseReference.child(mAuth.getCurrentUser().getUid()).child("gardId").setValue(true);

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