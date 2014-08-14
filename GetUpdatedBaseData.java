import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.database.QuizzyDatabase;
import dev.ideapot.quizzy.UpdateActivity;


public class GetUpdatedBaseData extends AsyncTask<String,Void,String>{

    private String namespace = "http://tempuri.org/";
    private String url = "http://www.talentseal.com/talent/User/android.asmx";
    private String SOAP_ACTION;
    private SoapObject request = null;
    private SoapSerializationEnvelope envelope;
    private AndroidHttpTransport androidHttpTransport;
    private String MethodName = "get_updated_subject_chaptert_to_android_candidate";
    private String result ="";
    private UpdateActivity updateAcivity;
    private List<String> listofData;
    private HashMap<String,String> mapEntries;
    private SubjectImagesHandler handler;
    private AssetManager assetManager;

    public GetUpdatedBaseData(UpdateActivity updateAcivity)
    {
        this.updateAcivity = updateAcivity;
        assetManager = updateAcivity.getAssets();
        try {
            listofData = Arrays.asList(assetManager.list("images"));
        } catch (IOException e) {
            cancel(true);
        }
        mapEntries = new HashMap<String, String>();
        HandlerThread handlerThread = new HandlerThread("IMAGE_HANLDER");
        handlerThread.start();
        handler = new SubjectImagesHandler(handlerThread.getLooper(),updateAcivity.getApplicationContext());
    }

    private void SetEnvelope() {
        try {
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            androidHttpTransport = new AndroidHttpTransport(url);
            androidHttpTransport.debug = true;
        } catch (Exception e)
        {
            cancel(true);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            SOAP_ACTION = namespace + MethodName;

            request = new SoapObject(namespace, MethodName);

            PropertyInfo weightProp1 = new PropertyInfo();
            PropertyInfo weightProp2 = new PropertyInfo();

            weightProp1.setType(String.class);
            weightProp1.setName("initiator_user_code");
            weightProp1.setValue(params[0]);

            weightProp2.setType(String.class);
            weightProp2.setName("transaction_user_code");
            weightProp2.setValue("000007236");

            request.addProperty(weightProp1);
            request.addProperty(weightProp2);

            SetEnvelope();

            androidHttpTransport.call(SOAP_ACTION,envelope);

            result = envelope.getResponse().toString();

            weightProp1 = null;
            weightProp2 = null;

            Log.w("DEMOOOOO", result);

        } catch (Exception e) {
            cancel(true);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        getDataFromKeysJSON(getJSONKeyList(s),s);

        listofData = null;
        result =null;
        namespace = null;
        url = null;
        SOAP_ACTION = null;
        request = null;
        envelope = null;
        androidHttpTransport = null;
        MethodName = null;
        mapEntries = null;

        System.gc();
    }



    private ArrayList<String> getJSONKeyList(String JSONString)
    {
        ArrayList<String> data = new ArrayList<String>();
        try {
            JSONObject base = new JSONObject(JSONString);
            Iterator keys = base.keys();
            while(keys.hasNext())
            {
                data.add((String)keys.next());
            }
        } catch (JSONException e) {
            cancel(true);
        }
        catch (Exception e) {
            cancel(true);
        }
        return data;
    }

    private boolean fileExistance(String fname){
        try {
            updateAcivity.openFileInput(fname);
            return true;
        }catch (IOException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private void getDataFromKeysJSON(ArrayList<String> keys,String JSONString)
    {
        QuizzyDatabase database = new QuizzyDatabase(updateAcivity, "QUIZZY", null, 1);
        try {
            JSONObject obj = new JSONObject(JSONString);
            JSONArray array = null;
            JSONObject subObj = null;
            String cat = null,catid = null,catimgpath = null;

            for (String key : keys) {
                catid = key;
                array = obj.getJSONArray(catid);
                for (int j = 0; j < array.length(); j++) {
                    switch (j) {
                        case 0:
                            subObj = array.getJSONObject(j);
                            cat = subObj.getString("SUB_NAME");
                            catimgpath = subObj.getString("SUB_IMAGE_PATH");
                            if(listofData.contains(catid+".png"))
                            {
                                //add to internal storage chapter

                                handler.SaveImage(BitmapFactory.decodeStream(assetManager.open("images/"+catid+".png")),catid+".png");
                            }
                            else
                            {
                                // call to download procedure

                                if(!fileExistance(catid+".png")) //if file is not present in internal storage
                                {
                                    if(catimgpath.endsWith(".png"))
                                    {
                                        mapEntries.put(catid,"http://www.talentseal.com/"+catimgpath.substring(2, catimgpath.length()));
                                    }
                                }

                            }
                            break;
                        default:
                            subObj = array.getJSONObject(j);

                            //inserting into database

                            if(!(subObj.getString("CHAP_NAME").equalsIgnoreCase(cat+"_CHAPTER")))
                            {
                                database.insertUpdatesQUIZDATA(catid, cat, "http://www.talentseal.com/" + catimgpath.substring(2, catimgpath.length()), subObj.getString("CHAP_ID"), subObj.getString("CHAP_NAME"), "http://www.talentseal.com/" + subObj.getString("CHAP_IMG_PATH").substring(2, subObj.getString("CHAP_IMG_PATH").length()));
                            }

                            if(listofData.contains(subObj.getString("CHAP_ID")+".png"))
                            {
                                //add to internal storage chapter
                                handler.SaveImage(BitmapFactory.decodeStream(assetManager.open("images/"+subObj.getString("CHAP_ID")+".png")),subObj.getString("CHAP_ID")+".png");
                            }
                            else
                            {

                                // call to download procedure
                                if(!fileExistance(subObj.getString("CHAP_ID")+".png")) //if file not exhist in internal storage
                                {
                                    if(subObj.getString("CHAP_IMG_PATH").endsWith(".png"))
                                    {
                                        mapEntries.put(subObj.getString("CHAP_ID"),"http://www.talentseal.com/"+subObj.getString("CHAP_IMG_PATH").substring(2, subObj.getString("CHAP_IMG_PATH").length()));
                                    }
                                }

                             }
                            Log.e("DEMO", "data inserted...");
                            break;
                    }
                }
            }

            Log.w("DEMOO"," Query executed "+ database.getUpdatesQUIZDATA().getCount());
            Log.w("DEMOO","Image set :: "+mapEntries.size());

            database.close();
                            if(mapEntries.size()!=0)
                            {
                                Set<?> s = mapEntries.entrySet();
                                Iterator<?> it = s.iterator();
                                while(it.hasNext())
                                {
                                    Map.Entry entry = (Map.Entry)it.next();
                                    String name = (String)entry.getKey()+".png";
                                    String url = (String)entry.getValue();
                                    Bundle data = new Bundle();
                                    data.putString("url", url);
                                    data.putString("name", name);
                                    if(it.hasNext()){
                                        data.putString("meta", "null");
                                    }else {
                                        data.putString("meta", "last");
                                    }
                                    Message message = new Message();
                                    message.setData(data);
                                    handler.sendMessage(message);
                                }
                            }
                            else
                            {
                                updateAcivity.sendBroadcast(new Intent("DOWNLOAD_OVER"));
                            }


        } catch (JSONException e) {
            cancel(true);
        }catch (Exception e) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled(String s)
    {
        Log.e("DEMOOOOO", "Service Failed");
    }

}
