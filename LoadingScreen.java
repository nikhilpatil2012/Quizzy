package tronbox.arena;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tronbox.Notification.FriendUserCode;
import tronbox.controller.QuizzyDatabase;
import tronbox.social.FakeUserService;
import tronbox.social.GameCodeQuestionDownloader;
import tronbox.social.GetScoreWithUserCode;
import tronbox.social.PlayLaterDownloader;
import tronbox.social.R;

import tronbox.social.VsActivity;
import tronbox.welcome.LeaderBoard;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SharedPrefrenceStorage;
import tronbox.welcome.UpdateActivity;

public class LoadingScreen extends Activity{

    private TextView message;
    private AnimatorSet set = new AnimatorSet();
    private AnimatorSet set1 = new AnimatorSet();

    private Point point = new Point();

    private String ChallengerName, ChallengerFacebookId;
    private String QuizzyMode;

    private FakeUserService fakeUserService;
    private GameCodeQuestionDownloader gameCodeQuestionDownloader;
    private FriendUserCode friendUserCode;
    private MessageSender messageSender;
    private PlayLaterDownloader playLaterDownloader;

    private LinearLayout startNow;

    private String gameCode = "null";

    private boolean stopGame = false;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        typeface = Typeface.createFromAsset(getAssets(), "tittle.ttf");

        stopGame = true;

        getActionBar().hide();

        getWindowManager().getDefaultDisplay().getSize(point);

        startNow = (LinearLayout)findViewById(R.id.start_now);

        message = (TextView)findViewById(R.id.loading_message);
        message.setTypeface(typeface);

        fakeUserService = new FakeUserService(this);
        gameCodeQuestionDownloader = new GameCodeQuestionDownloader(this);
        friendUserCode = new FriendUserCode(this);
        messageSender = new MessageSender();
        playLaterDownloader = new PlayLaterDownloader(this);

        ((ImageView)findViewById(R.id.qz_logo)).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim));

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            if(bundle.containsKey("Message")){

                message.setText(bundle.getString("Message"));
                set1.play(ObjectAnimator.ofFloat(message, View.X, -400, point.x/4));
                set1.setDuration(500);
                set1.start();

            }

            if(bundle.containsKey("Mode")){

                String Mode = bundle.getString("Mode");

                if (Mode.equals("Play")){

                    QuizzyMode = "Play";

                    handler.sendEmptyMessage(1);

                    registerReceiver(receiver, new IntentFilter("LOADING_ACTIONS"));

                }else if(Mode.equals("CHALLENGE")) {

                    QuizzyMode = "CHALLENGE";

                    if(bundle.containsKey("Seed")) {

                        gameCode = bundle.getBundle("Seed").getString("GameCode");

                        handler.sendEmptyMessage(2);

                    }

                }else if(Mode.equals("CHALLENGE_FRIEND")){


                    Log.w("SENDING_FRIEND_REQUEST", "LOADING_SCREEN_ON");


                    QuizzyMode = "CHALLENGE_FRIEND";

                    registerReceiver(receiver, new IntentFilter("LOADING_ACTIONS"));


                    if(bundle.containsKey("Name") && bundle.containsKey("Id")){

                        ChallengerName = bundle.getString("Name");
                        ChallengerFacebookId = bundle.getString("Id");

                        //handler.sendEmptyMessage(3);

                    }

                }else if(Mode.equals("Play_Later")){

                    if(bundle.containsKey("Seed")){

                        Bundle Seed = bundle.getBundle("Seed");

                        QuizzyApplication.gameCode = Seed.getString("GameCode");
                        QuizzyApplication.challengerName = Seed.getString("ChallengerName");
                        QuizzyApplication.challengerFacebookId = Seed.getString("ChallengerFacebookId");
                        QuizzyApplication.challengerUserCode = Seed.getString("ChallengerUserCode");
                        QuizzyApplication.ScoreBuffer = Seed.getString("BUFFER");
                        QuizzyApplication.SubjectCode = Seed.getString("SubjectCode");
                        QuizzyApplication.ChapterCode = Seed.getString("ChapterCode");

                        Log.w("ScoreBufferGameCode", QuizzyApplication.gameCode);
                        Log.w("ScoreBufferName", QuizzyApplication.challengerName);
                        Log.w("ScoreBufferFB", QuizzyApplication.challengerFacebookId);
                        Log.w("ScoreBufferUserCode", QuizzyApplication.challengerUserCode);
                        Log.w("ScoreBuffer", QuizzyApplication.ScoreBuffer);
                        Log.w("SubjectCode", QuizzyApplication.SubjectCode);
                        Log.w("ChapterCode", QuizzyApplication.ChapterCode);


                        handler.sendEmptyMessage(5);
                    }

                }else if(Mode.equals("Ranking")){

                    // Fetch all data from database
                    QuizzyDatabase database = new QuizzyDatabase(getApplicationContext(),"QUIZZY",null,1);
                    Cursor c = database.getUserCodesFriends();
                    StringBuilder builder = new StringBuilder();
                    if(c.moveToFirst())
                    {
                        do {
                            if(c.isFirst())
                            {
                                builder.append(c.getString(0));
                            }
                            else
                            {
                                builder.append("~").append(c.getString(0));
                            }
                      }while (c.moveToNext());
                    }
                    if(builder.length()>0)
                    {
                        new GetScoreWithUserCode(this).execute(builder.toString(),QuizzyApplication.SubjectCode,QuizzyApplication.ChapterCode);
                    }
                }
            }
        }
    }

    public void callLeaderBoard(String tag){

        if(tag.equals("user"))
        {
            Intent intent = new Intent(getApplicationContext(), LeaderBoard.class);
            intent.putExtra("USER",true);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), LeaderBoard.class);
            intent.putExtra("USER",false);
            startActivity(intent);
        }

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            String action = intent.getExtras().getString("TAG");

            if(action.equals("START_NOW")){

                callQuizzyArena("CHALLENGE");

            }else if(action.equals("FRIEND_BUSY")){

                message.setText("Friend is busy you can start now");

            }else if(action.equals("NEW_MESSAGE")){

                message.setText(intent.getExtras().getString("MESSAGE"));


            }else if(action.equals("START_PLAYING")){

                message.setText("Preparing Game Arena");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message.setVisibility(View.GONE);

                        startNow.setVisibility(View.VISIBLE);

                        ((Button)startNow.findViewById(R.id.start_game)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                QuizzyApplication.PlayLater = true;
                                handler.sendEmptyMessage(4);
                                Intent intent = new Intent(getApplicationContext(), VsActivity.class);
                                intent.putExtra("Mode", "CHALLENGE");
                                startActivity(intent);
                            }
                        });

                  ((TextView)findViewById(R.id.start_now_message)).setText(QuizzyApplication.challengerName+" will catch up later");

                    }
                },10000);

            }

        }
    };

    @Override
    protected void onPause() {

        if(QuizzyMode != null){

            if(QuizzyMode.equals("CHALLENGE_FRIEND") || QuizzyMode.equals("Play")){

                try{

                    unregisterReceiver(receiver);

                }catch(IllegalArgumentException ex){Log.w("BroadCast_Error", ex.getMessage());}

            }
        }

        stopGame = false;

        finish();
        super.onPause();
    }

    public void callQuizzyArena(String Mode){


        if(stopGame == true){

            if(Mode.equals("CHALLENGE")){

                handler.sendEmptyMessage(4);

            }

            Intent intent = new Intent(getApplicationContext(), VsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Mode", Mode);
            startActivity(intent);
        }


    }

    public void waitForAck(String Mode){

        message.setText("You can start now " + ChallengerName + " w'll catch up later");
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(stopGame == true){

                switch (msg.what){

                    case 1:{

                        fakeUserService.execute(getIntent().getExtras().getString("SubjectCode"), getIntent().getExtras().getString("ChapterCode"), SharedPrefrenceStorage.getUserCode(getApplicationContext()));

                    }break;

                    case 2:{

                        gameCodeQuestionDownloader.execute(gameCode);


                        Log.w("FRIEND_REQUEST", "LOADING_SCREEN_ON + GAME_CODE_QUESTIONS_DOWNLOADING");


                    }break;

                    case 3:{

                        friendUserCode.execute("FB", ChallengerFacebookId, ChallengerName);

                        Log.w("SENDING_FRIEND_REQUEST", "GENERATING_FRIEND_USER_CODE");

                    }break;


                    case 4:{

                        messageSender.execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()), QuizzyApplication.challengerUserCode, "CHALLENGE_ACCEPTED@Null");

                    }break;


                    case 5:{

                        playLaterDownloader.execute(QuizzyApplication.gameCode);

                    }break;

                }
            }
        }
    };

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("From_Score", "Home");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        QuizzyApplication.ScreenNumber = 101; // loading screen
    }
}
