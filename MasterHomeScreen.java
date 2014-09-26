package ideapot.welcome;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import ideapot.Logs.LogsActivity;
import ideapot.Sounds.AndroidAudio;
import ideapot.Sounds.Assets;
import ideapot.Sounds.Audio;
import ideapot.controller.QuizzyDatabase;
import ideapot.quizzy.DownloadFacebookData;
import ideapot.quizzy.QuizzeeFriendsZone;
import ideapot.quizzy.R;
import ideapot.quizzy.UpdateActivity;


public class MasterHomeScreen extends FragmentActivity implements View.OnTouchListener, ViewPager.OnPageChangeListener{

    private LinearLayout masterView, topBarView, midBarView, bottomBarView, sideDrawer, sideDrawerHome, sideDrawerFriend, sideDrawerProfile,sideDrawerDoantion, sideDrawerLogs;
    private LinearLayout.LayoutParams topBarParams, midBarParams, bottomBarParams;


    private Button trending,challenge,popular,fresh,all;
    private View trendingBottom, challengeBottom,popularBottom,freshBottom,allBottom;
    private int presssedButtonId = 0;

    private ViewPager pager;
    private ScreenSlidePagerAdapter adapter;

    private Point point;

    private ImageButton drawerButton;
    private ImageView profilePic;

    private boolean toggle = false;

    private QuizzyDatabase database;
    Audio audio;

    private int topBarHeight;

    private TextView Name, homeText, friendsText, profileText, donateText;

    private int currentItem = 0;
    private HorizontalScrollView scrollView;
    private Typeface font, titleFont;
    private TextView homeTitle;
    private float verticalButtonHeight = 0;
    private Fragment friendsZone;

    private LinearLayout customTabs;

    private ArrayList<Button> buttonsList = new ArrayList<Button>();
    private ArrayList<View> buttonsBottomList = new ArrayList<View>();

    private HashSet<String> set;
    private HashSet<String> chapterSet;

    private ArrayList<String> list;
    private ArrayList<String> chapterList;

    private Cursor categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_master_screen);

        getActionBar().hide();

        database = new QuizzyDatabase(getApplicationContext(), "QUIZZY", null, 1);

        categories = database.getListOfChapters();

        list = new ArrayList<String>();
        chapterList = new ArrayList<String>();

        set = new HashSet<String>();
        chapterSet = new HashSet<String>();

        int count = 1;

        if(categories.moveToFirst()){

           // do{

                    String name = categories.getString(1);

                    if(name.equals("INCREDIBLE INDIA")){

                        name = "India";
                    }

                    if(!list.contains(name)){
                        list.add(name);
                    }

                    String id = categories.getString(0);

                    if(!id.equals("ATC0002543")){

                        if(!chapterList.contains(id)){
                            chapterList.add(id);
                        }
                    }

           //} while(categories.moveToNext());

        }

        database.insertLogs("Home Screen is Loaded");

        init();

    }

    public void init(){

        font = Typeface.createFromAsset(getAssets(), "font.ttf");
        titleFont = Typeface.createFromAsset(getAssets(), "tittle.ttf");

        Bundle profileInfo = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());

        audio = new AndroidAudio(this);
        QuizzyApplication.gameSound = audio.newMusic("playback.ogg");
        QuizzyApplication.gameSound.setLooping(true);

        if(QuizzyApplication.backGroundSound == true){

            QuizzyApplication.gameSound.play();

        }

        QuizzyApplication.Mode = "Not_Playing";

///**** 	Extract the Screen Width and Screen Height and pass on to Application class for other activities ****///

        point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        QuizzyApplication.init(point.x, point.y);


        topBarHeight = (int)(0.10)*point.y;

        Log.w("TopBarheight", "" + (int) (0.10) * point.y);

//**************************************************************************//

//***** Master View *******//

        masterView = (LinearLayout)findViewById(R.id.master_home_screen);

//**************************

//**** Top Bar View --[Customize the height and width of the Top Bar]-- *********//

        topBarView = (LinearLayout)findViewById(R.id.home_top_bar);
        topBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
        topBarView.setLayoutParams(topBarParams);
        topBarView.setOnTouchListener(this);

        homeTitle = (TextView)findViewById(R.id.home_title);
        homeTitle.setTypeface(titleFont);

        scrollView = (HorizontalScrollView)findViewById(R.id.top_scroller);


//*********** Custom Work Going Her ***********

        customTabs = (LinearLayout)scrollView.findViewById(R.id.horizontal_top_bar);
        LinearLayout.LayoutParams customTabPramss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(list.size() == 1){

            homeTitle.setText("India");

        }else {

            for(int i=0; i<list.size(); i++){

                LinearLayout customlayout = (LinearLayout)getLayoutInflater().inflate(R.layout.custom_tab_layout, null, false);

                Button button = (Button)customlayout.findViewById(R.id.trending);
                button.setId(i);
                buttonsList.add(button);
                button.setOnClickListener(listener);
                button.setTypeface(font);
                button.setText(list.get(i));

                View buttonBottom = customlayout.findViewById(R.id.trending_bottom);
                buttonBottom.setId(i+10);
                buttonsBottomList.add(buttonBottom);

                if(i == list.size()-1){

                    ((View)customlayout.findViewById(R.id.white_line)).setVisibility(View.GONE);
                }

                customTabs.addView(customlayout, customTabPramss);

            }

            presssedButtonId = buttonsBottomList.get(0).getId();
            buttonsBottomList.get(0).setVisibility(View.VISIBLE);
        }



//************************************************


//**** Mid Bar View --[Customize the height and width of the Scroll Bar]-- *********//


        midBarView = (LinearLayout)findViewById(R.id.home_mid_bar);
        midBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        midBarParams.setMargins(0, 0, 0, 0);


//**** View Pager for providing sliding fragments *****///

        //pager = (ViewPager) findViewById(R.id.pager);
        //adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        //pager.setAdapter(adapter);
        // pager.setOnPageChangeListener(this);

//-------------------------------------------------------------------//
//------------------ Initializing the Top Bar ----------------------//
//-----------------------------------------------------------------//

        drawerButton = (ImageButton)topBarView.findViewById(R.id.drawer_button);
        drawerButton.setOnClickListener(listener);

//----------------------------------------------------------------//
//----------------------------------------------------------------//


//-------------------------------------------------------------------//
//------------------ Initializing the Scroll Bar ----------------------//
//-----------------------------------------------------------------//

//----------------------------------------------------------------//
//----------------------------------------------------------------//

//-------------------------------------------------------------------//
//------------------ Initializing the Side Drawer ----------------------//
//-----------------------------------------------------------------//


        sideDrawer = (LinearLayout)findViewById(R.id.side_drawer);
        sideDrawer.setVisibility(View.VISIBLE);

        profilePic = (ImageView)findViewById(R.id.drawer_profile_pic);
        profilePic.setImageBitmap(imageCircleClip(getImage(profileInfo.getString("FacebookId")+".png")));

        sideDrawerHome = (LinearLayout)findViewById(R.id.drawer_home_button);
        sideDrawerHome.setOnTouchListener(this);

        sideDrawerFriend = (LinearLayout)findViewById(R.id.drawer_friends_button);
        sideDrawerFriend.setOnTouchListener(this);

        sideDrawerProfile = (LinearLayout)findViewById(R.id.drawer_Profile_button);
        sideDrawerProfile.setOnTouchListener(this);


        sideDrawerDoantion = (LinearLayout)findViewById(R.id.drawer_Donate_button);
        sideDrawerDoantion.setOnTouchListener(this);


        sideDrawerLogs = (LinearLayout)findViewById(R.id.drawer_Logs_button);
        sideDrawerLogs.setOnTouchListener(this);

        //sideDrawerDoantion

        Name = (TextView)findViewById(R.id.drawer_profile_name);
        Name.setText(profileInfo.getString("Name")+" ");

        homeText = (TextView)findViewById(R.id.home_text);
        friendsText = (TextView)findViewById(R.id.friends_text);
        profileText = (TextView)findViewById(R.id.profile_text);
        donateText = (TextView)findViewById(R.id.donate_text);

//----------------------------------------------------------------//
//----------------------------------------------------------------//

//        Log.w("Pager Size", ""+pager.getChildCount());


        loadAssets();

        // checking bundle

        Bundle checkBundle = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());

        Log.w("User Details", ""+checkBundle.getString("Name")+"\n"+checkBundle.getString("FacebookId")+"\n"+checkBundle.getString("Location")+"\n"+checkBundle.getString("Dob")+"\n"+checkBundle.getString("Gender")+"\nUserCode = "+SharedPrefrenceStorage.getUserCode(getApplicationContext()));

    }


    public void loadAssets(){

        Assets.gong = audio.newSound("gong.ogg");
        Assets.wall = audio.newSound("wall.ogg");
        Assets.whistle = audio.newSound("whistle.ogg");
        Assets.loose = audio.newSound("loose.ogg");

    }


    public Bitmap getImage(String name){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(openFileInput(name));
        } catch (FileNotFoundException e) {

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.james);
        }


        return bitmap;
    }


    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            switch (v.getId()) {


                case R.id.drawer_button:{


                    drawerAnimation();


                }break;


            }

            for(int i=0; i<buttonsList.size(); i++){

                if(v.getId() == buttonsList.get(i).getId()){

                    closeElse(presssedButtonId);
                    buttonsBottomList.get(i).setVisibility(View.VISIBLE);
                    presssedButtonId = buttonsBottomList.get(i).getId();
                    pager.setCurrentItem(i);
                }
            }


        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        QuizzyApplication.ScreenNumber = 100;

        if(pager == null){


            pager = (ViewPager) findViewById(R.id.pager);
            adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(this);
            pager.setCurrentItem(SharedPrefrenceStorage.getCurrentState(getApplicationContext()));
            //adapter.changeCount();
        }

        QuizzyApplication.list_Question.clear();
        QuizzyApplication.imageQuestionIdPool.clear();
        QuizzyApplication.imageIdsPool.clear();
        QuizzyApplication.challengerList.clear();
        QuizzyApplication.userScoreList.clear();
        QuizzyApplication.scoreList.clear();


    }

    public void closeElse(int id){

        for(View v: buttonsBottomList){

            if(v.getId() == id){

                v.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void drawerAnimation(){

        toggle = !toggle;


        AnimatorSet drawerAnimation = new AnimatorSet();

        if(toggle){

            drawerAnimation.playTogether(ObjectAnimator.ofFloat(masterView, View.X, 0, -QuizzyApplication.homeScreenAnimationX),ObjectAnimator.ofFloat(sideDrawer, View.X, QuizzyApplication.screenWidth-20, QuizzyApplication.sideDrawerAnimationX-20));

        }else if(!toggle){

            drawerAnimation.playTogether(ObjectAnimator.ofFloat(masterView, View.X, -QuizzyApplication.homeScreenAnimationX, 0),ObjectAnimator.ofFloat(sideDrawer, View.X, QuizzyApplication.sideDrawerAnimationX-20, QuizzyApplication.screenWidth));

        }

        QuizzyApplication.isDrawerOpen = toggle;
        drawerAnimation.setDuration(500);
        drawerAnimation.start();



    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if(list.size() > 2){

            closeElse(presssedButtonId);
            buttonsBottomList.get(position).setVisibility(View.VISIBLE);
            presssedButtonId = buttonsBottomList.get(position).getId();

            if(position == 0){

                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);

            }else if(position == buttonsBottomList.size()-1){

                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }

        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

        Log.w("PageStateChanged", "Changed");

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        //private ArrayList<String> list, names;
        private int count = 1;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @Override
        public Fragment getItem(int id) {

            TrendingScreen trendingScreen = new TrendingScreen();

            Bundle bundle = new Bundle();
            bundle.putString("value", chapterList.get(id));

            Log.w("StartList", list.get(id));

            trendingScreen.setArguments(bundle);

            return trendingScreen;
        }

        @Override
        public int getCount() {

            return list.size();
        }

    }
    public static Bitmap imageCircleClip(Bitmap sourceBitmap){

        int targetWidth = 100;
        int targetheight = 100;


        Bitmap outputBitmap = Bitmap.createBitmap(targetWidth, targetheight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#FF3300"));


        Path path = new Path();
        path.addCircle(targetWidth/2, targetheight/2, targetWidth/2, Path.Direction.CCW);

        Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);

        Rect srcone = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Rect out = new Rect(0, 0, targetWidth, targetheight);

        Bitmap source = sourceBitmap;

        canvas.drawCircle(out.centerX(), out.centerY(), targetWidth/2, paint);
        canvas.drawBitmap(source, srcone, out, paint);

        Paint pp = new Paint(Paint.ANTI_ALIAS_FLAG);
        pp.setStrokeWidth(13);
        pp.setStyle(Paint.Style.STROKE);
        pp.setColor(Color.WHITE);

        canvas.drawCircle(out.centerX(), out.centerY(), targetWidth/2, pp);


        return outputBitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){

            if(v.getId() == R.id.drawer_home_button){

                Log.w("HomeButtonPressed", "Home");

            }else if(v.getId() == R.id.drawer_friends_button){


                Intent intent = new Intent(getApplicationContext(), QuizzeeFriendsZone.class);
                intent.putExtra("MODE", "null");
                intent.putExtra("TOP_MESSAGE", "FRIENDS");
                startActivity(intent);

            }else if(v.getId() == R.id.drawer_Profile_button){

                Intent intent = new Intent(getApplicationContext(), FinalProfileActivity.class);

                // SharedPrefrenceStorage.storeDonations(getApplicationContext(),"3");

                Bundle profileInfo = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());

                Log.w("ProfileButtonPressed", "Location :: "+profileInfo.getString("Location"));
                intent.putExtra("MAIN_PROFILE","");
                intent.putExtra("username",profileInfo.getString("Name"));
                intent.putExtra("status", database.getTitleProfileAllCatagory());
                intent.putExtra("location",profileInfo.getString("Location"));
                intent.putExtra("rank",SharedPrefrenceStorage.getGlobalRank(getApplicationContext()));
                intent.putExtra("k_time", database.getKillTimeTotalAllCatagory());
                intent.putExtra("user_score", database.getScoreTotalAllCatagory());
                intent.putExtra("user_win", database.getTotalWinAllCatagory());
                intent.putExtra("user_loose", database.getTotalLooseAllCatagory());
                intent.putExtra("user_donate", SharedPrefrenceStorage.getDonations(getApplicationContext()));

                startActivity(intent);
                finish();

            }else if(v.getId() == R.id.drawer_Donate_button){

                Intent intent = new Intent(getApplicationContext(), DonateActivity.class);
                startActivity(intent);


            }else if(v.getId() == R.id.drawer_Logs_button){

                Intent intent = new Intent(getApplicationContext(), LogsActivity.class);
                startActivity(intent);

            }

        }

        return true;
    }


    @Override
    protected void onPause() {

        Log.w("PagerValue", ""+pager.getCurrentItem());

        SharedPrefrenceStorage.storeHomeState(getApplicationContext(), pager.getCurrentItem());

        SharedPrefrenceStorage.storeActivityState(getApplicationContext(), 101);

        if(QuizzyApplication.gameSound.isPlaying() && QuizzyApplication.backGroundSound == true){

            QuizzyApplication.gameSound.stop();

        }

        flush();

        if(pager != null){

            Log.w("PagerValue", ""+pager.getCurrentItem());

            SharedPrefrenceStorage.storeHomeState(getApplicationContext(), pager.getCurrentItem());
        }

        super.onPause();
    }



    public void flush(){

        pager = null;

    }

    public AlertDialog makeDialog(String message, String buttonMessage)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        View v = getLayoutInflater().inflate(R.layout.oops_view, null);

        Typeface font = Typeface.createFromAsset(getAssets(),"font.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font1.TTF");

        TextView oopsTitle = (TextView)v.findViewById(R.id.oops_title);
        oopsTitle.setTypeface(font1);
        oopsTitle.setText(message);


        TextView oopsMessage = (TextView)v.findViewById(R.id.oops_message);
        oopsMessage.setTypeface(font1);


        Button tryAgain = (Button)v.findViewById(R.id.try_again);
        tryAgain.setTypeface(font1);
        tryAgain.setText(buttonMessage);

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

    public void updateFriends(){

        if(Session.getActiveSession() != null && Session.getActiveSession().isOpened()){

            new DownloadFacebookData(this).execute(Session.getActiveSession().getAccessToken());

        }
    }

    @Override
    protected void onDestroy() {

        Toast.makeText(getApplicationContext(), "Activity is Destroyed", Toast.LENGTH_LONG).show();

        SharedPrefrenceStorage.storeActivityState(getApplicationContext(), 100);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {


        finish();
    }

}


