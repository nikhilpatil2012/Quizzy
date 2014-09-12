package tronbox.social;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import tronbox.welcome.DonateActivity;
import tronbox.welcome.MasterHomeScreen;
import tronbox.welcome.SharedPrefrenceStorage;

public class DonationSender extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "insert_donate_questions_by_android_candidate";
    private String result ="";
    private Context context;
    private DonateActivity activity;

    public DonationSender(Context context,DonateActivity activity)
    {
        this.context = context;
        this.activity = activity;
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
    protected void onPreExecute() {
        Toast.makeText(context,"Sending donation",Toast.LENGTH_LONG).show();
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

        weightProp1.setType(String.class);
        weightProp1.setName("user_code");
        weightProp1.setValue(params[0]);

        weightProp2.setType(String.class);
        weightProp2.setName("que_format");
        weightProp2.setValue(params[1]);

        request.addProperty(weightProp1);
        request.addProperty(weightProp2);

    }

    @Override
    protected void onPostExecute(String s) {
        if(s.contains("~"))
        {
            SharedPrefrenceStorage.storeDonations(context,s.split("~")[0]);
            Toast.makeText(context,"Donation success",Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(context, MasterHomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            activity.finish();
        }
        else
        {
            Toast.makeText(context,"Error in sending...!",Toast.LENGTH_LONG).show();
        }
    }
}
