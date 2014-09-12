package tronbox.arena;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import tronbox.Sounds.AndroidAudio;
import tronbox.Sounds.Audio;
import tronbox.controller.QuizzyDatabase;
import tronbox.social.R;
import tronbox.social.SelfieSender;
import tronbox.welcome.InsertUserScore;
import tronbox.welcome.MasterHomeScreen;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SharedPrefrenceStorage;


public class ScoreActivity extends Activity {

    private static TextView text;
    private ProgressBar bar;
    private int count,player_1_score,player_2_score,LEVEL_PROGRESS;
    private Handler handler;
    private TextView p1_score,p2_score,level_text;
    private int[] firstPlayerScore,secondPlayerScore;
    private String p1_name,p2_name,p1_title,p2_title,CHAP_ID,CATAGORY,p1_image,p2_image;
    private DisplayMetrics MAT;
    private ActionBar actionBar;
    private Typeface font;
    private QuizzyDatabase database;
    private String Buffer;
    private Audio audio;
    private Boolean bufferPresent = false;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private float kill_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_dynamic_view);

        audio = new AndroidAudio(this);
        QuizzyApplication.gameSound = audio.newMusic("playback.ogg");
        QuizzyApplication.gameSound.setLooping(true);
        QuizzyApplication.gameSound.play();

        database = new QuizzyDatabase(getApplicationContext(),"QUIZZY",null,1);

        actionBar = getActionBar();

        font = Typeface.createFromAsset(getAssets(),"font.ttf");
        initActionBar();

        Bundle b = getIntent().getExtras();
        if (b != null) {

            for(Score s : QuizzyApplication.userScoreList){

                Log.w("AfterUserMessage", s.getScore());
                kill_time = kill_time + Integer.valueOf(s.getTime());
            }

            kill_time = 10f-(kill_time/10f);


            for(Score s : QuizzyApplication.challengerList){

                Log.w("AfterChallengerScore", s.getScore());
            }

            p1_name = b.getString("P1_NAME");
            p2_name = b.getString("P2_NAME");


            firstPlayerScore = new int[10];
            secondPlayerScore = new int[10];


            if((QuizzyApplication.userScoreList != null) && (QuizzyApplication.userScoreList.size() == 10))
            {
                for (int i = 0; i < QuizzyApplication.userScoreList.size(); i++) {
                    Score c = (Score) QuizzyApplication.userScoreList.get(i);
                    firstPlayerScore[i] = Integer.valueOf(c.getScore());
                }
            }


            if(b.containsKey("Buffer")){

                Buffer = b.getString("Buffer");

                bufferPresent = true;

                if(Buffer.length() > 19){

                    String[] score = Buffer.split("#");

                    if(score.length > 0 && score.length <=10){

                        for (int i = 0; i < score.length; i++) {
                            secondPlayerScore[i] = Integer.valueOf(score[i]);
                        }

                    }

                }else { // Complete score has not recieved

                    for (int i = 0; i < 10; i++) {

                        secondPlayerScore[i] = 0;

                    }

                }


            }

            else {

                if((QuizzyApplication.challengerList != null))

                {
                    for (int i = 0; i < QuizzyApplication.challengerList.size(); i++) {
                        Score c = (Score) QuizzyApplication.challengerList.get(i);
                        secondPlayerScore[i] = Integer.valueOf(c.getScore());
                    }

                }

            }




            if((b.getString("USER_CODE")!=null) && (b.getString("GAME_CODE")!=null) && (QuizzyApplication.userScoreList != null))
            {
                //     new ScoreSender(getApplicationContext()).execute(b.getString("USER_CODE"), b.getString("GAME_CODE"), getScoreString(QuizzyApplication.userScoreList));
            }

            if( (b.getString("CATAGORY") != null) && (b.getString("CHAP_ID")!=null) )
            {
                CATAGORY = b.getString("CATAGORY");
                CHAP_ID = b.getString("CHAP_ID");
            }

            if(b.getString("P1_TITLE") != null)
            {

                //p1_title = b.getString("P1_TITLE");

                p1_title = getp1Title();
            }

            if(b.getString("P2_TITLE") != null)
            {
                p2_title = b.getString("P2_TITLE");
            }

            if( (b.getString("UserImage") != null) && (b.getString("ChallengerImage")!=null) )
            {
                p1_image = b.getString("UserImage");
                p2_image = b.getString("ChallengerImage");
            }

            database.updateKillTime(CHAP_ID,String.valueOf(kill_time));

            init();


            handler = new Handler();
            handler.postDelayed(new ProgressController(),300);
            // handler.post(new ProgressController());
        }

    }

    private String getp1Title()
    {
        int value = Integer.valueOf(database.getTotalScore(CHAP_ID));
        if(value <=400)
        {
            return "Newbee";
        }
        else if(value >400 && value <=800)
        {
            return "Hustler";
        }
        else if(value >800 && value <=1200)
        {
            return "Pro";
        }
        else if(value >1200 && value <=1600)
        {
            return "Avenger";
        }
        else
        {
            return "Genius";
        }
    }

    private void initActionBar()
    {
        actionBar.setTitle("Login");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar_score_activity, null);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mCustomView.setLayoutParams(params);
        actionBar.setCustomView(mCustomView);
        TextView actionBarText = (TextView)mCustomView.findViewById(R.id.nameOfScreen);
        actionBarText.setText("Scores");
        actionBarText.setTypeface(font);


        TextView back = (TextView)mCustomView.findViewById(R.id.back);//
        back.setText("Home");
        back.setTypeface(font);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MasterHomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        ImageButton add = (ImageButton) mCustomView.findViewById(R.id.add_ques);
        add.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_camera));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        add.setVisibility(View.VISIBLE);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void databaseOperations(int CORRECT_QUES,int player_1_score)
    {
        try
        {
            database.updateFullScore(CHAP_ID,String.valueOf(Integer.valueOf(database.getGlobalRank(CHAP_ID))),String.valueOf(player_1_score+Integer.valueOf(database.getTotalScore(CHAP_ID))),String.valueOf(Integer.valueOf(database.getTotalCorrect(CHAP_ID))+CORRECT_QUES),String.valueOf(1+Integer.valueOf(database.getTotalCount(CHAP_ID))));

        }catch (Exception e){

        }

    }

    private void init()
    {
        int CORRECT_QUES = 0;
        for (int aSecondPlayerScore : secondPlayerScore) {
            player_2_score = player_2_score + aSecondPlayerScore;
        }

        for (int aFirstPlayerScore : firstPlayerScore) {

            if(aFirstPlayerScore > 0)
            {
                CORRECT_QUES++;
            }

            player_1_score = player_1_score + aFirstPlayerScore;

        }

        //performs the database updations in local database
        databaseOperations(CORRECT_QUES,player_1_score);


        LEVEL_PROGRESS = Integer.valueOf(database.getTotalScore(CHAP_ID));

        MAT = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(MAT);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.base_view);

        bar = (ProgressBar)findViewById(R.id.level_progress_bar);
        bar.setMax(2000);

        View v11= (View)findViewById(R.id.first_player);
        View v21= (View)findViewById(R.id.second_player);

        p1_score = (TextView)v11.findViewById(R.id.p_score);
        p2_score = (TextView)v21.findViewById(R.id.p_score);

        p1_score.setTypeface(font);
        p2_score.setTypeface(font);

        if(player_1_score == 0)
        {
            p1_score.setText("0");
        }
        if(player_2_score == 0)
        {
            p2_score.setText("0");
        }

        ((TextView)findViewById(R.id.analytics)).setTypeface(font);

        TextView t1 = (TextView)v11.findViewById(R.id.p_name);
        t1.setText(p1_name);
        t1.setTypeface(font);
        TextView t2 = (TextView)v21.findViewById(R.id.p_name);
        t2.setText(p2_name);
        t2.setTypeface(font);

        TextView t3 = (TextView)v11.findViewById(R.id.p_title);
        t3.setText(p1_title);
        t3.setTypeface(font);
        TextView t4 = (TextView)v21.findViewById(R.id.p_title);
        t4.setText(p2_title);
        t4.setTypeface(font);

        level_text = (TextView)findViewById(R.id.level_text);
        level_text.setTypeface(font);

        FinalGraph finalGraph = new FinalGraph(getApplicationContext(),firstPlayerScore,secondPlayerScore);
        finalGraph.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(MAT.heightPixels*40)/100));


        View v = getLayoutInflater().inflate(R.layout.scoregrod, null);

        View v1 = getLayoutInflater().inflate(R.layout.nameline,null);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0,25,0,0);
        v1.setLayoutParams(p);

        TextView t5 = (TextView)v1.findViewById(R.id.player);
        t5.setText(p1_name);
        t5.setTypeface(font);

        View v2 = getLayoutInflater().inflate(R.layout.nameline,null);
        v2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((TextView)v2.findViewById(R.id.player)).setText(p2_name);

        TextView t6 = (TextView)v2.findViewById(R.id.player);
        t6.setText(p2_name);
        t6.setTypeface(font);

        GridView gv = (GridView)v.findViewById(R.id.grid);
        gv.setAdapter(new CustomBaseAdapter());
        gv.setPadding((MAT.widthPixels*5)/100, 0, (MAT.widthPixels*5)/100,0);
        gv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (MAT.heightPixels*18)/100));

        linearLayout.addView(finalGraph);
        linearLayout.addView(v1);
        linearLayout.addView(gv);
        linearLayout.addView(v2);


        text = ((TextView)findViewById(R.id.result));
        text.setTypeface(Typeface.createFromAsset(getAssets(),"font1.TTF"));


        if( (player_1_score < player_2_score))
        {
            text.setText("You Loose !");
            database.updateLoose(CHAP_ID);
            ((ImageView)findViewById(R.id.red_green)).setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.red_green_line));
            ((ImageView)findViewById(R.id.image_p1)).setImageBitmap(imageCircleClip(getBitmap(p1_image),"#db2121"));//red
            ((ImageView)findViewById(R.id.image_p2)).setImageBitmap(imageCircleClip(getBitmap(p2_image),"#01e04a"));//green
        }
        else if( (player_1_score > player_2_score))
        {


            if(bufferPresent == true && Buffer.length() < 19){

                text.setText(QuizzyApplication.challengerName+"'s Turn");
                ((ImageView)findViewById(R.id.red_green)).setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.red_green_line1));
                ((ImageView)findViewById(R.id.image_p1)).setImageBitmap(imageCircleClip(getBitmap(p1_image),"#ffffff"));//red
                ((ImageView)findViewById(R.id.image_p2)).setImageBitmap(imageCircleClip(getBitmap(p2_image),"#ffffff"));//green

            }else{

                Log.w("DEMO",p1_image+ " :: "+p2_image);
                text.setText("You Won !");
                database.updateWin(CHAP_ID);
                ((ImageView)findViewById(R.id.red_green)).setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.red_green_line1));
                ((ImageView)findViewById(R.id.image_p1)).setImageBitmap(imageCircleClip(getBitmap(p1_image),"#01e04a"));//red
                ((ImageView)findViewById(R.id.image_p2)).setImageBitmap(imageCircleClip(getBitmap(p2_image),"#db2121"));//green
            }
        }
        Log.w("DANGER",database.getTotalScore(CHAP_ID));

        //sending score data of user
        new InsertUserScore(getApplicationContext()).execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()),CATAGORY,CHAP_ID,String.valueOf(player_1_score),database.getWin(CHAP_ID),database.getLoose(CHAP_ID),String.valueOf(kill_time),SharedPrefrenceStorage.getDonations(getApplicationContext()),p1_title);

    }


    public Bitmap getBitmap(String name){

        Bitmap bitmap = null;

        try{

            bitmap = BitmapFactory.decodeStream(openFileInput(name));

        }catch (FileNotFoundException ex){

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.james);
        }


        return bitmap;
    }

    private String getScoreString(ArrayList<Score> list)
    {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<list.size();i++)
        {
            Score c = (Score)list.get(i);
            if(i==0)
            {
                builder.append(c.getQid()).append("~").append(c.getTime()).append("~").append(c.getScore());
            }
            else
            {
                builder.append(":").append(c.getQid()).append("~").append(c.getTime()).append("~").append(c.getScore());

            }
        }
        return builder.toString();
    }

    class ProgressController implements Runnable
    {
        private boolean textAnimationComplete;
        private int c;
        @Override
        public void run() {
            count++;

            if(count  <= player_1_score || count  <= player_2_score )
            {

                if(count  <= player_1_score)
                {
                    p1_score.setText(String.valueOf(count));
                }

                if(bufferPresent){

                    if(Buffer.length() > 19){

                        if(count  <= player_2_score)
                        {
                            p2_score.setText(String.valueOf(count));
                        }

                    }else{

                        p2_score.setText("?");

                    }

                }else {

                    if(count  <= player_2_score)
                    {
                        p2_score.setText(String.valueOf(count));
                    }

                }


                handler.postDelayed(this,2);
            }
            else
            {
                if(LEVEL_PROGRESS>10)
                {
                    c = c + LEVEL_PROGRESS/10;
                }
                else
                {
                    c++;
                }

                if(!textAnimationComplete)
                {
                    textAnimationComplete = true;
                    handler.post(this);

                }
                else {
                    if(c < LEVEL_PROGRESS)
                    {
                        bar.setProgress(c);
                        handler.post(this);
                    }
                    //if(c > LEVEL_PROGRESS)
                    else{
                        ObjectAnimator.ofFloat(level_text,"alpha",0,1).setDuration(500).start();
                        level_text.setText("Level "+getLevelOfUser(Integer.valueOf(database.getTotalScore(CHAP_ID))));
                    }
                }

            }

        }
    }

    private int getLevelOfUser(int TOTAL_SCORE)
    {
        int count = 0;
        while((TOTAL_SCORE) >= 0)
        {
            TOTAL_SCORE = TOTAL_SCORE -200;
            count++;
        }
        return count;
    }

    class CustomBaseAdapter extends BaseAdapter
    {
        private LayoutInflater inf;
        CustomBaseAdapter(){
            inf = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        int pos;
        @Override
        public int getCount() {
            return 30;
        }
        @Override
        public Object getItem(int arg0) {
            return null;
        }
        @Override
        public long getItemId(int arg0) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inf.inflate(R.layout.scoreframe, null);

            text = (TextView)convertView.findViewById(R.id.score);
            text.setGravity(Gravity.CENTER);

            //add score of first player
            if(position<=9)
            {

                text.setText(""+firstPlayerScore[position]);
                if(firstPlayerScore[position]==0)
                {
                    text.setBackgroundColor(Color.parseColor("#db2121"));
                }
                else
                {
                    text.setBackgroundColor(Color.parseColor("#01e04a"));
                }
            }

            //add question numbers
            if(position>9 && position <20)
            {
                text.setText(""+(pos+1));


                text.setBackgroundColor(Color.BLACK);
                pos++;
            }

            if(position>19)
            {
                text.setText(""+secondPlayerScore[position-20]);
                if(secondPlayerScore[position-20]== 0)
                {
                    text.setBackgroundColor(Color.parseColor("#db2121"));
                }
                else
                {
                    text.setBackgroundColor(Color.parseColor("#01e04a"));
                }
            }

            text.setTextColor(Color.WHITE);
            text.setLayoutParams(new android.widget.FrameLayout.LayoutParams((MAT.widthPixels*8)/100, (((MAT.heightPixels*18)/100)*25)/100));
            android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT ,(MAT.heightPixels*6)/100);

            convertView.setLayoutParams(params);

            return convertView;
        }
    }

    private Bitmap imageCircleClip(Bitmap sourceBitmap,String colorcode)
    {
        Bitmap outputBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        path.addCircle(50, 50, 50, Path.Direction.CCW);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        Rect src = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Rect out = new Rect(0, 0, 100, 100);
        Bitmap source = sourceBitmap;
        canvas.drawBitmap(source, src, out, null);

        Paint pp = new Paint(Paint.ANTI_ALIAS_FLAG);
        pp.setStrokeWidth(10);
        pp.setStyle(Paint.Style.STROKE);
        pp.setColor(Color.parseColor(colorcode));

        //FF4500
        canvas.drawCircle(50, 50, 50, pp);

        return outputBitmap;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        QuizzyApplication.userScoreList.clear();
        QuizzyApplication.challengerList.clear();


        database.close();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(QuizzyApplication.gameSound.isPlaying() && QuizzyApplication.backGroundSound == true){

            QuizzyApplication.gameSound.stop();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        if(QuizzyApplication.gameSound.isPlaying()){

            QuizzyApplication.gameSound.stop();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Log.w("DEMO","OK....");
            Bitmap bp = (Bitmap) extras.get("data");

            if(bp==null)
            {
                Log.w("DEMO","null");
            }
            else
            {
                try{

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bp.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    byte[] arry =   bos.toByteArray();
                    String aryString = Base64.encodeToString(arry,0,arry.length,Base64.DEFAULT);
                    new SelfieSender(getApplicationContext()).execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()),aryString);
                }catch (Exception e){}
            }

        }
    }
}
