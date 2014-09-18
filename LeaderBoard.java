package tronbox.welcome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import tronbox.controller.QuizzyDatabase;
import tronbox.social.R;

public class LeaderBoard extends Activity {

    private ListView listView;
    private ArrayList<LeaderBoardScore> stringArrayList;
    private LeaderBoardAdapter leaderBoardAdapter;
    private static LeaderBoardScore score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        initActionBar(getActionBar());

        init();


    }

    private void initActionBar(ActionBar actionBar)
    {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar_score_activity, null);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mCustomView.setLayoutParams(params);
        actionBar.setCustomView(mCustomView);
        TextView actionBarText = (TextView)mCustomView.findViewById(R.id.nameOfScreen);
        actionBarText.setText("Leaders");

        TextView back = (TextView)mCustomView.findViewById(R.id.back);
        back.setText("Home");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MasterHomeScreen.class));
                finish();
            }
        });
        ((Button)mCustomView.findViewById(R.id.selfie)).setVisibility(View.INVISIBLE);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void init()
    {
        listView = (ListView)findViewById(R.id.leaderBoardList);
        stringArrayList  = new ArrayList<LeaderBoardScore>();

        final QuizzyDatabase database = new QuizzyDatabase(getApplicationContext(),"QUIZZY",null,1);


            if(getIntent().getExtras().getBoolean("USER"))
            {
                LeaderBoardScore[] scores = new LeaderBoardScore[1];
                scores[0] = new LeaderBoardScore(SharedPrefrenceStorage.getUserName(getApplicationContext()), SharedPrefrenceStorage.getUserFacebookId(getApplicationContext()),Integer.valueOf(database.getTotalScore(QuizzyApplication.ChapterCode)));
                stringArrayList.addAll(Arrays.asList(scores));

            }
            else
            {
                Cursor c = database.getUserCodesFriends();

                LeaderBoardScore[] scores = new LeaderBoardScore[c.getCount()+1];
                scores[0] = new LeaderBoardScore(SharedPrefrenceStorage.getUserName(getApplicationContext()), SharedPrefrenceStorage.getUserFacebookId(getApplicationContext()),Integer.valueOf(database.getTotalScore(QuizzyApplication.ChapterCode)));
                int count = 1;
                if(c.moveToFirst())
                {
                    do
                    {
                        scores[count] = new LeaderBoardScore(database.getNamedWithFbID(database.getFBIdWithUserCode(c.getString(0))),database.getFBIdWithUserCode(c.getString(0)),Integer.valueOf(database.getTotalScoreFriendsScoreData(c.getString(0),QuizzyApplication.ChapterCode)));
                        count++;
                    }while (c.moveToNext());
                }
                c.close();

                Arrays.sort(scores);

                stringArrayList.addAll(Arrays.asList(scores));


        }

        leaderBoardAdapter = new LeaderBoardAdapter(getApplicationContext(),stringArrayList);
        listView.setAdapter(leaderBoardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),FinalProfileActivity.class);

                LeaderBoardScore score1 = stringArrayList.get(i);
                intent.putExtra("FRIEND_PROFILE","");
                intent.putExtra("username",score1.name);
                intent.putExtra("fid",score1.FB_ID);
                Cursor c = database.getFullRecordScoreFriendsScoreData(database.getUserCode(score1.FB_ID),QuizzyApplication.ChapterCode);
                c.moveToFirst();
                intent.putExtra("status", c.getString(c.getColumnIndex("TAG")));
                intent.putExtra("user_score", c.getString(c.getColumnIndex("TOTAL_SCORE")));
                intent.putExtra("k_time", c.getString(c.getColumnIndex("KILL_TIME")));
                intent.putExtra("user_win", c.getString(c.getColumnIndex("WIN")));
                intent.putExtra("user_loose", c.getString(c.getColumnIndex("LOOSE")));
                intent.putExtra("user_donate", c.getString(c.getColumnIndex("GLOBAL_RANK")));
                c.close();
                startActivity(intent);
                finish();
            }
        });
        database.close();
    }

    class LeaderBoardAdapter extends BaseAdapter
     {
        private ArrayList<LeaderBoardScore> data;
        private LayoutInflater layoutInflater;

        LeaderBoardAdapter(Context context,ArrayList<LeaderBoardScore> data)
        {
            this.data = data;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View v1 = layoutInflater.inflate(R.layout.leader_board_item,null);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       View v = layoutInflater.inflate(R.layout.leader_board_item,null) ;

            ViewHolder.leaderImage = (ImageView)v1.findViewById(R.id.leader_image);
            ViewHolder.leaderName = (TextView)v1.findViewById(R.id.leader_name);
            ViewHolder.leaderScore = (TextView)v1.findViewById(R.id.leader_score);

            score = data.get(i);

            try
            {
                ViewHolder.leaderImage.setImageBitmap(BitmapFactory.decodeStream(openFileInput(score.FB_ID + ".png")));
            }catch (IOException e){

            }catch (Exception e){

            }
            ViewHolder.leaderName.setText(score.name);
            ViewHolder.leaderScore.setText(String.valueOf(score.score));

            return v1;
        }
    }

    static class ViewHolder
    {
        static ImageView leaderImage;
        static TextView leaderName,leaderScore;
    }

    class LeaderBoardScore implements  Comparable<LeaderBoardScore>
    {
        int score;
        String name;
        String FB_ID;

        LeaderBoardScore(String name, String FB_ID, int score)
        {
            this.name = name;
            this.FB_ID = FB_ID;
            this.score = score;
       }
        @Override
        public int compareTo(LeaderBoardScore leaderBoardScore) {
            int compareQuantity = ((LeaderBoardScore) leaderBoardScore).score;
            return compareQuantity - this.score;        //descending order
        }
    }
}
