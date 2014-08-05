import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

public class MessageSender extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "send_message_to_receiver";
    //String gcmMethodName = "api_update";
    private String result ="";

    private void SetEnvelope() {

        try {
            // Creating SOAP envelope
            envelope = new  SoapSerializationEnvelope(SoapEnvelope.VER11);

            // You can comment that line if your web service is not .NET // one.
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request); androidHttpTransport = new
                    AndroidHttpTransport(url); androidHttpTransport.debug = true;

        } catch (Exception e)
        { Log.e("DEMOO", "TASK CANCELLED by setEnvelope");  }
    }

    @Override
    protected String doInBackground(String... params) {

        try {


            Log.w("DEMO", "MSG sender Started");

            SOAP_ACTION = namespace + MethodName;

            // Adding values to request object
            request = new SoapObject(namespace, MethodName);

            // Adding Double value to request object
            PropertyInfo weightProp1 = new PropertyInfo();
            PropertyInfo weightProp2 = new PropertyInfo();
            PropertyInfo weightProp3 = new PropertyInfo();


            weightProp1.setType(String.class);
            weightProp1.setName("sender_user_code");
            weightProp1.setValue(params[0]);

            weightProp2.setType(String.class);
            weightProp2.setName("receiver_user_code");
            weightProp2.setValue(params[1]);

            weightProp3.setType(String.class);
            weightProp3.setName("message");
            weightProp3.setValue(params[2]);


            request.addProperty(weightProp1);
            request.addProperty(weightProp2);
            request.addProperty(weightProp3);


            SetEnvelope();

            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();

        } catch (Exception e) {
            cancel(true);

            Log.e("DEMO","TASK CANCELLED by exception");
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e("DEMO","MESSAGE SENT");
    }
}
