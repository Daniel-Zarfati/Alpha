package com.example.alpha.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alpha.Model.User;
import com.example.alpha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference dbRef;

    EditText editName,editIdNumber,editPhoneNumber,editCity,editEmail,editPassword;

    Button btnRegister;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("User");

        loadingBar = new ProgressDialog(this);

        editName = findViewById(R.id.edtName);
        editIdNumber = findViewById(R.id.edtIDnumber);
        editPhoneNumber = findViewById(R.id.edtPhoneNumber);
        editCity = findViewById(R.id.edtCity);
        editEmail = findViewById(R.id.edtEmail);
        editPassword = findViewById(R.id.edtPassword);

        btnRegister = findViewById(R.id.btnRegisterUser);
        btnRegister.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = editName.getText().toString().trim();
            String Idnumber = editIdNumber.getText().toString().trim();
            String Phonenumber = editPhoneNumber.getText().toString().trim();
            String City = editCity.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();

            String userSalary = "0";


            RegisterUser(name,Idnumber,Phonenumber,City,email,pass,userSalary);
        }
    });



}

    private void RegisterUser(String name, String Idnumber,String Phonenumber,String City,String email,String pass,String userSalary) {

        if(name.isEmpty()){
            editName.setError("Full Name is required!");
            editName.requestFocus();
            return;
        }

        if(Idnumber.isEmpty()){
            editIdNumber.setError("ID number is required!");
            editIdNumber.requestFocus();
            return;
        }

        if(Phonenumber.isEmpty()){
            editPhoneNumber.setError("Phone number is required!");
            editPhoneNumber.requestFocus();
            return;
        }

        if(City.isEmpty()){
            editCity.setError("City is required!");
            editCity.requestFocus();
            return;
        }


        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please provide valid email!");
            editEmail.requestFocus();
            return;
        }

        if(pass.length()<6){
            editPassword.setError("Min password length should be 6 characters!");
            editPassword.requestFocus();
            return;
        }

        else{

            loadingBar.setTitle("Registration");
            loadingBar.setMessage("Please wait, while you are being register");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                       FirebaseUser firebaseUser =  mAuth.getCurrentUser();
                       firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(Register.this, "Verification Email has been sent.", Toast.LENGTH_LONG).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Register.this, "Fail, Email not sent.", Toast.LENGTH_LONG).show();
                           }
                       });


                        //Save to db

                        User user = new User();

                        user.setName(editName.getText().toString());
                        user.setIdNumber(editIdNumber.getText().toString());
                        user.setCity(editCity.getText().toString());
                        user.setPhoneNumber(editPhoneNumber.getText().toString());
                        user.setPassword(editPassword.getText().toString());
                        user.setEmail(editEmail.getText().toString());
                        user.setSalary("0");

                        dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Register.this, "User Register successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, SignIn.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //Toast.makeText(Register.this, "Something is wrong, Try again", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }

    }
}



