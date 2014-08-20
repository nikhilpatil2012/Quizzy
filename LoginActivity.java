package tronbox.social;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tronbox.arena.LoadingScreen;
import tronbox.social.R;
import tronbox.welcome.MasterHomeScreen;
import tronbox.welcome.QuizzyApplication;
import tronbox.welcome.SendUserCredentials;
import tronbox.welcome.UpdateActivity;


public class LoginActivity extends FragmentActivity {


    private ActionBar bar;
    public TextView actionBarText;
    public Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        bar = getActionBar();
        initActionBar();


        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new LoginFragment()).commit();

    }


    private void initActionBar()
    {
        bar.setTitle("Login");
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mCustomView.setLayoutParams(params);
        bar.setCustomView(mCustomView);

        actionBarText = (TextView)mCustomView.findViewById(R.id.nameOfScreen);

        actionBarText.setText("Login");

        back = (Button)mCustomView.findViewById(R.id.back);//
        back.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.e("DEMO", "clicked");
                //removeCurrentFragment();

                actionBarText.setText("Login");
                //isSignUpEnabled = false;
                back.setVisibility(View.INVISIBLE);
                */
            }
        });
        bar.setDisplayShowCustomEnabled(true);
    }

    public void callHome(){


 new SendUserCredentials(getApplicationContext()).execute(QuizzyApplication.userFirstName,"","", QuizzyApplication.birthDay, QuizzyApplication.birthMonth, QuizzyApplication.birthYear, QuizzyApplication.userGender, QuizzyApplication.userMobile, "India", "Delhi", QuizzyApplication.userRegId, "FB", QuizzyApplication.userFacebookId);

//new SendUserCredentials(getActivity()).execute(firstName,"",lastName, birthDay, birthMonth, birthYear, gender, "38393939393", "", "", userRegId, "mb", id);



/*
        if(QuizzyApplication.DownloadOver == true){

            QuizzyApplication.DownloadOver = false;

            Intent intent = new Intent(getApplicationContext(), MasterHomeScreen.class);
            startActivity(intent);

        }else{

            Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
            startActivity(intent);

        }*/
    }
}
