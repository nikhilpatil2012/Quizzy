package tronbox.social;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import tronbox.controller.QuizzyDatabase;

public class GetUserCodeWithFbId extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "get_list_of_social_media_user";
    private String result ="";
    private Context context;

    public GetUserCodeWithFbId(Context context)
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

        weightProp1.setType(String.class);
        weightProp1.setName("source_name");
        weightProp1.setValue(params[0]);

        weightProp2.setType(String.class);
        weightProp2.setName("source_list");
        weightProp2.setValue(params[1]);

        request.addProperty(weightProp1);
        request.addProperty(weightProp2);
    }

    @Override
    protected void onPostExecute(String s) {
        // decode server response and store it into database
            decodeResponseAndStoreInDatabase(s);
    }

    @Override
    protected void onCancelled(String s) {


    }

    private void decodeResponseAndStoreInDatabase(String result)
    {
        QuizzyDatabase database = new QuizzyDatabase(context,"QUIZZY",null,1);
        StringBuilder builder = new StringBuilder();
        try
        {
            String[] data = result.split(":");
            String[] temp = null;
            for(int i=0;i<data.length;i++)
            {
                temp = data[i].split("~");
                if(database.insertUserFriendsTable(temp[0],temp[1]) == 1){
                    if(i==0)
                    {
                          builder.append(temp[1]);
                    }
                    else
                    {
                        builder.append("~").append(temp[1]);
                    }
                }
            }
            Log.w("DANGER","Result :: "+builder.toString().length());
        }catch (Exception e){

        }
        finally {
            database.close();
        }
    }
}
