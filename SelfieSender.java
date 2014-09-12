package tronbox.social;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tronbox.arena.Question;
import tronbox.arena.Score;
import tronbox.networking.SubjectImagesHandler;
import tronbox.welcome.QuizzyApplication;


public class SelfieSender extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "insert_android_candidate_game_played_image";

    private String result ="";
    private Context context;


    public SelfieSender(Context context)
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
        weightProp1.setName("docbinaryarray");
        weightProp1.setValue(params[1]);

        weightProp2.setType(String.class);
        weightProp2.setName("initiator_user_code");
        weightProp2.setValue(params[0]);

        weightProp3.setType(String.class);
        weightProp3.setName("image_title");
        weightProp3.setValue("");

        request.addProperty(weightProp1);
        request.addProperty(weightProp3);
        request.addProperty(weightProp2);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.w("DANGER","result :: "+s);
        Toast.makeText(context,"Click sent to server...!",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled(String s) {
        Toast.makeText(context,"Error in sending click...!",Toast.LENGTH_LONG).show();
    }
}
