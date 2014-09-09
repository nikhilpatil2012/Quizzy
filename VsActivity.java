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
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;

import tronbox.QuizzyArena.QuizzyArena;
import tronbox.Sounds.Assets;
import tronbox.welcome.MasterHomeScreen;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SharedPrefrenceStorage;

public class VsActivity extends Activity {

    private ImageView p1_profile, p2_profile,center_back;
    private FrameLayout top_Frame,bottom_Frame;
    private Bitmap player1Pic, player2Pic;
    //String name, country;
    private Point p;
    private AnimationDrawable timerAnimation;
    private String userCode, userName, userFbId, challengerUserCode, challengerName, challengerFbId, Mode, isCorrect = "no";

    private TextView player1Name, player1Level, player2Name, player2Level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_activity);

        getActionBar().hide();


        Bundle profileInfo = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());
        userCode = SharedPrefrenceStorage.getUserCode(getApplicationContext());
        userName = profileInfo.getString("Name");
        userFbId = profileInfo.getString("FacebookId");

        challengerName = QuizzyApplication.challengerName;
        challengerFbId = QuizzyApplication.challengerFacebookId;
        challengerUserCode = QuizzyApplication.challengerUserCode;


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

                if(QuizzyApplication.soundFX == true){

                    Assets.wall.play(1);
                }


                ((LinearLayout) findViewById(R.id.vs_layout)).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
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
                                Intent intent = new Intent(getApplicationContext(), QuizzyArena.class);
                                intent.putExtra("Mode", Mode);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                        }, 600);

                    }
                }, 1300);

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

                String facebookId = SharedPrefrenceStorage.getUserFacebookId(getApplicationContext());

                if(Mode.equals("Play")){

                    player2Pic = getImage(QuizzyApplication.challengerUserCode+".png");

                }else if(Mode.equals("CHALLENGE")){

                    player2Pic = getImage(challengerFbId+".png");

                }

            }

        }
    }

    private void init()
    {


        Log.w("UserCode", userCode);
        Log.w("UserName", userName);
        Log.w("UserFbId", userFbId);

        if(challengerName!=null){

            Log.w("ChallengerName", challengerName);
        }

        if(challengerFbId!=null){

            Log.w("ChallengerName", challengerFbId);
        }


        if(challengerUserCode!=null){

            Log.w("ChallengerName", challengerUserCode);
        }

        Bundle profileInfo = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());

        player2Pic = getImage(QuizzyApplication.challengerUserCode+".png");


        top_Frame = (FrameLayout)findViewById(R.id.top_frame);
        bottom_Frame = (FrameLayout)findViewById(R.id.bottom_frame);

        center_back = (ImageView)findViewById(R.id.center_back_wall);
        center_back.setVisibility(View.INVISIBLE);

        p1_profile = (ImageView)findViewById(R.id.p1_profile_pic);
        p2_profile = (ImageView)findViewById(R.id.p2_profile_pic);

        player1Name = (TextView)findViewById(R.id.p1_profile_name);
        player1Name.setText(userName);

        player2Name = (TextView)findViewById(R.id.p2_profile_name);
        player2Name.setText(challengerName);

        player1Level = (TextView)findViewById(R.id.p1_profile_level);
        player1Level.setText("Hustler");

        player2Level = (TextView)findViewById(R.id.p2_profile_level);
        player2Level.setText("Avenger");

        center_back.setBackgroundResource(R.drawable.animation_vs);
        timerAnimation = (AnimationDrawable) center_back.getBackground();

        p1_profile.setImageBitmap(getImage(userFbId + ".png"));
        p2_profile.setImageBitmap(getImage(challengerFbId+".png"));

    }

    public Bitmap getImage(String name){
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(openFileInput(name));

        } catch (FileNotFoundException e) {

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.james);
        }
          return bitmap;
    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(getApplicationContext(), MasterHomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();

    }

}
