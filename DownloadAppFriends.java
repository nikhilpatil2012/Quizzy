package tronbox.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import tronbox.controller.QuizzyDatabase;
import tronbox.networking.SubjectImagesHandler;
import tronbox.welcome.Test;

public class DownloadAppFriends extends AsyncTask<String, Void, String>{

    private QuizzyDatabase database;
    private Context context;
    private SubjectImagesHandler handler;
    private HandlerThread thread;

    public DownloadAppFriends(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        thread = new HandlerThread("Images");
        thread.start();
        handler = new SubjectImagesHandler(thread.getLooper(), context);


        database = new QuizzyDatabase(context, "QUIZZY", null, 1);

        String myUrl = "https://graph.facebook.com/me?fields=friends.limit(500).fields(id,name,picture,cover)&format=json&access_token="+params[0];

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


            Log.w("Gesponseeeeee",  build.toString());



            JSONObject ob1 = new JSONObject(build.toString());
            JSONObject ob2 = ob1.getJSONObject("friends");
            JSONArray data = ob2.getJSONArray("data");

            Log.w("Data Count", ""+data.length());

            for(int i=0; i<=data.length()-1; i++){

                JSONObject info = data.getJSONObject(i);

                String name = info.getString("name");
                String id = info.getString("id");
                String pic = info.getJSONObject("picture").getJSONObject("data").getString("url");
                String cover = info.getJSONObject("cover").getString("source");

                database.insertFriends(id, name, pic);

                Bundle bundle = new Bundle();
                bundle.putString("url", pic);
                bundle.putString("name", id+".png");

                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);


                Bundle bundle1 = new Bundle();
                bundle1.putString("url", cover);
                bundle1.putString("name", id+"_timeline.png");

                Message message1 = new Message();
                message1.setData(bundle1);
                handler.sendMessage(message1);

                Log.w("FacebookName",name);
                Log.w("Facebookid",id);
                Log.w("Facebookpic",pic);
                Log.w("Facebookcover",cover);

            }


        }

        catch (MalformedURLException e){ Log.e("Facebook_Connect_Error", "Url is malformed "+e.getLocalizedMessage());}
        catch (IOException e) {Log.e("Facebook_Connect_Error", "IO Error "+e.getLocalizedMessage());}
        catch (JSONException e) {Log.e("Facebook_Connect_Error", "Jason Error "+e.getLocalizedMessage());}


        return null;
    }
}

