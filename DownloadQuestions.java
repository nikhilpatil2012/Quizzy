package tronbox.arena;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DownloadQuestions extends AsyncTask<String, Void, String>{

    private ArrayList<Question> list_Question;
    private HashMap<String, String> image_pool_map;


    String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    String SOAP_ACTION; SoapObject request = null;
    SoapSerializationEnvelope envelope; AndroidHttpTransport
            androidHttpTransport;
    String MethodName = "create_game_question";
    //String gcmMethodName = "api_update";
    String result ="";
    Context context;


    public DownloadQuestions(Context context) {

        this.context = context;
        list_Question = new ArrayList<Question>();
        image_pool_map = new HashMap<String, String>();


    }

    private void SetEnvelope() {

        try {
            // Creating SOAP envelope
            envelope = new  SoapSerializationEnvelope(SoapEnvelope.VER11);

            // You can comment that line if your web service is not .NET // one.
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request); androidHttpTransport = new
                    AndroidHttpTransport(url); androidHttpTransport.debug = true;

        } catch (Exception e)
        { System.out.println("Soap Exception---->>>" +
                e.toString()); } }
    @Override
    protected String doInBackground(String... params) {


        try {


            Log.w("DEMO", "Started");

            SOAP_ACTION = namespace + MethodName;

            // Adding values to request object
            request = new SoapObject(namespace, MethodName);

            // Adding Double value to request object
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
            weightProp3.setName("transaction_user");
            weightProp3.setValue("000007236");

            weightProp4.setType(String.class);
            weightProp4.setName("initiator_user_code");
            weightProp4.setValue("000007263");


            request.addProperty(weightProp1);
            request.addProperty(weightProp2);
            request.addProperty(weightProp3);
            request.addProperty(weightProp4);



            SetEnvelope();



            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();


            Log.w("DEMO", result);

            // new DeleteServerRecord(context).execute("000007233"); // delete server record




        } catch (Exception e) {
            cancel(true);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


        ((GameArena)context).renderQuestions(result);

    }


}
