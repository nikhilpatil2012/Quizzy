import ideapot.quizzy.controller.QuizzyDatabase;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ScoringBoard extends Activity {

	
	private ListView scoreListAll;
	private Cursor c;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_activity_all);
		
		scoreListAll = (ListView)findViewById(R.id.scorelistall);
		
		QuizzyDatabase database = new QuizzyDatabase(getApplicationContext(), "QUIZZY", null, 1);
		
		c = database.getUpdatesSCOREDATA();
		
		database.close();
		
		scoreListAll.setAdapter(new MyListAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.score_activity_all, menu);
		return true;
	}
	
	class MyListAdapter extends BaseAdapter
	{
		private int temp = 0;
		private Typeface font = Typeface.createFromAsset(getAssets(), "font.ttf");
		
		@Override
		public int getCount() {
			return c.getCount();
		}

		@Override
		public Object getItem(int arg0) {
			
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View counterView, ViewGroup parent) {
			
			if(temp>9)
			{
				temp = 0;
			}
			LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = counterView;
				
				c.moveToPosition(arg0);
				v = inflator.inflate(R.layout.master_score_view, null);
				v.setEnabled(false);
				v.setOnClickListener(null);
				v.setBackgroundColor(colorCodes[temp]);
				
				ViewHolder.name = (TextView)v.findViewById(R.id.name);
				ViewHolder.grank = (TextView)v.findViewById(R.id.grank);
				ViewHolder.score = (TextView)v.findViewById(R.id.score);
				ViewHolder.played = (TextView)v.findViewById(R.id.played);
				ViewHolder.won = (TextView)v.findViewById(R.id.won);
				
				ViewHolder.name.setTypeface(font);
				ViewHolder.grank.setTypeface(font);
				ViewHolder.score.setTypeface(font);
				ViewHolder.played.setTypeface(font);
				ViewHolder.won.setTypeface(font);
				
				ViewHolder.name.setText(c.getString(c.getColumnIndex("CHAP_NAME")));
				ViewHolder.grank.setText(Html.fromHtml("<html>G Rank :"+c.getString(c.getColumnIndex("GLOBAL_RANK"))+"</html>"));
				ViewHolder.score.setText(Html.fromHtml("<html>Score :"+c.getString(c.getColumnIndex("TOTAL_SCORE"))+"</html>"));
				ViewHolder.won.setText(Html.fromHtml("<html>Won<br>"+c.getString(c.getColumnIndex("CORRECT_QUES"))+"</html>"));
				ViewHolder.played.setText(Html.fromHtml("<html>Played<br>"+(Integer.valueOf(c.getString(c.getColumnIndex("CORRECT_QUES")))+Integer.valueOf(c.getString(c.getColumnIndex("INCORRECT_QUES"))))+"</html>"));
			
			temp++;
			return v;
		}
		
	}
	static class ViewHolder
	{
		static TextView name,grank,score,played,won;
	}
	static int[] colorCodes = {Color.parseColor("#5c39b6"),Color.parseColor("#dc572e"),Color.parseColor("#1cba36"),Color.parseColor("#2c87ee"),Color.parseColor("#dd2154"),Color.parseColor("#0140a3"),Color.parseColor("#e9c010"),Color.parseColor("#ed3b3c"),Color.parseColor("#0ab275"),Color.parseColor("#d2ba19")};
}
