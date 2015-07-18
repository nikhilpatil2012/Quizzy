package tronbox.welcome;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;

import tronbox.arena.LoadingScreen;
import tronbox.social.AppIdGetterId;
import tronbox.controller.QuizzyDatabase;
import tronbox.social.ChallengersList;
import tronbox.social.Friend;
import tronbox.social.QuizzeeFriendsZone;
import tronbox.social.R;


public class Test extends FragmentActivity{

    private int ImageId, top, width, height, imageHeight;
    private String Name, ImageName, subjectCode, chapterCode, userCode;

    ImageView imageView, laterImageView, topQuizImage;
    TextView textView, laterTextView, quizeName, topQuizName, quizDesc;

    ImageButton cancel;

    ImageView quizImage;

    int[] screenLocation;
    FrameLayout layout, masterlayout;
    FrameLayout.LayoutParams params;
    FrameLayout laterLayout;

    private  TimeInterpolator sDecelerator;

    ColorDrawable darkBackground;
    Drawable drawable;

    Button play, challenge, ranking;

    QuizzyDatabase database;

    ArrayList<Friend> list;

    Fragment fragment;

    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_challenge_layout);

        Bundle bundle = getIntent().getExtras();
        ImageId = bundle.getInt("ImageId");
        ImageName = bundle.getString("ImageName");
        Name = bundle.getString("Name");
        top = bundle.getInt("Top");
        width = bundle.getInt("Width");
        height = bundle.getInt("Height");
        imageHeight = bundle.getInt("ImageHeight");
        subjectCode  = bundle.getString("SubjectCode");
        chapterCode  = bundle.getString("ChapterCode");
        QuizzyApplication.SubjectCode = subjectCode;
        QuizzyApplication.ChapterCode = chapterCode;
        QuizzyApplication.QuizName = Name;

        masterlayout = (FrameLayout)findViewById(R.id.common_fragment_background);
        laterLayout = (FrameLayout)findViewById(R.id.hidden_detail_fragment);

        /***** This Layout is intially visible which animates to top then dissolves ******/

        quizImage = (ImageView)findViewById(R.id.quiz_image);
        quizImage.setImageBitmap(getBitmapFromName(ImageName));

        quizeName = (TextView)findViewById(R.id.quiz_name);
        quizeName.setText(Name);
        quizeName.setTypeface(font);


        font = Typeface.createFromAsset(getAssets(), "font.ttf");

        quizDesc = (TextView)findViewById(R.id.topQuizDesc);
        quizDesc.setTypeface(font);

        layout = (FrameLayout)findViewById(R.id.animate_item);

        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);
        params.setMargins(0, top+138+10, 0, 0);
        layout.setLayoutParams(params);

        darkBackground = new ColorDrawable(Color.BLACK);

        /********************************************************************************/

        /**********     This is the top View which was initially invisible ******/


        topQuizImage = (ImageView)findViewById(R.id.topImage);
        topQuizImage.setImageBitmap(getBitmapFromName(ImageName));

        topQuizName = (TextView)findViewById(R.id.topQuizName);
        topQuizName.setTypeface(font);
        topQuizName.setText(Name);


        /********************************************************************************/

        /**********************  Bottom View ******************************************/


        database = new QuizzyDatabase(getApplicationContext(), "QUIZZY", null, 1);
        list = new ArrayList<Friend>();

        Cursor getData = database.getFriendsList();

        if(getData.moveToFirst()){

            do {

                list.add(new Friend(getData.getString(1), getData.getString(2), getData.getString(3)));

//	        				    Log.w("Name",getData.getString(1));
//	    		    			Log.w("id",""+getData.getLong(0));
//	    		    			Log.w("pic", getData.getString(2));
//

            } while(getData.moveToNext());
        }

        getData.close();
        database.close();

        fragment = new ChallengersList(list);
        Bundle data = new Bundle();
        data.putString("Mode", "CHALLENGE");
        fragment.setArguments(data);


        play = (Button)laterLayout.findViewById(R.id.play);
        play.setTypeface(font);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(QuizzyApplication.NetworkCheck(getApplicationContext())){

                    Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                    intent.putExtra("Message", "Searching Opponent");
                    intent.putExtra("Mode", "Play");
                    intent.putExtra("SubjectCode", subjectCode);
                    intent.putExtra("ChapterCode", chapterCode);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {

                    makeDialog().show();
                }

                sendBroadcast(new Intent("Finish_Reciever"));

            }
        });

        challenge = (Button)laterLayout.findViewById(R.id.challenge);
        challenge.setTypeface(font);
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(QuizzyApplication.NetworkCheck(getApplicationContext())){

                    Intent intent = new Intent(getApplicationContext(), QuizzeeFriendsZone.class);
                    intent.putExtra("MODE", "CHALLENGE");
                    intent.putExtra("TOP_MESSAGE", "CHALLENGE");
                    startActivity(intent);

                }else {

                    makeDialog().show();
                }


                sendBroadcast(new Intent("Finish_Reciever"));

            }
        });

        ranking = (Button)findViewById(R.id.ranking);
        ranking.setTypeface(font);
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(QuizzyApplication.NetworkCheck(getApplicationContext())){

                    QuizzyApplication.SubjectCode = subjectCode;
                    QuizzyApplication.ChapterCode = chapterCode;

                    Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                    intent.putExtra("Message", "Fetching Data");
                    intent.putExtra("Mode", "Ranking");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {

                    makeDialog().show();
                }

            }
        });


        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goBack();

            }
        });

        /********************************************************************************/



        if(savedInstanceState == null){

            ViewTreeObserver observer = masterlayout.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    masterlayout.getViewTreeObserver().removeOnPreDrawListener(this);


                    AnimatorSet set = new AnimatorSet();
                    set.setInterpolator(new BounceInterpolator());
                    set.setDuration(500);
                    set.playTogether(ObjectAnimator.ofFloat(layout, View.SCALE_X, 1.1f),ObjectAnimator.ofFloat(layout, View.SCALE_Y, 1.2f));
                    set.start();



                    ObjectAnimator bgAnim = ObjectAnimator.ofInt(darkBackground, "alpha", 0, 255);
                    bgAnim.setDuration(500);
                    bgAnim.setStartDelay(500);
                    bgAnim.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                            masterlayout.setBackground(darkBackground);
                            laterLayout.setVisibility(View.VISIBLE);


                            AnimatorSet set2 = new AnimatorSet();
                            set2.setDuration(1000);
                            set2.playTogether(ObjectAnimator.ofFloat(layout, View.Y, top+140, 0), ObjectAnimator.ofFloat(layout, View.ALPHA, 1, 0), ObjectAnimator.ofFloat(laterLayout, View.ALPHA, 0,1));
                            set2.start();

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    });
                    bgAnim.start();

                    return true;
                }
            });
        }


    }

    public void runExitAction(final Runnable endAction){

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(darkBackground, "alpha", 255, 0);
        bgAnim.setDuration(1000);
        bgAnim.setStartDelay(1000);
        bgAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

                masterlayout.setBackground(darkBackground);
                laterLayout.setVisibility(View.VISIBLE);


                AnimatorSet set2 = new AnimatorSet();
                set2.setDuration(1000);
                set2.playTogether(ObjectAnimator.ofFloat(layout, View.Y, 0, top+140+10), ObjectAnimator.ofFloat(layout, View.ALPHA, 0, 1), ObjectAnimator.ofFloat(laterLayout, View.ALPHA, 1,0));
                set2.start();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                ArrayList<Animator> list = new ArrayList<Animator>();

                list.add(ObjectAnimator.ofFloat(layout, View.ALPHA, 1, 0));

                AnimatorSet set = new AnimatorSet();
                set.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub

                    }
                });
                set.setInterpolator(new BounceInterpolator());
                set.setDuration(500);
                set.playTogether(list);
                set.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

        bgAnim.start();

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {

        goBack();

    }

    public void goBack(){


        runExitAction(new Runnable() {

            @Override
            public void run() {

                finish();

            }
        });

    }

    public Bitmap getBitmapFromName(String Name){

        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput(Name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(inputStream);
    }

    public void callFriends(){

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }

    private AlertDialog makeDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        View v = getLayoutInflater().inflate(R.layout.oops_view, null);

        Typeface font = Typeface.createFromAsset(getAssets(),"font.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font1.TTF");

        ((TextView)v.findViewById(R.id.oops_title)).setTypeface(font1);
        ((TextView)v.findViewById(R.id.oops_message)).setTypeface(font);

        Button tryAgain = (Button)v.findViewById(R.id.try_again);
        tryAgain.setTypeface(font1);

        alertDialogBuilder.setView(v);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;

        alertDialog.setCancelable(false);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();

            }
        });
        return alertDialog;
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
