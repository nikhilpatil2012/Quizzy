package tronbox.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import tronbox.Logs.ErrorLogs;

public class SharedPrefrenceStorage {

    public static SharedPreferences initialize(Context context){

        return context.getSharedPreferences("Quizzy", Context.MODE_PRIVATE);
    }

    public static void StoreProfileInfo(Context context, String FirstName, String LastName, String UserCode, String FacebookId) {

         SharedPreferences.Editor editor = initialize(context).edit();
         editor.putString("Name", FirstName);
         editor.putString("LastName", LastName);
         editor.putString("UserCode", UserCode);
         editor.putString("FacebookId", FacebookId);
         editor.putBoolean("Login", true);
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
        bundle.putString("UserCode", preferences.getString("UserCode", ""));
        bundle.putString("FacebookId", preferences.getString("FacebookId", ""));

         return  bundle;
    }

    public static String getUserCode(Context context){

        SharedPreferences preferences = initialize(context);

        return preferences.getString("UserCode", "");

    }

    public static String getUserFacebookId(Context context){

        return initialize(context).getString("FacebookId", "");
    }


}
