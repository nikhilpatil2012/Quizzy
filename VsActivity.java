package tronbox.social;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;


import dev.ideapot.quizzy.R;

public class VsActivity extends Activity {

    private ImageView p1_profile, p2_profile,center_back;
    private FrameLayout top_Frame,bottom_Frame;
    private Bitmap player1Pic, player2Pic;
    //String name, country;
    private Point p;
    private AnimationDrawable timerAnimation;
    private String Mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_activity);

        getActionBar().hide();

        p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        ParseBundle(getIntent().getExtras());

        init();

        animationStart();
    }
    public void animationStart(){

        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(top_Frame, View.Y, -p.y/2, 0), ObjectAnimator.ofFloat(bottom_Frame, View.Y, p.y+p.y/2, p.y/2));
        set.setDuration(600);

        set.start();

        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                ((LinearLayout)findViewById(R.id.vs_layout)).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake));
                ((FrameLayout)findViewById(R.id.center_layer)).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake));

                center_back.setVisibility(View.VISIBLE);
                timerAnimation.start();


                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timerAnimation.stop();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                intent.putExtra("Mode", Mode);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                        },600);
                    }
                },1500);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

        });
    }
    public void ParseBundle(Bundle bundle){
        if(bundle != null){
            if(bundle.containsKey("Mode")){
                Mode = bundle.getString("Mode");
            }

           // String id = SharedPrefrenceStorage.getUserFacebookId(getApplicationContext());
           // player1Pic = getImage(id+".png");
           // player2Pic = getImage(QuizzyApplication.challengerUserCode+".png");
           // player1Timeline = getImage(id+"_timeline.png");
           // player2Timeline = BitmapFactory.decodeResource(getResources(), R.drawable.amir);

        }
    }

    private void init()
    {
        top_Frame = (FrameLayout)findViewById(R.id.top_frame);
        bottom_Frame = (FrameLayout)findViewById(R.id.bottom_frame);

        center_back = (ImageView)findViewById(R.id.center_back_wall);
        center_back.setVisibility(View.INVISIBLE);

        p1_profile = (ImageView)findViewById(R.id.p1_profile_pic);
        p2_profile = (ImageView)findViewById(R.id.p2_profile_pic);


        center_back.setBackgroundResource(R.drawable.animation_vs);
        timerAnimation = (AnimationDrawable) center_back.getBackground();


        p1_profile.setImageBitmap(player1Pic);
        p2_profile.setImageBitmap(player2Pic);
    }

    public Bitmap getImage(String name){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(openFileInput(name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

