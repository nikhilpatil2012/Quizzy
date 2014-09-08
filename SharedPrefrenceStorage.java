package tronbox.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import tronbox.Logs.ErrorLogs;

public class SharedPrefrenceStorage {

    public static SharedPreferences initialize(Context context){

        return context.getSharedPreferences("Quizzy", Context.MODE_PRIVATE);
    }

    public static void StoreProfileInfo(Context context, String FirstName, String FacebookId, String UserLocation, String UserDob, String gender) {

         SharedPreferences.Editor editor = initialize(context).edit();
         editor.putString("Name", FirstName);
         editor.putString("FacebookId", FacebookId);
         editor.putBoolean("Login", true);
         editor.putString("Location", UserLocation);
         editor.putString("Dob", UserDob);
         editor.putString("Gender", gender);

         editor.commit();

        ErrorLogs.PARSE_ERRORS(ErrorLogs.SharedPrefrenceLog, "PROFILE DATA SUCCESSFULLY STORED IN THE SHARED_PREFRENCE "+getUserCode(context));
    }

    public static void StoreRegId(Context context, String RegId) {

        SharedPreferences.Editor editor = initialize(context).edit();
        editor.putString("RegId", RegId);
        editor.commit();

        QuizzyApplication.userRegId = RegId;

        ErrorLogs.PARSE_ERRORS(ErrorLogs.SharedPrefrenceLog, "REGISTRATION ID SUCCESSFULLY STORED IN THE SHARED_PREFRENCE");

        ErrorLogs.PARSE_ERRORS(ErrorLogs.GCM_CODE, "Registration Id Stored = "+GetRegId(context));
        ErrorLogs.PARSE_ERRORS(ErrorLogs.GCM_CODE, "Is Registration Id Stored = "+checkRegId(context));

    }

    public static String GetRegId(Context context) {

        return initialize(context).getString("RegId", "null");
    }

    public static boolean checkRegId(Context context){

        if (initialize(context).getString("RegId", "null").length() > 5) {

            return true;

        } else {

            return false;
        }

    }

    public static boolean check(Context context){

        return initialize(context).getBoolean("Login", false);
    }

    public static Bundle getProfileInfo(Context context){

        SharedPreferences preferences = initialize(context);

        Bundle bundle = new Bundle();
        bundle.putString("Name", preferences.getString("Name", ""));
        bundle.putString("FacebookId", preferences.getString("FacebookId", ""));
        bundle.putString("Location", preferences.getString("Location", ""));
        bundle.putString("Dob", preferences.getString("Dob", ""));
        bundle.putString("Gender", preferences.getString("Gender", ""));


        return  bundle;
    }

    public static void storeUserCode(Context context, String usercode){

        SharedPreferences.Editor editor = initialize(context).edit();
        editor.putString("UserCode", usercode);
        editor.commit();

    }

    public static String getUserCode(Context context){

        SharedPreferences preferences = initialize(context);

        return preferences.getString("UserCode", "");

    }

    public static String getUserFacebookId(Context context){

        return initialize(context).getString("FacebookId", "");
    }

    public static boolean saveUpdateInfo(Context context,String name,String mobile,String gender,String bday,String bmonth,String byear){

        SharedPreferences.Editor editor = initialize(context).edit();
        editor.putString("Name", name);
        editor.putString("userMobile", mobile);
        editor.putString("userGender", gender);
        editor.putString("birthDay", bday);
        editor.putString("birthMonth", bmonth);
        editor.putString("birthYear", byear);
        return editor.commit();
    }


    public static void storeHomeState(Context context, int state){

        Log.w("HomeState", ""+state);

        SharedPreferences.Editor editor = initialize(context).edit();
        editor.putInt("State", state);
        editor.commit();

    }

    public static int getCurrentState(Context context){

        return initialize(context).getInt("State", 0);
    }

}
