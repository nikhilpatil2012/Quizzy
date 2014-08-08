import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ideapot on 8/8/14.
 */

public class FakeUserService extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "get_fake_question_profile_score_details";

    private String result ="";
    private ArrayList<Score> scoreArrayList;
    private Bundle profileInfo;
    private String questionString;
    private Context context;

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

    public FakeUserService(Context context)
    {
        this.context = context;
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
        PropertyInfo weightProp4 = new PropertyInfo();

        weightProp1.setType(String.class);
        weightProp1.setName("subject_code");
        weightProp1.setValue(params[0]);

        weightProp2.setType(String.class);
        weightProp2.setName("chapter_code");
        weightProp2.setValue(params[1]);

        weightProp3.setType(String.class);
        weightProp3.setName("initiator_user_code");
        weightProp3.setValue(params[2]);

        weightProp4.setType(String.class);
        weightProp4.setName("transaction_user_code");
        weightProp4.setValue("000007236");


        request.addProperty(weightProp1);
        request.addProperty(weightProp2);
        request.addProperty(weightProp3);
        request.addProperty(weightProp4);
    }

    @Override
    protected void onPostExecute(String s) {
        String[] data = s.split("@@@");

        questionString = data[0];
        profileInfo = decodeFakeProfile(data[1]);
        scoreArrayList = decodeFakeScore(data[2]);

    }


    private Bundle decodeFakeProfile(String profileJSONString)
    {
        Bundle profileInfo = new Bundle();
        try
        {
            JSONObject object = new JSONObject(profileJSONString);
            //getting the key
            String key = (String)object.keys().next();

            //getting the array of profile details
            JSONArray array = object.getJSONArray(key);

            //getting the first JSONObject of array of profile details
            JSONObject details = array.getJSONObject(0);

            String key1 = null;

            Iterator<String> list = details.keys();

            String nameOfImage = null;

            while(list.hasNext())
            {
                key1 = (String)list.next();
                profileInfo.putString(key1,details.getString(key1));

                if(key1.equals("user_code"))
                {
                    nameOfImage = details.getString(key1);
                }
                if(key1.equals("ANDROID_CANDIDATE_IMAGE_PATH"))
                {
                    HandlerThread thread = new HandlerThread("D_IMAGE");
                    thread.start();
                    Bundle b = new Bundle();
                    Log.e("SCORE",key1+" :: "+"http://www.talentseal.com/"+details.getString(key1).substring(2));
                    b.putString("url","http://www.talentseal.com/"+details.getString(key1).substring(1));
                    b.putString("name",nameOfImage+".png");
                    Message msg = new Message();
                    msg.setData(b);
                    new SubjectImagesHandler(thread.getLooper(), context).sendMessage(msg);
                }

            }
        }catch (Exception e)
        {
             cancel(true);
        }
        return profileInfo;
    }

    private ArrayList<Score> decodeFakeScore(String scoreJSONString)
    {
        ArrayList<Score> list = new ArrayList<Score>();
        try
        {
            JSONObject object = new JSONObject(scoreJSONString);
            //getting the key
            String key = (String)object.keys().next();
            //getting the array of score
            JSONArray array = object.getJSONArray(key);

            JSONObject obj = null;

            for(int i=0;i<array.length();i++)
            {
                obj = array.getJSONObject(i);

                switch (i)
                {
                    case 0:case 1:case 2:
                        if((Float.valueOf(String.valueOf(obj.get("M"))))>0.0)
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(10-(Float.valueOf(String.valueOf(obj.get("T")))))+10))));
                        }
                        else
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("M")))))))));
                        }
                    break;
                    case 3:case 4:case 5:
                        if((Float.valueOf(String.valueOf(obj.get("M"))))>0.0)
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(10-(Float.valueOf(String.valueOf(obj.get("T")))))+20))));

                        }
                        else
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("M")))))))));
                        }
                    break;
                    case 6:case 7:case 8:
                       if((Float.valueOf(String.valueOf(obj.get("M"))))>0.0)
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(10-(Float.valueOf(String.valueOf(obj.get("T")))))+30))));

                        }
                    else
                       {
                           list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("M")))))))));
                       }
                    break;
                    case 9:
                        if((Float.valueOf(String.valueOf(obj.get("M"))))>0.0)
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(10-(Float.valueOf(String.valueOf(obj.get("T")))))+40))));

                        }
                        else
                        {
                            list.add(new Score(String.valueOf(obj.get("Q_ID")),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("T"))))))),String.valueOf(((int)(1*(Float.valueOf(String.valueOf(obj.get("M")))))))));
                        }
                    break;
                }
            }
        }catch (Exception e)
        {
            cancel(true);
        }
        return list;
    }

    @Override
    protected void onCancelled(String s) {

        Log.e("FAKEUSER","TASK CANCELLED");
    }
}
