package tronbox.welcome;



        import org.ksoap2.SoapEnvelope;
        import org.ksoap2.serialization.PropertyInfo;
        import org.ksoap2.serialization.SoapObject;
        import org.ksoap2.serialization.SoapSerializationEnvelope;
        import org.ksoap2.transport.AndroidHttpTransport;

        import android.content.Context;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.util.Log;
        import android.widget.Toast;

        import tronbox.Logs.ErrorLogs;

public class SendUserCredentials extends AsyncTask<String, Void, String>{

    String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";

    String SOAP_ACTION; SoapObject request = null;
    SoapSerializationEnvelope envelope; AndroidHttpTransport
            androidHttpTransport;
    String MethodName = "insert_android_data";
    //String gcmMethodName = "api_update";
    String result ="";
    Context context;



    public SendUserCredentials(Context context) {


        ErrorLogs.PARSE_ERRORS(ErrorLogs.Activity_Flow, "SEND_CREDENTIALS");

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
        { System.out.println("Soap Exception---->>>" +
                e.toString()); } }
    @Override
    protected String doInBackground(String... params) {


        try {

            ErrorLogs.PARSE_ERRORS(ErrorLogs.Server_LOGS, "Calling Server");

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
            PropertyInfo weightProp8 = new PropertyInfo();
            PropertyInfo weightProp9 = new PropertyInfo();
            PropertyInfo weightProp10 = new PropertyInfo();
            PropertyInfo weightProp11 = new PropertyInfo();
            PropertyInfo weightProp12 = new PropertyInfo();
            PropertyInfo weightProp13 = new PropertyInfo();
            PropertyInfo weightProp14 = new PropertyInfo();
            PropertyInfo weightProp15 = new PropertyInfo();
            PropertyInfo weightProp16 = new PropertyInfo();
            PropertyInfo weightProp17 = new PropertyInfo();
            PropertyInfo weightProp18 = new PropertyInfo();




            weightProp1.setType(String.class);
            weightProp1.setName("first_name");
            weightProp1.setValue(params[0]);

            weightProp2.setType(String.class);
            weightProp2.setName("middle_name");
            weightProp2.setValue("");

            weightProp3.setType(String.class);
            weightProp3.setName("last_name");
            weightProp3.setValue("");

            weightProp4.setType(String.class);
            weightProp4.setName("birth_day");
            weightProp4.setValue(params[1]);

            weightProp5.setType(String.class);
            weightProp5.setName("birth_month");
            weightProp5.setValue("");

            weightProp6.setType(String.class);
            weightProp6.setName("birth_year");
            weightProp6.setValue("");

            weightProp7.setType(String.class);
            weightProp7.setName("gender");
            weightProp7.setValue(params[2]);

            weightProp8.setType(String.class);
            weightProp8.setName("mobile_no");
            weightProp8.setValue("");

            weightProp9.setType(String.class);
            weightProp9.setName("country");
            weightProp9.setValue(params[3]);

            weightProp10.setType(String.class);
            weightProp10.setName("city");
            weightProp10.setValue("");

            weightProp11.setType(String.class);
            weightProp11.setName("timezone");
            weightProp11.setValue("");

            weightProp12.setType(String.class);
            weightProp12.setName("quotes");
            weightProp12.setValue("");

            weightProp13.setType(String.class);
            weightProp13.setName("api_key");
            weightProp13.setValue(params[4]);

            weightProp14.setType(String.class);
            weightProp14.setName("source"); //  fb, gp, mb
            weightProp14.setValue("FB");

            weightProp15.setType(String.class);
            weightProp15.setName("source_id"); // facebook or g+ id
            weightProp15.setValue(params[5]);

            weightProp16.setType(String.class);
            weightProp16.setName("language_known"); // facebook or g+ id
            weightProp16.setValue("English");

            weightProp17.setType(String.class);
            weightProp17.setName("user_type"); // facebook or g+ id
            weightProp17.setValue("ON");

            weightProp18.setType(String.class);
            weightProp18.setName("candidate_image_path"); // facebook or g+ id
            weightProp18.setValue("");

            request.addProperty(weightProp1);
            request.addProperty(weightProp2);
            request.addProperty(weightProp3);
            request.addProperty(weightProp4);
            request.addProperty(weightProp5);
            request.addProperty(weightProp6);
            request.addProperty(weightProp7);
            request.addProperty(weightProp8);
            request.addProperty(weightProp9);
            request.addProperty(weightProp10);
            request.addProperty(weightProp11);
            request.addProperty(weightProp12);
            request.addProperty(weightProp13);
            request.addProperty(weightProp14);
            request.addProperty(weightProp15);
            request.addProperty(weightProp16);
            request.addProperty(weightProp17);
            request.addProperty(weightProp18);

            SetEnvelope();

            // SOAP calling webservice
            androidHttpTransport.call(SOAP_ACTION,envelope);

            // Got Webservice response
            result =  envelope.getResponse().toString();

        } catch (Exception e) {
            cancel(true);
        }


  ErrorLogs.PARSE_ERRORS(ErrorLogs.Server_LOGS, "Data Stored at server result = "+result);

  String userCode = result.split("~")[0];


  return userCode;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        SharedPrefrenceStorage.storeUserCode(context, result);
        new GetUpdatedBaseData(context).execute(result);

        
    }

}
