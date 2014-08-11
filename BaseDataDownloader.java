package tronbox.networking;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import tronbox.controller.QuizzyDatabase;

public class BaseDataDownloader extends AsyncTask<Void, Void, String>{
	
	private Context context;
	String namespace = "http://tempuri.org/";
	private String url = "http://www.talentseal.com/talent/User/android.asmx";

	String SOAP_ACTION;
	SoapObject request = null;
	SoapSerializationEnvelope envelope;
	AndroidHttpTransport androidHttpTransport;
	String MethodName = "get_question_category_sub_category_list";
	String result = "";
	
	String transaction_user = "000007236";

    SubjectImagesHandler handler;
    HandlerThread thread;

	protected void SetEnvelope() {

		try {
			// Creating SOAP envelope
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

			// You can comment that line if your web service is not .NET
			// // one.
			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);
			androidHttpTransport = new AndroidHttpTransport(url);
			androidHttpTransport.debug = true;

		} catch (Exception e) {
			System.out.println("Soap Exception---->>>" + e.toString());
		}
	}

	public BaseDataDownloader(Context context) {
		this.context = context;
	}
	@Override
	protected void onPreExecute() {
		Log.i("DEMO", "task onPreExecute");
	}
	@Override
	protected String doInBackground(Void... arg0) {
		Log.i("DEMO", "task doInBackground");

        thread = new HandlerThread("Images");
        thread.start();
        handler = new SubjectImagesHandler(thread.getLooper(), context);

		try {
			SOAP_ACTION = namespace + MethodName;

			// Adding values to request object
			request = new SoapObject(namespace, MethodName);

			// Adding Double value to request object
			PropertyInfo weightProp = new PropertyInfo();
			weightProp.setType(String.class);
			weightProp.setName("transaction_user");
			weightProp.setValue(transaction_user);

			request.addProperty(weightProp);
			
			SetEnvelope();
			// SOAP calling webservice
				androidHttpTransport.call(SOAP_ACTION, envelope);
				// Got Webservice response
				result = envelope.getResponse().toString();
				//Log.e("DEMO", "Lenght :: " + result.length());
		} catch (Exception e) {
			cancel(true);
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i("DEMO", "task onPostExecute");
		//Log.w("DEMO", result);
		context.sendBroadcast(new Intent("quizzy.RECEIVER").putExtra("ACK", "BaseDataDownloader.DONE"));
		getDataFromKeysJSON(getJSONKeyList(result),result);
	}
	@Override
	protected void onCancelled(String result) {
		context.sendBroadcast(new Intent("quizzy.RECEIVER").putExtra("ACK", "BaseDataDownloader.FAIL"));
	}
	
	private ArrayList<String> getJSONKeyList(String JSONString)
	{
		ArrayList<String> data = new ArrayList<String>();
		try {
			JSONObject base = new JSONObject(JSONString);
			Iterator<?> keys = base.keys();
			
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
	
	private void getDataFromKeysJSON(ArrayList<String> keys,String JSONString)
	{
		QuizzyDatabase database = new QuizzyDatabase(context, "QUIZZY", null, 1);
		try {
			JSONObject obj = new JSONObject(JSONString);
			JSONArray array = null;
			JSONObject subObj = null;
			String cat = null,catid = null,catimgpath = null;
			HashMap<String, String> imagepool = new HashMap<String, String>();
			
			for(int i=0;i<keys.size();i++)
			{
				catid = keys.get(i);
				array = obj.getJSONArray(catid);
				for(int j=0;j<array.length();j++)
				{
					switch (j) {
					
					case 0:
						subObj = array.getJSONObject(j);
						cat = subObj.getString("SUB_NAME");
						catimgpath = subObj.getString("SUB_IMAGE_PATH");
						break;

					default:
						subObj = array.getJSONObject(j);
						
						/*Log.w("DEMO", "CATAGORY :: "+catid);
						Log.w("DEMO", "CATAGORY_IMG_PATH :: "+catimgpath);
						Log.w("DEMO", "CATAGORY_NAME:: "+cat);
						Log.w("DEMO", "CHAP_ID :: "+subObj.getString("CHAP_ID"));
						Log.w("DEMO", "CHAP_NAME :: "+subObj.getString("CHAP_NAME"));
						Log.w("DEMO", "CHAP_IMG_PATH :: "+subObj.getString("CHAP_IMG_PATH"));
						*/
						
						database.insertUpdatesQUIZDATA(catid, cat, "http://www.talentseal.com/"+catimgpath.substring(1, catimgpath.length()), subObj.getString("CHAP_ID"), subObj.getString("CHAP_NAME"), "http://www.talentseal.com/"+subObj.getString("CHAP_IMG_PATH").substring(1, subObj.getString("CHAP_IMG_PATH").length()));

						if(!catimgpath.equals("null"))
						{
							imagepool.put(catid, "http://www.talentseal.com/"+catimgpath.substring(1, catimgpath.length()));
						}
						if(!subObj.getString("CHAP_IMG_PATH").equals("null"))
						{
							imagepool.put(subObj.getString("CHAP_ID"), "http://www.talentseal.com/"+subObj.getString("CHAP_IMG_PATH").substring(1, subObj.getString("CHAP_IMG_PATH").length()));
						}
						
						Log.e("DEMO", "data inserted...");
						
						break;
					}
				}
			}
			
			Log.e("DEMO", ""+imagepool+"\n Lenght ==> "+imagepool.size());
			
			Set<?> s = imagepool.entrySet();
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
					
		} catch (JSONException e) {
			cancel(true);
		}catch (Exception e) {
			cancel(true);
		}
	}
}
