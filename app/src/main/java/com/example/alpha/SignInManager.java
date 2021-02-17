package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alpha.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInManager extends AppCompatActivity {

    EditText editEmailM, editPasswordM,editPinCode;
    ImageView ivSignIn,Security;
    DatabaseReference databaseref;
    private FirebaseAuth MAuth;
    ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_manager);

        databaseref = FirebaseDatabase.getInstance().getReference("User");
        MAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        editEmailM = findViewById(R.id.emailEdt);
        editPasswordM = findViewById(R.id.passwordEdt);
        editPinCode = findViewById(R.id.pinCodeEdt);
        Security = findViewById(R.id.Security);


        ivSignIn = findViewById(R.id.SignInPin);
        ivSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailM.getText().toString();
                String password = editPasswordM.getText().toString();
                String pinCode = editPinCode.getText().toString();

                SigninM(email,password,pinCode);
            }

        });
    }

    private void SigninM(String email, String password,String pinCode) {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmailM.setError("Please provide valid email!");
            editEmailM.requestFocus();
            return;
        }

        if(password.length()<6){
            editPasswordM.setError("Min password length should be 6 characters!");
            editPasswordM.requestFocus();
            return;
        }

        if(!pinCode.equals("307839035")) {
            editPinCode.setError("Pin Code isn't match!");
            editPinCode.requestFocus();
            return;
        }

        else{
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();

            MAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        loadingBar.dismiss();
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {   // Retriving data from Current user to Gloabl Var to keep and display it
                                        GlobalVar.currentUser = snapshot.getValue(User.class);

                                        databaseref.child(snapshot.getKey()).child("manager").setValue(true);

                                        Intent home = new Intent(SignInManager.this,Home.class);
                                        startActivity(home);

                                        Toast.makeText(SignInManager.this, "Successfully sign in", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                    else{
                        Toast.makeText(SignInManager.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
}
