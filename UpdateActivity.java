package tronbox.welcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tronbox.Logs.ErrorLogs;
import tronbox.social.AppIdGetterId;
import tronbox.social.LoginFragment;
import tronbox.social.R;

public class UpdateActivity extends FragmentActivity {

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getActionBar().hide();

        initBroadcastReceiver();


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {


            if (SharedPrefrenceStorage.check(this)) {

                ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "UPDATEACTIVITY_LOGIN");

                new GetUpdatedBaseData(this).execute(SharedPrefrenceStorage.getUserCode(getApplicationContext()));


            } else {

                new AppIdGetterId(getApplicationContext()).execute();

                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new LoginFragment()).commit();

                ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "LOGIN");


            }

        } else {

            makeDialog().show();

        }

    }

    private void initBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startActivity(new Intent(getApplicationContext(), MasterHomeScreen.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter("DOWNLOAD_OVER"));
    }

    public void callHome(){
        ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "CALL_HOME");

        Intent intent = new Intent(getApplicationContext(), MasterHomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
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
}
