import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Typeface;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;


public class LoadingScreen extends Activity implements LoadingScreenConstants {

    private WebView web;
    private TextView centerText,msgLoading,footermsg;
    private Button startGame;
    private Typeface font;
    private Bundle bundle = new Bundle();



    private BroadcastReceiver loadindReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        actionOnMessage(intent.getExtras());
     }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        getActionBar().hide();

        Bundle data = new Bundle();
        data = getIntent().getExtras();



        if(data!= null)
        {
            init();

            registerReceiver(loadindReceiver,new IntentFilter(LOADING_SCREEN_FILTER));

            Bundle data1 = data.getBundle(STARTING_BUNDLE_STRING);
            Set<String> set = data1.keySet();
            Iterator<String> it = set.iterator();
            String key = null;
            while (it.hasNext())
            {
                key = (String)it.next();
                sendBroadcast(new Intent(LOADING_SCREEN_FILTER).putExtra(key,data1.getString(key)));
            }
        }

    }

    private void actionOnMessage(Bundle bundle)
    {
        if(bundle.containsKey(MSG_LOADING_TEXT))
        {
            msgLoading.setText(bundle.getString(MSG_LOADING_TEXT));
        }
        if(bundle.containsKey(MSG_CENTER_TEXT))
        {
            centerText.setText(bundle.getString(MSG_CENTER_TEXT));
        }
        if(bundle.containsKey(MSG_FOOTER_TEXT))
        {
            footermsg.setText(bundle.getString(MSG_FOOTER_TEXT));
        }
        if(bundle.containsKey(MSG_INVISIBLE_LOADING_TEXT))
        {
            msgLoading.setVisibility(View.INVISIBLE);
        }
        if(bundle.containsKey(MSG_INVISIBLE_CENTER_TEXT))
        {
            centerText.setVisibility(View.INVISIBLE);
        }
        if(bundle.containsKey(MSG_INVISIBLE_FOOTER_TEXT))
        {
            footermsg.setVisibility(View.INVISIBLE);
        }
        if(bundle.containsKey(MSG_INVISIBLE_GBUTTON))
        {
            startGame.setVisibility(View.INVISIBLE);
        }
        if(bundle.containsKey(MSG_VISIBLE_LOADING_TEXT))
        {
            msgLoading.setVisibility(View.VISIBLE);
        }
        if(bundle.containsKey(MSG_VISIBLE_CENTER_TEXT))
        {
            centerText.setVisibility(View.VISIBLE);
        }
        if(bundle.containsKey(MSG_VISIBLE_FOOTER_TEXT))
        {
            footermsg.setVisibility(View.VISIBLE);
        }
        if(bundle.containsKey(MSG_VISIBLE_GBUTTON))
        {
            startGame.setVisibility(View.VISIBLE);
        }
    }

    private void init()
    {
        font = Typeface.createFromAsset(getAssets(),"font.ttf");

        centerText = (TextView)findViewById(R.id.centertext);
        msgLoading = (TextView)findViewById(R.id.msgloading);
        footermsg = (TextView)findViewById(R.id.footermsg);
        startGame = (Button)findViewById(R.id.game);

        centerText.setTypeface(font);
        msgLoading.setTypeface(font);
        footermsg.setTypeface(font);
        startGame.setTypeface(font);

        web = (WebView)findViewById(R.id.webView);
        web.loadUrl("file:///android_asset/abc.html");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loadindReceiver);
    }
}
