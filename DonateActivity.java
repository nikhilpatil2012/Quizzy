package tronbox.welcome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


import tronbox.social.DonationSender;
import tronbox.social.R;

public class DonateActivity extends Activity {

    private ListView donaListView;
    private MyListAdapter myListAdapter;
    private ArrayList<MyDonateQuestion> listdata;
    private final int MAX_QUESTIONS = 5;
    private Button b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donate);

        init();

        initActionBar(getActionBar());

       // new InsertUserScore(getApplicationContext()).execute("000007520","ATI0000686","ATC0002521","100","2","0","2.0","Newbee");\
       // new GetScoreWithUserCode(getApplicationContext()).execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()),"ATI0000686","ATC0002521");

    }

    private void init()
    {
        donaListView = (ListView)findViewById(R.id.donationList);
        listdata = new ArrayList<MyDonateQuestion>();
        myListAdapter = new MyListAdapter(getApplicationContext(),listdata);
        donaListView.setAdapter(myListAdapter);
        Collections.reverse(listdata);
        myListAdapter.notifyDataSetChanged();

        final DonateActivity activity= this;

        b = (Button)findViewById(R.id.send_donation);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(listdata);

                //calling donation sender service

                if (prepareDataString(listdata).length() > 0) {
                    new DonationSender(getApplicationContext(), activity).execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()), prepareDataString(listdata));
                } else {
                    Toast.makeText(getApplicationContext(),"data not valid",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String prepareDataString(ArrayList<MyDonateQuestion> data)
    {
       StringBuilder stringBuilder = new StringBuilder();

        MyDonateQuestion question = null;
        for(int i=0;i<data.size();i++)
        {
            question = data.get(i);

            if(i==0)
            {
                if(!(question.question.equals("Q :")&&question.answer.equals("A :")))
                {
                    stringBuilder.append(question.question.substring(3)+"~"+question.answer.substring(3));
                }
            }
           else
           {
               if(!(question.question.equals("Q :")&&question.answer.equals("A :")))
               {
                   stringBuilder.append("`"+question.question.substring(3)+"~"+question.answer.substring(3));
               }
           }
        }
        return stringBuilder.toString();
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
        actionBarText.setText("Donate");

        TextView back = (TextView)mCustomView.findViewById(R.id.back);
        back.setText("Home");

        ImageButton add = (ImageButton)mCustomView.findViewById(R.id.add_ques);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listdata.size() < MAX_QUESTIONS)
                {
                    if(listdata.size() > 0)
                    {
                        if(listdata.get(0).question.equals("Q :") && listdata.get(0).answer.equals("A :"))
                        {
                            Toast.makeText(getApplicationContext(),"Complete previous entry",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Collections.reverse(listdata);
                            listdata.add(new MyDonateQuestion("Q :", "A :"));
                            Collections.reverse(listdata);
                            myListAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Collections.reverse(listdata);
                        listdata.add(new MyDonateQuestion("Q :", "A :"));
                        Collections.reverse(listdata);
                        myListAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Over limit entry",Toast.LENGTH_LONG).show();
                }
            }
        });

        add.setVisibility(View.VISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MasterHomeScreen.class));
                finish();
            }
        });

        actionBar.setDisplayShowCustomEnabled(true);
    }

    class MyListAdapter extends BaseAdapter
    {
        private LayoutInflater layoutInflater;
        private Context context;
        private ArrayList<MyDonateQuestion> listdata;

        MyListAdapter(Context context,ArrayList<MyDonateQuestion> listdata)
        {
            this.context = context;
            this.listdata = listdata;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Object getItem(int i) {
            return listdata.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View v = view;

            v = layoutInflater.inflate(R.layout.donate_items,null);

            final MyDonateQuestion question = listdata.get(i);

            final EditText q = (EditText)v.findViewById(R.id.question_donate);
            final EditText a = (EditText)v.findViewById(R.id.answer_donate);

            final TextView q_c = (TextView)v.findViewById(R.id.count_left_ques);
            final TextView a_c = (TextView)v.findViewById(R.id.count_left_answer);

            q.setText(question.question);
            a.setText(question.answer);


            Selection.setSelection(q.getText(), q.getText().length());
            Selection.setSelection(a.getText(), a.getText().length());


            q.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    question.setQuestion(q.getText().toString());
                    //Log.w("DONATE","start : "+i+" before : "+i2+" count : "+i3);
                    if(i>4)
                    {
                        q_c.setText(String.valueOf((52-i)));
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().contains("Q :")){
                        q.setText("Q :");
                        Selection.setSelection(q.getText(), q.getText().length());
                    }
                }
            });
            a.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    question.setAnswer(a.getText().toString());
                    Log.w("DONATE","start : "+i+" before : "+i2+" count : "+i3);
                    if(i>4)
                    {
                        a_c.setText(String.valueOf((27-i)));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().contains("A :")) {
                        a.setText("A :");
                        Selection.setSelection(a.getText(), a.getText().length());
                    }
                }
            });



            if(i==0)
            {
                   q.setFocusable(true);
            }
            return v;
        }

    }

    class MyDonateQuestion
    {
        String question;
        String answer;

        MyDonateQuestion(String question,String answer)
        {
            this.question = question;
            this.answer = answer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

}
