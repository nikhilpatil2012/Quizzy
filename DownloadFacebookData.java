package tronbox.social;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tronbox.Logs.ErrorLogs;
import tronbox.controller.QuizzyDatabase;
import tronbox.networking.SubjectImagesHandler;
import tronbox.welcome.SendUserCredentials;
import tronbox.welcome.SharedPrefrenceStorage;

public class DownloadFacebookData extends AsyncTask<String, Void, String>{

    private QuizzyDatabase database;
    private Context context;
    private SubjectImagesHandler handler;
    private HandlerThread thread;
    private String id = "null", name = "null", pic = "null", location = "null", birthday = "null", gender = "null";
    private HashMap<String,String> mapEntries;
    public DownloadFacebookData(Context context){
        this.context = context;
    }

    private StringBuilder builder = new StringBuilder();

    @Override
    protected String doInBackground(String... params) {

        thread = new HandlerThread("Images");
        thread.start();
        handler = new SubjectImagesHandler(thread.getLooper(), context);

        mapEntries = new HashMap<String,String>();

        database = new QuizzyDatabase(context, "QUIZZY", null, 1);

        String myUrl = "https://graph.facebook.com/me?fields=id,name,birthday,location,gender,picture,friends.limit(500).fields(id,name,picture)&format=json&access_token="+params[0];

        try {

            HttpURLConnection httpConnection = (HttpURLConnection)new URL(myUrl).openConnection();
            httpConnection.connect();

            InputStream inputStream = httpConnection.getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(reader);

            StringBuilder build = new StringBuilder();
            String temp;

            while((temp = bufferReader.readLine()) != null){
                build.append(temp);
            }


            Log.w("NewGesponseeeeee",  build.toString());



            JSONObject master = new JSONObject(build.toString());

            if(master.has("birthday")){

                birthday = master.getString("birthday");

                Log.w("ProfileFacebookBirthDay", birthday);

            }

            if(master.has("name")){

                name = master.getString("name");
                Log.w("ProfileFacebookName",name);


            }

            if(master.has("gender"));
            {
                gender = master.getString("gender");

                Log.w("ProfileFacebookGender", gender);


            }

            if(master.has("id")){

                id = master.getString("id");

                Log.w("ProfileFacebookid",id);


            }

            if(master.has("picture")){


                pic = master.getJSONObject("picture").getJSONObject("data").getString("url");

                mapEntries.put(id+".png", pic);

                Log.w("ProfileFacebookpic",pic);


            }

            if(master.has("location")){


                location = master.getJSONObject("location").getString("name");

                Log.w("ProfileFacebookLocation", location);


            }


            SharedPrefrenceStorage.StoreProfileInfo(context, name, id, location, birthday, gender);


            if(master.has("friends")){

                JSONObject ob2 = master.getJSONObject("friends");
                JSONArray data = ob2.getJSONArray("data");

                Log.w("Data Count", ""+data.length());

                int count = 0;
                int temp1 = 1;
                for(int i=0; i<=data.length()-1; i++){

                    JSONObject info = data.getJSONObject(i);

                    String name = info.getString("name");
                    String id = info.getString("id");
                    String pic = info.getJSONObject("picture").getJSONObject("data").getString("url");

                    temp1 = database.insertFriends(id, name, pic);

                    if(temp1 == 1){

                        if(count == 0){

                            builder.append(id);
                            count++;

                        }else{

                            builder.append("~").append(id);
                        }

                    }

                    mapEntries.put(id+".png", pic);

                    Log.w("FacebookName",name);
                    Log.w("Facebookid",id);
                    Log.w("Facebookpic",pic);

                }

            }

            Iterator iterator = mapEntries.entrySet().iterator();

            while(iterator.hasNext()) {

                Map.Entry entry = (Map.Entry)iterator.next();

                String name = (String)entry.getKey();
                String url = (String)entry.getValue();

                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("name", name);

                if(!iterator.hasNext()){
                    bundle.putString("meta", params[1]);
                }

                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);

            }

            if(builder.length() > 3){
                Log.w("DANGER",builder.toString());
                new GetUserCodeWithFbId(context).execute(builder.toString());
            }

        }

        catch (MalformedURLException e){ Log.e("Facebook_Connect_Error", "Url is malformed "+e.getLocalizedMessage());}
        catch (IOException e) {Log.e("Facebook_Connect_Error", "IO Error "+e.getLocalizedMessage());}
        catch (JSONException e) {Log.e("Facebook_Connect_Error", "Jason Error "+e.getLocalizedMessage());}

        return null;
    }

}

