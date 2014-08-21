import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import dev.database.QuizzyDatabase;
import dev.networking.ScoreSender;
import dev.support.Score;



/**
 * Bundle values required
 *
 *
 * 1. Score of first player
 *
 * 2. Score of second player
 *
 * 3. Name of first player
 *
 * 4. Name of second player
 *
 * 5. game_code
 *
 * 6. user_code
 *
 * 7. p1_title
 *
 * 8. p2_title
 *
 * 9. CATAGORY
 *
 * 10.CHAP_ID
 */


public class ScoreActivity extends Activity {

    private GridView gv;
    private static TextView text;
    private ProgressBar bar;
    private int count,player_1_score,player_2_score,LEVEL_PROGRESS;
    private Handler handler;
    private TextView p1_score,p2_score,level_text,actionBarText,back;
    private FinalGraph finalGraph;
    private int[] firstPlayerScore,secondPlayerScore;
    private String p1_name,p2_name,p1_title,p2_title,CHAP_ID,CATAGORY;
    private DisplayMetrics MAT;
    private ActionBar actionBar;
    private Typeface font;
    private QuizzyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoring_dynamic_view);

        database = new QuizzyDatabase(getApplicationContext(),"QUIZY",null,1);

        actionBar = getActionBar();

        font = Typeface.createFromAsset(getAssets(),"font.ttf");
        initActionBar();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            ArrayList<Score> data1 = b.getParcelableArrayList("P1_SCORE");
            ArrayList<Score> data2 = b.getParcelableArrayList("P2_SCORE");

            p1_name = b.getString("P1_NAME");
            p2_name = b.getString("P2_NAME");


            firstPlayerScore = new int[10];
            secondPlayerScore = new int[10];

            if((data1 != null) && (data1.size() == 10))
            {
                for (int i = 0; i < data1.size(); i++) {
                    Score c = (Score) data1.get(i);
                    firstPlayerScore[i] = Integer.valueOf(c.getScore());
                }
            }


            if( (data2 != null) && (data2.size() == 10))
            {
                for (int i = 0; i < data2.size(); i++) {
                    Score c = (Score) data2.get(i);
                    secondPlayerScore[i] = Integer.valueOf(c.getScore());
                }
            }


            if(b.getString("P1_TITLE") != null)
            {
                p1_title = b.getString("P1_TITLE");
            }

            if(b.getString("P2_TITLE") != null)
            {
                p2_title = b.getString("P2_TITLE");
            }

            //calling service to save data on server
            //000007343 --> user_code
            //GAM000000014 --> game_code
            //get score string using method getScoreString(ArrayList<Score> list)

            //new ScoreSender(getApplicationContext()).execute("000007343", "GAM000000014", getScoreString(data1));

            ((ImageView)findViewById(R.id.image_p1)).setImageBitmap(imageCircleClip(BitmapFactory.decodeResource(getResources(), R.drawable.sunny),"#db2121"));
            ((ImageView)findViewById(R.id.image_p2)).setImageBitmap(imageCircleClip(BitmapFactory.decodeResource(getResources(), R.drawable.sunny),"#01e04a"));

            if((b.getString("USER_CODE")!=null) && (b.getString("GAME_CODE")!=null) && (data1 != null))
            {
                new ScoreSender(getApplicationContext()).execute(b.getString("USER_CODE"), b.getString("GAME_CODE"), getScoreString(data1));
            }

            if( (b.getString("CATAGORY") != null) && (b.getString("CHAP_ID")!=null) )
            {
                CATAGORY = b.getString("CATAGORY");
                CHAP_ID = b.getString("CHAP_ID");
            }

            init();

            handler = new Handler();
            handler.post(new ProgressController());
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
        actionBarText = (TextView)mCustomView.findViewById(R.id.nameOfScreen);
        actionBarText.setText("Scores");
        actionBarText.setTypeface(font);


        back = (TextView)mCustomView.findViewById(R.id.back);//
        back.setText("Home");
        back.setTypeface(font);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Log.w("DEMO", "back to home");


            }
        });

        actionBar.setDisplayShowCustomEnabled(true);
    }


    private void databaseOperations(int CORRECT_QUES,int player_1_score)
    {
        try
        {
            database.updateFullScore(CATAGORY,CHAP_ID,String.valueOf(Integer.valueOf(database.getGlobalRank(CATAGORY,CHAP_ID))),String.valueOf(player_1_score+Integer.valueOf(database.getTotalScore(CATAGORY,CHAP_ID))),String.valueOf(Integer.valueOf(database.getTotalCorrect(CATAGORY,CHAP_ID))+CORRECT_QUES),String.valueOf(1+Integer.valueOf(database.getTotalCount(CATAGORY,CHAP_ID))));
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

                if(CORRECT_QUES > 0)
                {
                    CORRECT_QUES++;
                }

                player_1_score = player_1_score + aFirstPlayerScore;

            }

        //performs the database updations in local database
        databaseOperations(CORRECT_QUES,player_1_score);

        LEVEL_PROGRESS = Integer.valueOf(database.getTotalScore(CATAGORY,CHAP_ID));

        MAT = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(MAT);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.base_view);

        bar = (ProgressBar)findViewById(R.id.level_progress_bar);
        bar.setMax(2000);

        View v11= (View)findViewById(R.id.first_player);
        View v21= (View)findViewById(R.id.second_player);

        p1_score = (TextView)v11.findViewById(R.id.p_score);
        p2_score = (TextView)v21.findViewById(R.id.p_score);

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

        finalGraph = new FinalGraph(getApplicationContext(),firstPlayerScore,secondPlayerScore);
        finalGraph.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(MAT.heightPixels*40)/100));


        View v = getLayoutInflater().inflate(R.layout.scoregrid, null);

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

        gv = (GridView)v.findViewById(R.id.grid);
        gv.setAdapter(new CustomBaseAdapter());
        gv.setPadding((MAT.widthPixels*5)/100, 0, (MAT.widthPixels*5)/100,0);
        gv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (MAT.heightPixels*18)/100));

        linearLayout.addView(finalGraph);
        linearLayout.addView(v1);
        linearLayout.addView(gv);
        linearLayout.addView(v2);

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
                if(count  <= player_2_score)
                {
                    p2_score.setText(String.valueOf(count));
                }
                handler.postDelayed(this,2);
            }
            else
            {
                c++;
                if(!textAnimationComplete)
                {
                    textAnimationComplete = true;
                    handler.post(this);

                }
                else {
                    if(c < LEVEL_PROGRESS)
                    {
                        bar.setProgress(c);
                        handler.postDelayed(this,10);
                    }
                    if(c == LEVEL_PROGRESS)
                    {
                        ObjectAnimator.ofFloat(level_text,"alpha",0,1).setDuration(500).start();
                        level_text.setText("Level "+database.getTotalScore(CATAGORY,CHAP_ID));
                    }
                }

            }

        }
    }

    private int getLevelOfUser(int TOTAL_SCORE)
    {
        int count = 1;
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

    private Bitmap imageCircleClip(Bitmap sourceBitmap,String colorcode){
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

        canvas.drawCircle(50, 50, 50, pp);

        return outputBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        database.close();
    }
}
