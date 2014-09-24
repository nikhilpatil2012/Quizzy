package tronbox.deleteserveruser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FriendUserCode extends AsyncTask<String, String, String>{

    String result;
    Context context;

    String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    String SOAP_ACTION; SoapObject request = null;
    SoapSerializationEnvelope envelope; AndroidHttpTransport
            androidHttpTransport;
    String MethodName = "get_user_code";

    public FriendUserCode(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            SOAP_ACTION = namespace + MethodName;

            request = new SoapObject(namespace, MethodName);

            PropertyInfo weightProp1 = new PropertyInfo();
            PropertyInfo weightProp2 = new PropertyInfo();

            weightProp1.setType(String.class);
            weightProp1.setName("source_name");
            weightProp1.setValue(params[0]);


            weightProp2.setType(String.class);
            weightProp2.setName("source_code");
            weightProp2.setValue(params[1]);


            request.addProperty(weightProp1);
            request.addProperty(weightProp2);

            SetEnvelope();

            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();

            new DeleteServerRecord(context).execute(result);


            Log.w("Friends User Code MB", result);

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

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request); androidHttpTransport = new
            AndroidHttpTransport(url); androidHttpTransport.debug = true;

        }  catch (Exception e)

        {
            System.out.println("Soap Exception---->>>" + e.toString());
        }
    }


}


