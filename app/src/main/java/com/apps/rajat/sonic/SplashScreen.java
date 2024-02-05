package com.apps.rajat.sonic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {



    RelativeLayout rl;

    TextView tvsonic, tvname;
    Typeface sonicfont;
    ImageView ivicon;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        rl = (RelativeLayout)findViewById(R.id.splashactivity);
        if (Build.VERSION.SDK_INT>20) {
            rl.postDelayed(new Runnable() {
                @Override
                public void run() {
                    revealeffectframe();
                }
            }, 100);
        }else{
            rl.setVisibility(View.VISIBLE);
        }
     tvsonic = (TextView) findViewById(R.id.tv);
        tvname = (TextView) findViewById(R.id.name);
       ivicon = (ImageView) findViewById(R.id.iv);
        sonicfont = Typeface.createFromAsset(this.getAssets(),"font/titlefont.otf");
        tvsonic.setTypeface(sonicfont);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
      //  Animation byeanim = AnimationUtils.loadAnimation(this, R.anim.reversetransition);

     //   tvsonic.startAnimation(myanim);
      //  ivicon.startAnimation(myanim);
      //  tvname.startAnimation(myanim);
    //    tvname.startAnimation(byeanim);
     /*   YoYo.with(Techniques.Tada)
                .duration(1000)
                .repeatMode(0)
                .playOn(tvsonic);*/

    /*    YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .repeat(0)
                .playOn(ivicon);*/


        //Animaton of fullL




        final Intent main1 = new Intent(this, MainActivity.class);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(main1);
                    finish();

                }
            }
        };
        timer.start();


    }

   void revealeffectframe(){
        if (Build.VERSION.SDK_INT>20){
            int cx = rl.getMeasuredWidth()/2;
            int cy = rl.getMeasuredHeight()/2;
            int radius = Math.max(rl.getWidth(),rl.getHeight());
            android.animation.Animator a = ViewAnimationUtils.createCircularReveal(rl,cx,cy,0,radius);
            a.setDuration(1000);
            rl.setVisibility(View.VISIBLE);
            a.start();

        }
    }


}

