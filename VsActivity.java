package tronbox.social;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.util.List;

import tronbox.QuizzyArena.QuizzyArena;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SharedPrefrenceStorage;

public class VsActivity extends Activity {

    ImageView topImage, topProfile, bottomImage, bottomProfile, animateLogo;

    LinearLayout.LayoutParams topParams, bottomParams;
    Bitmap player1Pic, player1Timeline, player2Pic, player2Timeline;
    String name, country;
    Point p;
    AnimationDrawable timerAnimation;
    private String Mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_activity);
        getActionBar().hide();

        p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        ParseBundle(getIntent().getExtras());

        topParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, p.y/2);
        topParams.gravity = Gravity.TOP;


        bottomParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, p.y/2);
        bottomParams.gravity = Gravity.BOTTOM;

        topImage = (ImageView)findViewById(R.id.top_image);
        topImage.setScaleType(ImageView.ScaleType.FIT_XY);
        topImage.setImageBitmap(player1Timeline);
        topImage.setLayoutParams(topParams);

        bottomImage = (ImageView)findViewById(R.id.bottom_image);
        bottomImage.setScaleType(ImageView.ScaleType.FIT_XY);
        bottomImage.setImageBitmap(player2Timeline);
        bottomImage.setLayoutParams(bottomParams);

        topProfile = (ImageView)findViewById(R.id.top_profile);
        topProfile.setImageBitmap(imageCircleClip(player1Pic));

        bottomProfile = (ImageView)findViewById(R.id.bottom_profile);
        bottomProfile.setImageBitmap(imageCircleClip(player2Pic));

        animateLogo = (ImageView)findViewById(R.id.animate_logo);
        animateLogo.setBackgroundResource(R.drawable.qz_logo);
       // timerAnimation = (AnimationDrawable)animateLogo.getBackground();


        animationStart();
    }

    public void animationStart(){

        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(topImage, View.Y, -p.y/2, 0), ObjectAnimator.ofFloat(bottomImage, View.Y, p.y+p.y/2, p.y/2));
        set.setInterpolator(new BounceInterpolator());
        set.setDuration(2000);
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                Intent intent = new Intent(getApplicationContext(), QuizzyArena.class);
                intent.putExtra("Mode", Mode);
                startActivity(intent);

//                timerAnimation.start();

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

            String id = SharedPrefrenceStorage.getUserFacebookId(getApplicationContext());

            player1Pic = getImage(id+".png");
            player2Pic = getImage(QuizzyApplication.challengerUserCode+".png");

            player1Timeline = getImage(id+"_timeline.png");
            player2Timeline = BitmapFactory.decodeResource(getResources(), R.drawable.amir);

        }
    }

    public  Bitmap imageCircleClip(Bitmap sourceBitmap){

        Bitmap bitmap = sourceBitmap;

        if(bitmap == null){

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.james);
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.parseColor("#bbbbbb"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        int targetWidth = 80;
        int targetheight = 80;

        Bitmap outputBitmap = Bitmap.createBitmap(targetWidth, targetheight, Bitmap.Config.ARGB_8888);

        Path path = new Path();
        path.addCircle(targetWidth/2, targetheight/2, targetWidth/2, Path.Direction.CW);

        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect out = new Rect(0, 0, targetWidth, targetheight);

        Bitmap source = bitmap;

        canvas.drawBitmap(source, src, out, null);
        canvas.drawCircle(targetWidth/2, targetheight/2, targetWidth/2, paint);

        return outputBitmap;
    }

    public Bitmap getImage(String name){

        Bitmap bitmap = null;

        try {

        bitmap =  BitmapFactory.decodeStream(openFileInput(name));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
