import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



public class ScoreSender extends AsyncTask<String, Void, String>{
	
	private Context context; 
	final private String namespace = "http://tempuri.org/";
	final private String url = "http://www.talentseal.com/talent/User/android.asmx";
	private String SOAP_ACTION;
	private SoapObject request = null;
	private SoapSerializationEnvelope envelope;
	private AndroidHttpTransport androidHttpTransport;
	final private String MethodName = "insert_android_candidate_score_card";
	private String result = "";

	final private String transaction_user = "000007236";

	protected void SetEnvelope() {

		try {
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			androidHttpTransport = new AndroidHttpTransport(url);
			androidHttpTransport.debug = true;
		} catch (Exception e) {
			cancel(true);
		}
	}

	
	
	public ScoreSender(Context context) {
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			SOAP_ACTION = namespace + MethodName;
			request = new SoapObject(namespace, MethodName);

			PropertyInfo weightProp = new PropertyInfo();
			weightProp.setType(String.class);
			weightProp.setName("user_code");
			weightProp.setValue(params[0]);

			PropertyInfo weightProp1 = new PropertyInfo();
			weightProp1.setType(String.class);
			weightProp1.setName("game_code");
			weightProp1.setValue(params[1]);

			PropertyInfo weightProp2 = new PropertyInfo();
			weightProp2.setType(String.class);
			weightProp2.setName("marks_format");
			weightProp2.setValue(params[2]);

			PropertyInfo weightProp3 = new PropertyInfo();
			weightProp3.setType(String.class);
			weightProp3.setName("transaction_user");
			weightProp3.setValue(transaction_user);

			request.addProperty(weightProp);
			request.addProperty(weightProp1);
			request.addProperty(weightProp2);
			request.addProperty(weightProp3);

			SetEnvelope();
			androidHttpTransport.call(SOAP_ACTION, envelope);
			result = envelope.getResponse().toString();
			
		} catch (Exception e) {
			cancel(true);
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		Log.i("DEMO", "Response from server :: "+result);
		
	}
	
	@Override
	protected void onCancelled(String result) {
	
		Log.i("DEMO", "TASK CANCELLED");
	}
}
