package tronbox.social;

        import java.util.Arrays;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentSender;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.HandlerThread;
        import android.os.Message;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;
        import android.support.v4.view.ViewPager;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;
        import com.facebook.HttpMethod;
        import com.facebook.Request;
        import com.facebook.RequestBatch;
        import com.facebook.Response;
        import com.facebook.Session;
        import com.facebook.SessionState;
        import com.facebook.UiLifecycleHelper;
        import com.facebook.widget.LoginButton;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.CommonStatusCodes;
        import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.plus.People;
        import com.google.android.gms.plus.Plus;
        import com.google.android.gms.plus.model.people.Person;
        import com.google.android.gms.plus.model.people.PersonBuffer;


        import tronbox.Logs.ErrorLogs;
        import tronbox.arena.LoadingScreen;
        import tronbox.controller.QuizzyDatabase;
        import tronbox.networking.SubjectImagesHandler;
        import tronbox.view.ImageFrag;
        import tronbox.welcome.QuizzyApplication;
        import tronbox.welcome.SendUserCredentials;
        import tronbox.welcome.SharedPrefrenceStorage;
        import tronbox.welcome.UpdateActivity;


public class LoginFragment extends Fragment  {

    private static final String TAG = "MainFragment";
    private UiLifecycleHelper helper;
    private TextView screen, tv;
    private View view;
    private LoginButton login;
    private String myData;
    private Button button;
    private boolean isSessionOpened = false;

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress = false;

    private ImageButton signUp;

    private Session session;

    //private String id, firstName, lastName, gender, city, country, birthDay, birthMonth, birthYear, profileImage, userRegId;


    private ViewPager pager;
    private Handler hanlder;
    private static int SCREEn_NO = 0;
    private ScreenController controller;
    private ImageView[] slider;
    private int[] idsSlider = { R.id.vi3, R.id.vi2, R.id.vi1, R.id.vi4, R.id.vi5, R.id.vi6 };
    private Bitmap on, off;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getActivity().getActionBar().hide();

        helper = new UiLifecycleHelper(getActivity(), callback);
        helper.onCreate(savedInstanceState);


       ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "LOGIN_FRAGMENT");


    }

    @Override
    public void onStart() {
        super.onStart();

        SCREEn_NO = 0;
        hanlder.postDelayed(controller, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.activity_login, container, false); // here we are creating a View of our layout XML


        ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "LOGIN CLASS");

        on = BitmapFactory.decodeResource(getResources(), R.drawable.on);
        off = BitmapFactory.decodeResource(getResources(), R.drawable.off);

        hanlder = new Handler();
        controller = new ScreenController();
        pager = (ViewPager)view.findViewById(R.id.pager);

        pager.setAdapter(new MyFragPagerAdap(getActivity().getSupportFragmentManager()));

        //  this handle will control everything present in this view
        login = (LoginButton)view.findViewById(R.id.authButton);
        login.setFragment(this);
        login.setReadPermissions(Arrays.asList("public_profile", "user_birthday", "user_location", "user_friends")); // ask for more permissions here https://developers.facebook.com/docs/reference/login/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(SharedPrefrenceStorage.checkRegId(getActivity()) == false){


                    new AppIdGetterId(getActivity()).execute();

                }
            }
        });


        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                hanlder.removeCallbacks(controller);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                imageSliderController(pager.getCurrentItem());
            }
        });

        DisplayMetrics MAT = new DisplayMetrics();
        ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(MAT);

        View v = (View)view.findViewById(R.id.slider);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (MAT.heightPixels * 13) / 100);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        v.setLayoutParams(params);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (MAT.heightPixels * 10) / 100);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        login.setLayoutParams(params1);

        slider = new ImageView[idsSlider.length];

        for (int i = 0; i < slider.length; i++) {
            slider[i] = (ImageView)view.findViewById(idsSlider[i]);
        }

        hanlder.postDelayed(controller, 3000);

        imageSliderController(SCREEn_NO);

        return view; // Not attached to root
    }




    Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(Session statusSession, SessionState state, Exception exception) {


            session = statusSession;

            if(state.isOpened()){

                isSessionOpened = true;

                handler.sendEmptyMessage(1);

            }else {

                isSessionOpened = false;

                Log.w("Session", "Not Opened");

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        helper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            Toast.makeText(getActivity(), "DONE", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoadingScreen.class);
            startActivity(intent);
        }

        if (requestCode == RC_SIGN_IN) {

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        helper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        helper.onSaveInstanceState(outState);
    }


    Handler handler =  new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){

                new DownloadFacebookData(getActivity()).execute(session.getAccessToken());

            }
        }
    };

    class ScreenController implements Runnable {
        @Override
        public void run() {

            pager.setCurrentItem(SCREEn_NO);

            SCREEn_NO = (SCREEn_NO+1) % 6;

            hanlder.postDelayed(this, 3000);
        }

    }

    private void imageSliderController(int position) {
        switch (position) {
            case 0:
                slider[0].setImageBitmap(on);
                slider[1].setImageBitmap(off);
                slider[2].setImageBitmap(off);
                slider[3].setImageBitmap(off);
                slider[4].setImageBitmap(off);
                slider[5].setImageBitmap(off);
                break;
            case 1:
                slider[0].setImageBitmap(off);
                slider[1].setImageBitmap(on);
                slider[2].setImageBitmap(off);
                slider[3].setImageBitmap(off);
                slider[4].setImageBitmap(off);
                slider[5].setImageBitmap(off);
                break;
            case 2:
                slider[0].setImageBitmap(off);
                slider[1].setImageBitmap(off);
                slider[2].setImageBitmap(on);
                slider[3].setImageBitmap(off);
                slider[4].setImageBitmap(off);
                slider[5].setImageBitmap(off);
                break;
            case 3:
                slider[0].setImageBitmap(off);
                slider[1].setImageBitmap(off);
                slider[2].setImageBitmap(off);
                slider[3].setImageBitmap(on);
                slider[4].setImageBitmap(off);
                slider[5].setImageBitmap(off);
                break;
            case 4:
                slider[0].setImageBitmap(off);
                slider[1].setImageBitmap(off);
                slider[2].setImageBitmap(off);
                slider[3].setImageBitmap(off);
                slider[4].setImageBitmap(on);
                slider[5].setImageBitmap(off);

                break;

            case 5:
                slider[0].setImageBitmap(off);
                slider[1].setImageBitmap(off);
                slider[2].setImageBitmap(off);
                slider[3].setImageBitmap(off);
                slider[4].setImageBitmap(off);
                slider[5].setImageBitmap(on);
                break;


        }
    }

    class MyFragPagerAdap extends FragmentPagerAdapter {

        public MyFragPagerAdap(FragmentManager fm) {
            super(fm);

        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {

            ImageFrag im = new ImageFrag();
            Bundle b = new Bundle();
            switch (arg0) {
                case 0:

                    b.putInt("KEY", 0);
                    im.setArguments(b);

                    break;
                case 1:

                    b.putInt("KEY", 1);
                    im.setArguments(b);

                    break;
                case 2:
                    b.putInt("KEY", 2);
                    im.setArguments(b);

                    break;
                case 3:

                    b.putInt("KEY", 3);
                    im.setArguments(b);

                    break;

                case 4:

                    b.putInt("KEY", 4);
                    im.setArguments(b);

                    break;


                case 5:

                    b.putInt("KEY", 5);
                    im.setArguments(b);

                    break;

            }

            return im;
        }

        @Override
        public int getCount() {

            return 6;
        }

    }

}
