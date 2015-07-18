package tronbox.social;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import tronbox.arena.DownloadFreshQuestions;
import tronbox.controller.QuizzyDatabase;
import tronbox.welcome.QuizzyApplication;

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
    private String value = null;

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
        weightProp1.setValue("FB");

        weightProp2.setType(String.class);
        weightProp2.setName("source_list");
        weightProp2.setValue(params[0]);

        request.addProperty(weightProp1);
        request.addProperty(weightProp2);


        if(params.length>1)
        {
            value = params[1];
        }
/*
        if(params[1] != null){

        }*/
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
                int data1 = database.insertUserFriendsTable(temp[0],temp[1]);
                if(data1 == 1){
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
        }catch (Exception e){

        }
        finally {

            if(value != null && value.equals("mole")){

                String data = database.getUserCode(QuizzyApplication.challengerFacebookId);

                if(!data.equals("null")){

                    Log.w("Mole", "Executed");

                    QuizzyApplication.challengerUserCode = data;

                    new DownloadFreshQuestions(context).execute(QuizzyApplication.SubjectCode,QuizzyApplication.ChapterCode, data);

                }

            }

            database.close();
        }

    }
}
