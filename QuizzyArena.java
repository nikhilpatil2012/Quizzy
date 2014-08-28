package tronbox.QuizzyArena;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import tronbox.arena.MessageSender;
import tronbox.arena.Question;
import tronbox.arena.Score;
import tronbox.arena.ScoreActivity;
import tronbox.social.R;
import tronbox.welcome.MasterHomeScreen;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SharedPrefrenceStorage;


public class QuizzyArena extends Activity {

    int Animation_Code = 0;
    int point = 0, count = 0;

/* Initialize Top View */

    private TextView player1Name, player1Level, player2Name, player2Level, player1Score, player2Score;
    int userScore = 0, challengerScore = 0;

    private ImageView player1Pic, player2Pic;

    private ImageView timer;
    private AnimationDrawable timerAnimation;

    ImageView round, round1, round2, round3, bonusRound;

    FrameLayout roundLayout, timerLayout;
    LinearLayout verticalLayout, horizontalLayout;

    private Animator.AnimatorListener animatorListener0 = null;
    private Animator.AnimatorListener animatorListener1 = null;


    ArrayList<Animator> animatorArrayList;
    ArrayList<Animator> animatorTempArrayList;
    ArrayList<Button> buttonArrayList;


    private AnimatorSet set0 = new AnimatorSet();
    private AnimatorSet set1 = new AnimatorSet();
    private AnimatorSet set2 = new AnimatorSet();

    private AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);

    private HandlerController controller;
    private HandlerThread handlerThread;


    private Question question = null;
    private Bitmap bitmap = null;

    private int time = 1000;
    private Handler callScoreScreen = new Handler();
    private Runnable caller;

//////////////////////////////////////////////////


/* Initialize "Question + Image + Options " View */

    private TextView questionText;
    private ImageView imageView;
    private Button option1, option2, option3, option4, option5, option6, option7, option8;
    private Button o1, o2, o3, o4, Snitch;


////////////////////////////////////////////////////


    public static ArrayList<Question> list_Question = new ArrayList<Question>();
    public static HashMap<String, String> imageQuestionIdPool = new HashMap<String, String>();
    public static ArrayList<String> imageIdsPool = new ArrayList<String>();
    public static ArrayList<Score> scoreList = new ArrayList<Score>(10);
    public static ArrayList<Score> userScoreList = new ArrayList<Score>();


    private int questionNum = 0;

    LoopQuestions handler;

    LinearLayout visible;

    String userAnswer = "null", correctAnswer = "null";

    private int timerCount = 10;

    private int scoreValue = 0;

    private String userCode, userName, userFbId, challengerUserCode, challengerName, challengerFbId, Mode, isCorrect = "no";

    private LinearLayout.LayoutParams buttonSize;

    private StringBuffer ScoreBuffer = new StringBuffer();

    private String ChallengerBuffer;

    private int SHOW_ROUND = 2000;

    private int SHOW_QUESTION = 2000;

    private int messageCount = 0;

    private volatile boolean flip = false;

    private int soint = 0;

    private int wrong = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizzy_arena);

        getActionBar().hide();


        init();


    }

    public void init(){

        buttonSize = new LinearLayout.LayoutParams(0, 100);


        Bundle profileInfo = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());
        userCode = profileInfo.getString("UserCode");
        userName = profileInfo.getString("Name");
        userFbId = profileInfo.getString("FacebookId");

        challengerName = QuizzyApplication.challengerName;
        challengerFbId = QuizzyApplication.challengerFacebookId;
        challengerUserCode = QuizzyApplication.challengerUserCode;

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

        player1Name = (TextView)findViewById(R.id.player_1_name);
        player1Name.setText(userName);

        player2Name = (TextView)findViewById(R.id.player_2_name);
        player2Name.setText(challengerName);

        player1Level = (TextView)findViewById(R.id.player_1_level);
        player2Level = (TextView)findViewById(R.id.player_2_level);

        player1Score = (TextView)findViewById(R.id.player_1_score);
        player1Score.setText(String.valueOf(userScore));

        player2Score = (TextView)findViewById(R.id.player_2_score);
        player2Score.setText(String.valueOf(challengerScore));

        player1Pic = (ImageView)findViewById(R.id.player_1_pic);
        player1Pic.setImageBitmap(imageCircleClip(getBitmap(userFbId+".png")));

        player2Pic = (ImageView)findViewById(R.id.player_2_pic);
        player2Pic.setImageBitmap(imageCircleClip(getBitmap(challengerFbId+".png")));

        timer = (ImageView)findViewById(R.id.timer_animation);
        timer.setBackgroundResource(R.drawable.timer);
        timerAnimation = (AnimationDrawable)timer.getBackground();


        animatorArrayList = new ArrayList<Animator>();
        animatorTempArrayList = new ArrayList<Animator>();

       buttonArrayList = new ArrayList<Button>();

        handlerThread = new HandlerThread("");
        handlerThread.start();

        controller = new HandlerController(handlerThread.getLooper());

        handler = new LoopQuestions();

        timerLayout = (FrameLayout)findViewById(R.id.timer_layout);

        verticalLayout = (LinearLayout)findViewById(R.id.vertial_layout);
        horizontalLayout = (LinearLayout)findViewById(R.id.horizontal_layout);


        questionText = (TextView)findViewById(R.id.question);

        imageView = (ImageView)findViewById(R.id.image_question);

        round1 = (ImageView)findViewById(R.id.round_1);
        //round2 = (ImageView)findViewById(R.id.round_2);


        option1 = (Button)findViewById(R.id.one);
        option1.setOnClickListener(listener);

        option2 = (Button)findViewById(R.id.two);
        option2.setOnClickListener(listener);

        option3 = (Button)findViewById(R.id.three);
        option3.setOnClickListener(listener);

        option4 = (Button)findViewById(R.id.four);
        option4.setOnClickListener(listener);

        option5 = (Button)findViewById(R.id.five);
        //option5.setLayoutParams(buttonSize);
        option5.setOnClickListener(listener);

        option6 = (Button)findViewById(R.id.six);
        //option6.setLayoutParams(buttonSize);
        option6.setOnClickListener(listener);

        option7 = (Button)findViewById(R.id.seven);
        //option7.setLayoutParams(buttonSize);
        option7.setOnClickListener(listener);

        option8 = (Button)findViewById(R.id.eight);
       // option8.setLayoutParams(buttonSize);
        option8.setOnClickListener(listener);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            if(bundle.containsKey("Mode")){

                if(bundle.getString("Mode").equals("Play")){

                    Mode = "Play";

                    list_Question.addAll(QuizzyApplication.list_Question);
                    imageQuestionIdPool.putAll(QuizzyApplication.imageQuestionIdPool);
                    imageIdsPool.addAll(QuizzyApplication.imageIdsPool);
                    scoreList.addAll(QuizzyApplication.scoreList);

                    QuizzyApplication.list_Question.clear();
                    QuizzyApplication.imageQuestionIdPool.clear();
                    QuizzyApplication.imageIdsPool.clear();
                    QuizzyApplication.scoreList.clear();

                }else if(bundle.getString("Mode").equals("CHALLENGE")){

                    Mode = "CHALLENGE";

                    player2Pic.setImageBitmap(imageCircleClip(getBitmap(challengerFbId+".png")));

                    registerReceiver(broadcastReceiver, new IntentFilter("QUIZZY_ARENA"));

                    list_Question.addAll(QuizzyApplication.list_Question);
                    imageQuestionIdPool.putAll(QuizzyApplication.imageQuestionIdPool);
                    imageIdsPool.addAll(QuizzyApplication.imageIdsPool);

                    QuizzyApplication.list_Question.clear();
                    QuizzyApplication.imageQuestionIdPool.clear();
                    QuizzyApplication.imageIdsPool.clear();

                }

            }
        }

        controller.sendEmptyMessage(1);

    }

    @Override
    protected void onPause() {

        if(Mode.equals("CHALLENGE")){

            unregisterReceiver(broadcastReceiver);
        }

        super.onPause();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            userAnswer = ((Button)view).getText().toString();

            if(!userAnswer.equals(correctAnswer)){

                generateScore();

                ((Button)view).setBackgroundResource(R.drawable.oval_button_red_shape);

                player1Score.setBackgroundResource(R.drawable.text_circle_red_shape);

            }else {

                isCorrect = "yes";

                generateScore();

                ((Button)view).setBackgroundResource(R.drawable.oval_button__green_shape);

                player1Score.setBackgroundResource(R.drawable.text_circle_green_shape);

            }

            if(Mode.equals("Play")){

                timerAnimation.stop();
            }

             //   controller.flip();


        }
    };

    class LoopQuestions extends Handler {


        @Override
        public void handleMessage(Message msg) {


                if(msg.what == 1){ // show round fragment

                    postDelayed(showRound, 500);
                    controller.sendEmptyMessageDelayed(1, 4000);

                }else if(msg.what == 100){


                    if(questionNum < 10){

                        post(runner0);


                    } else {

                        controller.stop();
                        removeCallbacks(showRound);
                        removeCallbacks(runner0);
                        removeCallbacks(clearRunner);

                    }


                }else if(msg.what == 101){

                    post(clearRunner);

                    controller.sendEmptyMessageDelayed(1, 4000);


                }


        }

        Runnable showRound = new Runnable() {
            @Override
            public void run() {

                round1.setVisibility(View.VISIBLE);

                Log.w("Round_Count", ""+count);

                if(count == 1){

                    round1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.round_1));

                }else if(count == 5){

                    round1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.round_2));

                }else if(count == 9){

                    round1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.round_3));

                }else if(count == 13){

                    round1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bonus_round));

                }else if(count == 15){

                    round1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bonus_round));

                }


                animatorListener0 = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                        round1.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        round1.setVisibility(View.GONE);

                        set0.removeListener(animatorListener0);


                        if(questionNum == 10 && count == 15){

                            callScoreScreen();
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                };


                set0.playSequentially(ObjectAnimator.ofFloat(round1, View.ALPHA, 0, 1).setDuration(SHOW_ROUND), ObjectAnimator.ofFloat(round1, View.ALPHA, 1, 0).setDuration(SHOW_ROUND)); // 4 SECOND ANIMATION
                set0.addListener(animatorListener0);
                set0.start();


            }
        };

        Runnable runner0 = new Runnable() {
            @Override
            public void run() {


                timerAnimation.start();

                clear();

                question = nextQuestion();
                correctAnswer = question.getAns();


                bitmap = null;

                try{

                    if(!question.getqImage().equals("null") && question.getqImage().length() > 5){

                        Log.w("QuizzyArenaImage", question.getqKey()+".png");

                        bitmap = getBitmap(question.getqKey()+".png");

                    }

                }catch (NullPointerException el){

                    if(el.getMessage() != null){

                        Log.w("NUll Exception", el.getMessage());

                    }


                }


                questionText.setText(question.getqText());


                if(bitmap != null){ //bitmap is present

                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);

                    horizontalLayout.setVisibility(View.VISIBLE);
                    verticalLayout.setVisibility(View.GONE);

                    o1 = option5;
                    o2 = option6;
                    o3 = option7;
                    o4 = option8;


                }else {


                    o1 = option1;
                    o2 = option2;
                    o3 = option3;
                    o4 = option4;

                    imageView.setVisibility(View.GONE);
                    verticalLayout.setVisibility(View.VISIBLE);
                    horizontalLayout.setVisibility(View.GONE);
                }

                animatorArrayList.clear();

                animatorArrayList.add(ObjectAnimator.ofFloat(questionText, View.ALPHA, 0,1));
                animatorArrayList.add(ObjectAnimator.ofFloat(timerLayout, View.ALPHA, 0,1));



                if(bitmap != null){

                    animatorArrayList.add(ObjectAnimator.ofFloat(imageView, View.ALPHA, 0, 1));
                }


                animatorListener1 = new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animator) {


                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        switch (Animation_Code){

                            case 100:{

                                animatorArrayList.clear();

                                String[] options = question.getOpt().split(",");

                                o1.setText(options[0]);
                                o2.setText(options[1]);
                                o3.setText(options[2]);
                                o4.setText(options[3]);

                                o1.setVisibility(View.VISIBLE);
                                o2.setVisibility(View.VISIBLE);
                                o3.setVisibility(View.VISIBLE);
                                o4.setVisibility(View.VISIBLE);


                                o1.setBackgroundResource(R.drawable.oval_button_shape);
                                o2.setBackgroundResource(R.drawable.oval_button_shape);
                                o3.setBackgroundResource(R.drawable.oval_button_shape);
                                o4.setBackgroundResource(R.drawable.oval_button_shape);


                                animatorArrayList.add(ObjectAnimator.ofFloat(o1, View.ALPHA, 0, 1));
                                animatorArrayList.add(ObjectAnimator.ofFloat(o2, View.ALPHA, 0, 1));
                                animatorArrayList.add(ObjectAnimator.ofFloat(o3, View.ALPHA, 0, 1));
                                animatorArrayList.add(ObjectAnimator.ofFloat(o4, View.ALPHA, 0, 1));

                                Animation_Code = 102;
                                set2.playTogether(animatorArrayList);
                                set2.addListener(animatorListener1);
                                set2.setDuration(1000);
                                set2.start();


                            } break;

                            case 102:{

                                set1.removeListener(animatorListener1);
                                set2.removeListener(animatorListener1);

                            }break;


                        }


                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                };



                Animation_Code = 100;
                set1.playTogether(animatorArrayList);
                set1.addListener(animatorListener1);
                set1.setDuration(2000);
                set1.start();

            }
        };

        Runnable clearRunner = new Runnable() {
            @Override
            public void run() {

                if(questionNum != wrong){

                    Log.w("WRONGWRONGWORNG", "Wrong = "+wrong);
                }

                animatorArrayList.clear();
                timerAnimation.stop();

                    generateScore();

                animation2.setDuration(1000);
                animation2.setStartOffset(1000);
                animation2.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(ObjectAnimator.ofFloat(questionText, View.ALPHA, 1, 0),ObjectAnimator.ofFloat(imageView, View.ALPHA, 1, 0), ObjectAnimator.ofFloat(Snitch, View.ALPHA, 1, 0), ObjectAnimator.ofFloat(timerLayout, View.ALPHA, 1, 0));
                        set.setDuration(1000);
                        set.setStartDelay(1000);
                        set.addListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                o1.setVisibility(View.GONE);
                                o2.setVisibility(View.GONE);
                                o3.setVisibility(View.GONE);
                                o4.setVisibility(View.GONE);


                                if(questionNum == 10){

                                    callScoreScreen();
                                }

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        set.start();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });



                if(!o1.getText().equals(question.getAns())){

                    o1.startAnimation(animation2);
                    o1.setVisibility(View.INVISIBLE);
                    //buttonArrayList.add(o1);

                }else{

                    Snitch = o1;
                }

                if(!o2.getText().equals(question.getAns())){

                    o2.startAnimation(animation2);
                    o2.setVisibility(View.INVISIBLE);
                    //buttonArrayList.add(o2);

                }else{


                    Snitch = o2;
                }

                if(!o3.getText().equals(question.getAns())){

                    o3.startAnimation(animation2);
                    o3.setVisibility(View.INVISIBLE);
                   // buttonArrayList.add(o3);

                }else {

                    Snitch = o3;
                }

                if(!o4.getText().equals(question.getAns())){

                    o4.startAnimation(animation2);
                    o4.setVisibility(View.INVISIBLE);
                   // buttonArrayList.add(o4);

                }else {

                    Snitch = o4;
                }

                if(Snitch != null){

                    Snitch.setBackgroundResource(R.drawable.oval_button__green_shape);
                }


            }
        };

        public void stop(){

            removeCallbacks(runner0);
            removeCallbacks(showRound);
            removeCallbacks(clearRunner);

        }
    }

    public Question nextQuestion(){

        Question question = null;

        if(questionNum < 10 && list_Question.size() == 10){

            question = list_Question.get(questionNum);

            Log.w("Question_Num", ""+questionNum);

            questionNum++;
        }

        return question;
    }


    class HandlerController extends Handler {

        private long sysTime = 0;
        boolean timerStop = false;

        public HandlerController(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            timerStop = false;
            timerCount = 13;

            post(worker);

        }

        Runnable worker = new Runnable() {
            @Override
            public void run() {

                if(count < 14){

                    if(count == 0 || count == 4 || count == 8 || count == 12 || count == 14){

                        handler.sendEmptyMessage(1); // show round image

                    } else {


                        handler.sendEmptyMessage(100);

                        while( timerCount > 0  && timerStop == false){

                            if((System.currentTimeMillis()/1000) - sysTime >= 1){

                                timerCount = timerCount - 1;
                                Log.w("Count", ""+timer);

                                sysTime = System.currentTimeMillis()/1000;
                            }
                        }

                        handler.sendEmptyMessage(101);

                    }

                    count++;

                }
            }
        };

        public void flip(){

            timerStop = true;
        }

        public void stop(){

            removeCallbacks(worker);

        }

    }

    public void generateScore(){

        Log.w("ScoreCount", ""+soint++);

        if(questionNum <= 10 && flip == false){

            flip = true;

            scoreValue = 0;

            if(correctAnswer.equals(userAnswer)){

                    if(questionNum == 9){

                      scoreValue = 20 + (10-timerCount);

                  }else{

                        scoreValue = 10 + (10-timerCount);

                    }

            }


            userScore = userScore + scoreValue;

            userScoreList.add(new Score(question.getqKey(), String.valueOf((int) timerCount), String.valueOf(scoreValue)));

            player1Score.setText(String.valueOf(userScore));

            if(Mode.equals("CHALLENGE")){

          ScoreBuffer.append(scoreValue+"#");

       new MessageSender().execute(userCode, challengerUserCode, "SCORE@"+scoreValue+"@"+isCorrect+"@"+timerCount+"@"+ScoreBuffer.toString());

                int val = questionNum - messageCount;

                if(val > 2){

                     Log.w("MESSAGE_NOT_COMING", "MESSAGE_NOT_COMING");
                }

            } else if(Mode.equals("Play")) {

                Log.w("ScoreListSize", "Size = "+scoreList.size());


                if (questionNum < 10 && scoreList.size() == 10){

                    String score = scoreList.get(questionNum).getScore();

                    Log.w("ScoreListSize", "Size = "+score);

                    if(score != null){

                        challengerScore = challengerScore + Integer.parseInt(score);

                        player2Score.setText(String.valueOf(challengerScore));

                        if(score.equals("0")){

                            player2Score.setBackgroundResource(R.drawable.text_circle_red_shape);

                        } else {

                            player2Score.setBackgroundResource(R.drawable.text_circle_green_shape);

                        }

                    }


                }

            }


        }


    }

    public Bitmap getBitmap(String name){

        Bitmap bitmap = null;

        try{

            bitmap = BitmapFactory.decodeStream(openFileInput(name));

        }catch (FileNotFoundException ex){

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_image);
        }


        return bitmap;
    }

    public void callScoreScreen(){

        caller = new Runnable() {

            @Override
            public void run() {

                new MessageSender().execute(userCode, challengerUserCode, "SCORE@"+scoreValue+"@"+isCorrect+"@"+timerCount+"@"+ScoreBuffer.toString());

                for(Score s : userScoreList){

                    Log.w("UserMessage", s.getScore());
                }


                for(Score s : scoreList){

                    Log.w("ChallengerScore", s.getScore());
                }

                Log.w("ScoreBUffer", ScoreBuffer.toString());

                Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putParcelableArrayListExtra("P1_SCORE", userScoreList);
                intent.putParcelableArrayListExtra("P2_SCORE", scoreList);

                intent.putExtra("P1_NAME", userName);
                intent.putExtra("P2_NAME", challengerName);

                intent.putExtra("GAME_CODE", QuizzyApplication.gameCode);

                intent.putExtra("USER_CODE", userCode);

                intent.putExtra("P1_TITLE", "Hustler");
                intent.putExtra("P2_TITLE", "Avenger");

                intent.putExtra("CATAGORY", QuizzyApplication.SubjectCode);
                intent.putExtra("CHAP_ID", QuizzyApplication.ChapterCode);

                if(Mode.equals("CHALLENGE")){

                    intent.putExtra("Buffer", ChallengerBuffer);
                }

                intent.putExtra("UserImage", userFbId+".png");

                intent.putExtra("ChallengerImage", challengerFbId+".png");

                startActivity(intent);


                userScoreList.clear();
                list_Question.clear();
                imageQuestionIdPool.clear();
                imageIdsPool.clear();
                scoreList.clear();

                messageCount = 0;

                finish();
            }

        };

       callScoreScreen.postDelayed(caller, time);

    }

    public  Bitmap imageCircleClip(Bitmap sourceBitmap){

        int targetWidth = 100;
        int targetheight = 100;

        Bitmap outputBitmap = Bitmap.createBitmap(targetWidth, targetheight, Bitmap.Config.ARGB_8888);

        Path path = new Path();
        path.addCircle(targetWidth/2, targetheight/2, targetWidth/2, Path.Direction.CCW);

        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);

        Rect src = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Rect out = new Rect(0, 0, targetWidth, targetheight);

        Bitmap source = sourceBitmap;

        canvas.drawBitmap(source, src, out, null);


        Paint pp = new Paint(Paint.ANTI_ALIAS_FLAG);
        pp.setStrokeWidth(12);
        pp.setStyle(Paint.Style.STROKE);
        pp.setColor(Color.parseColor("#FF4500"));
        canvas.drawCircle(50, 50, 50, pp);


        return outputBitmap;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(scoreList.size() < 11){

                Bundle bundle = intent.getExtras();

                if(bundle != null && bundle.containsKey("TAG")){

                    String Tag = bundle.getString("TAG");

                    if(Tag.equals("SCORE_VALUE") && bundle.containsKey("SCORE") && bundle.containsKey("CORRECT")){

                        messageCount++;

                        String score = bundle.getString("SCORE");
                        String correct = bundle.getString("CORRECT");
                        String timerCount = bundle.getString("TIMERCOUNT");

                        ChallengerBuffer = bundle.getString("BUFFER");

                        Log.w("BUFFERRRRR", ChallengerBuffer);


                        challengerScore = challengerScore + Integer.parseInt(score);

                        player2Score.setText(String.valueOf(challengerScore));

                        if(correct.equals("yes")){

                            player2Score.setBackgroundResource(R.drawable.text_circle_green_shape);

                        }else if(correct.equals("no")){

                            player2Score.setBackgroundResource(R.drawable.text_circle_red_shape);

                        }

                        wrong++;

                        if(flip == true){

                            timerAnimation.stop();
                            controller.flip();

                        }


                        scoreList.add(new Score("no use", timerCount, score));

                        Log.w("ACKNOLEGE", score);
                    }

                }

            }

        }
    };

    public void clear(){

        isCorrect = "no";

        player1Score.setBackgroundResource(R.drawable.circle_shape);
        player2Score.setBackgroundResource(R.drawable.circle_shape);


        option1.setVisibility(View.GONE);
        option2.setVisibility(View.GONE);
        option3.setVisibility(View.GONE);
        option4.setVisibility(View.GONE);
        option5.setVisibility(View.GONE);
        option6.setVisibility(View.GONE);
        option7.setVisibility(View.GONE);
        option8.setVisibility(View.GONE);

        flip = false;


    }
}
