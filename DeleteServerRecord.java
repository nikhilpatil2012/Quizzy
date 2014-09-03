package tronbox.deleteserveruser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteServerRecord extends AsyncTask<String, String, String>{

    String result;
    Context context;

    String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    String SOAP_ACTION; SoapObject request = null;
    SoapSerializationEnvelope envelope; AndroidHttpTransport
            androidHttpTransport;
    String MethodName = "delete_android_registration_data";

    public DeleteServerRecord(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {


            SOAP_ACTION = namespace + MethodName;

            request = new SoapObject(namespace, MethodName);


            PropertyInfo weightProp1 = new PropertyInfo();


            weightProp1.setType(String.class);
            weightProp1.setName("user_code");
            weightProp1.setValue(params[0]);



            request.addProperty(weightProp1);

            SetEnvelope();



            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();

            Log.w("Deleted Result ", result);

        } catch (UnsupportedEncodingException e) {}
        //catch (ClientProtocolException e) {}
        catch (IOException e) {} catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
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


}


