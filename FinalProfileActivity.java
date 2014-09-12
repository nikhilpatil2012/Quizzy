package tronbox.welcome;

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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import tronbox.social.R;
import tronbox.social.SelfieSender;

public class FinalProfileActivity extends Activity {

    private ImageView proImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_final_profile);

        Bundle bundle = getIntent().getExtras();

        if(bundle.containsKey("MAIN_PROFILE"))
        {
            try {

                proImage = (ImageView)findViewById(R.id.profile_image_user);
                proImage.setImageBitmap(imageCircleClip(BitmapFactory.decodeStream(openFileInput(SharedPrefrenceStorage.getUserFacebookId(getApplicationContext()) + ".png")), "#ffffff"));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(bundle.getString("location").equals("null"))
            {
                ((TextView)findViewById(R.id.profile_user_details)).setText(Html.fromHtml("<html><b>"+bundle.getString("username")+"</b><br><font color='#efc500'>"+bundle.getString("status")+"</font><br>G Rank : "+bundle.getString("rank")+"</html>"));
            }
            else
            {
                ((TextView)findViewById(R.id.profile_user_details)).setText(Html.fromHtml("<html><b>"+bundle.getString("username")+"</b><br><font color='#efc500'>"+bundle.getString("status")+"</font> from "+bundle.getString("location")+"<br>G Rank : "+bundle.getString("rank")+"</html>"));
            }

            if(Float.valueOf(bundle.getString("k_time")) > 0)
            {
                ((TextView)findViewById(R.id.kill_time_detail)).setText(bundle.getString("k_time")+" sec");
            }
            else
            {
                ((TextView)findViewById(R.id.kill_time_detail)).setText("0.0 sec");
            }

            ((TextView)findViewById(R.id.profile_score)).setText(bundle.getString("user_score"));
            ((TextView)findViewById(R.id.profile_win)).setText(bundle.getString("user_win"));
            ((TextView)findViewById(R.id.profile_loose)).setText(bundle.getString("user_loose"));
            ((TextView)findViewById(R.id.profile_donate)).setText(bundle.getString("user_donate"));
        }

        initActionBar(getActionBar());
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
        actionBarText.setText("Profile");

        TextView back = (TextView)mCustomView.findViewById(R.id.back);
        back.setText("Home");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w("DEMO", "back to home");
                startActivity(new Intent(getApplicationContext(),MasterHomeScreen.class));
                finish();

            }
        });

        actionBar.setDisplayShowCustomEnabled(true);
    }


    private Bitmap imageCircleClip(Bitmap sourceBitmap,String colorcode)
    {
        Bitmap outputBitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        path.addCircle(125, 125, 125, Path.Direction.CCW);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        Rect src = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Rect out = new Rect(0, 0, 250, 250);
        Bitmap source = sourceBitmap;
        canvas.drawBitmap(source, src, out, null);

        Paint pp = new Paint(Paint.ANTI_ALIAS_FLAG);
        pp.setStrokeWidth(8);
        pp.setStyle(Paint.Style.STROKE);
        pp.setColor(Color.parseColor(colorcode));

        //FF4500
        canvas.drawCircle(125, 125, 125, pp);

        return outputBitmap;
    }
}
