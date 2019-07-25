package com.dasayantist.msacco;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splashscreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;

    ImageView imageSplasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

       imageSplasher = findViewById(R.id.imgStart);


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.together);
        imageSplasher.startAnimation(animation);


        new Handler().postDelayed(new Runnable() {

/*
             * Showing splash screen with a timer. This will be useful when you
* want to show case your app logo / company
*/

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

//    public void splasher(){
//        //ImageView imageSplasher = findViewById(R.id.imgStart);
//
//    }

}
