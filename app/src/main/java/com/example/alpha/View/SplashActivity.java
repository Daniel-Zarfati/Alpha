package com.example.alpha.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.alpha.R;
import com.example.alpha.SignIn;

public class SplashActivity extends AppCompatActivity {

    ImageView splashImag;

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashImag = findViewById(R.id.image);
        lottieAnimationView =  findViewById(R.id.lottie);

        splashImag.animate().translationY(-1600).setDuration(1000).setStartDelay(1950); // 1000 equels 1 sec
        lottieAnimationView.animate().translationY(-1400).setDuration(1000).setStartDelay(2000); // 1000 equels 1 sec

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SignIn.class);
                startActivity(intent);

            }
        }, 2350);

    }


}