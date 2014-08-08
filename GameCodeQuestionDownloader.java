import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

/**
 * Created by ideapot on 5/8/14.
 */
public class GameCodeQuestionDownloader extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "get_question_by_game_code";
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


            Log.w("DEMO", "GameCodeQuestionDownloader Started");

            SOAP_ACTION = namespace + MethodName;

            // Adding values to request object
            request = new SoapObject(namespace, MethodName);

            // Adding Double value to request object
            PropertyInfo weightProp1 = new PropertyInfo();


            weightProp1.setType(String.class);
            weightProp1.setName("game_code_transaction_user");
            weightProp1.setValue(params[0]+"~000007236");


            request.addProperty(weightProp1);

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
    protected void onPostExecute(String result) {

        Log.e("DEMO",result);
    }
}
