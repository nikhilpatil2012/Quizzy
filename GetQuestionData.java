import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

public class GetQuestionData extends AsyncTask<String,Void,String>{


    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION; private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "create_game_question_with_image";

    private String result ="";


    private void SetEnvelope() {
        try {
            // Creating SOAP envelope
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // You can comment that line if your web service is not .NET // one.
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request); androidHttpTransport = new
                    AndroidHttpTransport(url); androidHttpTransport.debug = true;
        } catch (Exception e)
        {
            cancel(true);
        }
    }

    /**
     *
     *
     * @return String questionJSON
     */
    @Override
    protected String doInBackground(String... params) {
        try {

            SOAP_ACTION = namespace + MethodName;

            request = new SoapObject(namespace, MethodName);

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
            weightProp4.setValue(params[2]);

            request.addProperty(weightProp1);
            request.addProperty(weightProp2);
            request.addProperty(weightProp3);
            request.addProperty(weightProp4);

            SetEnvelope();

            androidHttpTransport.call(SOAP_ACTION,envelope);

            result = envelope.getResponse().toString();

            weightProp1 = null;
            weightProp2 = null;
            weightProp3 = null;
            weightProp4 = null;


        } catch (Exception e) {
            cancel(true);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        Log.w("DEMOOOOO", s);

        nullAllVariables();
    }

    private void nullAllVariables()
    {
        result =null;
        namespace = null;
        url = null;
        SOAP_ACTION = null;
        request = null;
        envelope = null;
        androidHttpTransport = null;
        MethodName = null;
        System.gc();
    }

    @Override
    protected void onCancelled(String s) {


        Log.e("DEMOOOOO", "Service Failed");
    }
}
