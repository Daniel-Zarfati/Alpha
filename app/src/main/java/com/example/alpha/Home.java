package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.alpha.Events.RegisterEvent;
import com.example.alpha.FragmentsClasses.FutureEventsFragment;
import com.example.alpha.FragmentsClasses.MyEventsFragment;
import com.example.alpha.FragmentsClasses.SalaryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    TextView txtName;
    BottomNavigationView bottomBar;
    Button btn2;

    private CircleImageView profileImageView;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        btn2 = findViewById(R.id.btn2);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyEventsFragment()).commit();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        profileImageView = findViewById(R.id.dp);

        txtName = findViewById(R.id.txtName);
        txtName.setText(GlobalVar.currentUser.getName());


        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.MyEvents:
                        selectedFragment = new MyEventsFragment();
                        Toast.makeText(Home.this, "My Events.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Events:
                        selectedFragment = new FutureEventsFragment();
                        Toast.makeText(Home.this, "Events.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Salary:
                        selectedFragment = new SalaryFragment();
                        Toast.makeText(Home.this, "Salary.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.LogOut:
                        //Toast.makeText(Home.this, "Logout.", Toast.LENGTH_LONG).show();
                        //FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Home.this, RegisterEvent.class));
                        break;
                }
                //displaying the fragments
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

                return true;
            }
        });


        btn2.setOnClickListener((v) -> {
            Intent profile = new Intent(Home.this, EditProfile.class);
            startActivity(profile);
        });

        getUserinfo();
    }


        private void getUserinfo() {
            databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.getChildrenCount() > 0){

                        String name = snapshot.child("name").getValue().toString();

                        //txtName.setText(new  );

                        if(snapshot.hasChild("image")){
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

}

