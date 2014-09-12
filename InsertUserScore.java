package tronbox.welcome;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import tronbox.Logs.ErrorLogs;
import tronbox.social.GetGlobalRank;

public class InsertUserScore extends AsyncTask<String, Void, String>{

    String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    String SOAP_ACTION; SoapObject request = null;
    SoapSerializationEnvelope envelope; AndroidHttpTransport
            androidHttpTransport;
    String MethodName = "insert_game_score_to_candidate";
    //String gcmMethodName = "api_update";
    String result ="";
    Context context;



    public InsertUserScore(Context context) {
        this.context = context;
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
        {  }
    }
    @Override
    protected String doInBackground(String... params) {


        try {

            SOAP_ACTION = namespace + MethodName;

            // Adding values to request object
            request = new SoapObject(namespace, MethodName);

            // Adding Double value to request object
            PropertyInfo weightProp1 = new PropertyInfo();
            PropertyInfo weightProp2 = new PropertyInfo();
            PropertyInfo weightProp3 = new PropertyInfo();
            PropertyInfo weightProp4 = new PropertyInfo();
            PropertyInfo weightProp5 = new PropertyInfo();
            PropertyInfo weightProp6 = new PropertyInfo();
            PropertyInfo weightProp7 = new PropertyInfo();
           // PropertyInfo weightProp8 = new PropertyInfo();
            PropertyInfo weightProp9 = new PropertyInfo();

            weightProp1.setType(String.class);
            weightProp1.setName("user_code");
            weightProp1.setValue(params[0]);

            weightProp2.setType(String.class);
            weightProp2.setName("subject_code");
            weightProp2.setValue(params[1]);

            weightProp3.setType(String.class);
            weightProp3.setName("chapter_code");
            weightProp3.setValue(params[2]);

            weightProp4.setType(String.class);
            weightProp4.setName("scored");
            weightProp4.setValue(params[3]);

            weightProp5.setType(String.class);
            weightProp5.setName("win");
            weightProp5.setValue(params[4]);

            weightProp6.setType(String.class);
            weightProp6.setName("loose");
            weightProp6.setValue(params[5]);

            weightProp7.setType(String.class);
            weightProp7.setName("kill_time");
            weightProp7.setValue(params[6]);


            weightProp9.setType(String.class);
            weightProp9.setName("tag");
            weightProp9.setValue(params[7]);


            request.addProperty(weightProp1);
            request.addProperty(weightProp2);
            request.addProperty(weightProp3);
            request.addProperty(weightProp4);
            request.addProperty(weightProp5);
            request.addProperty(weightProp6);
            request.addProperty(weightProp7);
            request.addProperty(weightProp9);

            SetEnvelope();

            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();

        } catch (Exception e) {
            cancel(true);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        new GetGlobalRank(context).execute(SharedPrefrenceStorage.getUserCode(context));
        Log.e("DANGER","Result :: "+result);
    }

}
