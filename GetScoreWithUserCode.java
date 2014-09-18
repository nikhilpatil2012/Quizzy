package tronbox.social;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import tronbox.arena.LoadingScreen;
import tronbox.controller.QuizzyDatabase;

public class GetScoreWithUserCode extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "get_list_of_social_android_candidates";
    private String result ="";
    private Context context;
    private String category,chapter,data;
    private final String USER_CODE="user_code",WIN="win",LOOSE="loose",KILL_TIME="kill_time",DONATE="donate",GAME_SCORED="game_scored",TAG="tag";

    public GetScoreWithUserCode(Context context)
    {
        this.context = context;
    }
    private void SetEnvelope() {
        try {
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request); androidHttpTransport = new
                    AndroidHttpTransport(url); androidHttpTransport.debug = true;
        } catch (Exception e)
        {
            cancel(true);
        }
    }
    @Override
    protected String doInBackground(String... params) {

        try {
            SOAP_ACTION = namespace + MethodName;

            // Adding values to request object
            request = new SoapObject(namespace, MethodName);

            // Adding values to request object
            initValuesPassing(request,params);

            SetEnvelope();

            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result = envelope.getResponse().toString();

            category = params[1];

            chapter = params[2];

        } catch (Exception e) {
            cancel(true);
        }

        return result;
    }

    private void initValuesPassing(SoapObject request,String... params)
    {
        PropertyInfo weightProp1 = new PropertyInfo();
        PropertyInfo weightProp2 = new PropertyInfo();
        PropertyInfo weightProp3 = new PropertyInfo();


        weightProp1.setType(String.class);
        weightProp1.setName("user_code_list");
        weightProp1.setValue(params[0]);
        data = params[0];

        weightProp2.setType(String.class);
        weightProp2.setName("subject_code");
        weightProp2.setValue(params[1]);

        weightProp3.setType(String.class);
        weightProp3.setName("chapter_code");
        weightProp3.setValue(params[2]);

        request.addProperty(weightProp1);
        request.addProperty(weightProp2);
        request.addProperty(weightProp3);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.w("DANGER","data :: "+s);
        boolean f = false;

        String[] param = data.split("~");

        for(int i=0;i<param.length;i++)
        {
            if(s.contains(param[i]))
            {
                f=true;
                break;
            }
        }

        if(f)
        {
            Log.w("DANGER","if");
            //decode the JSON String from server
            decodeServerResponseAndSaveToDb(s);
        }
        else {
            //show individual score
            Log.w("DANGER","else part no record in database of friends");
            ((LoadingScreen)context).callLeaderBoard("user");
        }
    }

    @Override
    protected void onCancelled(String s) {
        Log.e("DANGER","TASK CANCELLED");
    }

    /**
     * Method decode the JSON string from server and save data into database
     *
     * @param response JSON string from server
     */
    private void decodeServerResponseAndSaveToDb(String response)
    {
        QuizzyDatabase database = new QuizzyDatabase(context,"QUIZZY",null,1);
        try
        {
            JSONObject baseString = new JSONObject(response);
            JSONArray array = baseString.getJSONArray(category);
            JSONObject obj = array.getJSONObject(0);
            JSONArray dataArray = obj.getJSONArray(chapter);
            JSONObject temp = null;

            for(int i=0;i<dataArray.length();i++)
            {
                temp = dataArray.getJSONObject(i);
                database.insertDataOfFriend(temp.getString(USER_CODE),chapter,String.valueOf(temp.get(DONATE)),String.valueOf(temp.getInt(GAME_SCORED)),temp.getString(WIN),temp.getString(LOOSE),temp.getString(KILL_TIME),temp.getString(TAG));
            }

        }catch (JSONException e)
        {

        }catch (Exception e)
        {

        }
        finally {
            database.close();
            ((LoadingScreen)context).callLeaderBoard("not user");
        }
    }
}
