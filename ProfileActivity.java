package tronbox.welcome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import tronbox.social.R;


public class ProfileActivity extends Activity {

    private EditText name,mobile;
    private RadioGroup radioGroup;
    private DatePicker datePicker;
    private ImageView proImageView;
    private Button submit;
    private String usr_name,usr_mobile,usr_bday,usr_bmonth,usr_byear,usr_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);

        initActionBar(getActionBar());

        Bundle b = SharedPrefrenceStorage.getProfileInfo(getApplicationContext());

        init(b.getString("Name"),"-",b.getString("Gender"),b.getString("Dob"),b.getString("FacebookId"));


    }

    private void init(String u_name,String u_mobile,String u_gender,String udob,String u_code)
    {
        proImageView = (ImageView)findViewById(R.id.profilePic);
        try
        {

            proImageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput(u_code+".png")));
        }catch (Exception e){

        }

        name = (EditText)findViewById(R.id.name);
        mobile = (EditText)findViewById(R.id.mobile);
        datePicker = (DatePicker)findViewById(R.id.datePicker1);
        radioGroup = (RadioGroup)findViewById(R.id.gender);

        submit = (Button)findViewById(R.id.submit);

        name.setText(u_name);
        mobile.setText(u_mobile);

        if(!udob.equals("null")){

            String dob[] = udob.split("/");
            Log.w("DOB", ""+dob[0]+"\n"+dob[1]+"\n"+dob[2]);

            try {
                datePicker.updateDate(Integer.valueOf(dob[2]), Integer.valueOf(dob[0])-1, Integer.valueOf(dob[1]));
            }catch (Exception e){

            }
        }



        if(u_gender.equals("male"))
        {
            ((RadioButton)findViewById(R.id.male)).setChecked(true);
            ((RadioButton)findViewById(R.id.female)).setChecked(false);
        }
        else
        {
            ((RadioButton)findViewById(R.id.female)).setChecked(true);
            ((RadioButton)findViewById(R.id.male)).setChecked(false);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllValuesAndSave();
            }
        });

        proImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1235;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    private void getAllValuesAndSave()
    {

        usr_name = name.getText().toString();
        usr_mobile = mobile.getText().toString();
        usr_bday = ""+datePicker.getDayOfMonth();
        usr_bmonth = ""+datePicker.getMonth();
        usr_byear = ""+datePicker.getYear();
        if(radioGroup.getCheckedRadioButtonId() == R.id.male)
        {
            usr_gender = "male";
        }
        else
        {
            usr_gender = "female";
        }

        //store data in SP
        if(SharedPrefrenceStorage.saveUpdateInfo(getApplicationContext(),usr_name,usr_mobile,usr_gender,usr_bday,usr_bmonth,usr_byear))
        {
            Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Profile Update Error",Toast.LENGTH_LONG).show();
        }

    }

    private void initActionBar(ActionBar actionBar)
    {
        actionBar.setTitle("Login");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar_score_activity, null);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mCustomView.setLayoutParams(params);
        actionBar.setCustomView(mCustomView);
        TextView actionBarText = (TextView)mCustomView.findViewById(R.id.nameOfScreen);
        actionBarText.setText("Profile");

        TextView back = (TextView)mCustomView.findViewById(R.id.back);
        back.setText("Home");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MasterHomeScreen.class);
                startActivity(intent);

            }
        });

        actionBar.setDisplayShowCustomEnabled(true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1235:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    proImageView.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.w("DEMO", "path >> " + filePath);
                                ExifInterface exif = new ExifInterface(filePath);
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                                Log.w("DEMO", "ORIENTATION :: " + orientation);
                                Bitmap image = BitmapFactory.decodeFile(filePath);
                                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                                    Matrix mat = new Matrix();
                                    mat.postRotate(90);
                                    image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
                                }
                                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                                    Matrix mat = new Matrix();
                                    mat.postRotate(180);
                                    image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
                                }
                                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                                    Matrix mat = new Matrix();
                                    mat.postRotate(270);
                                    image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
                                }

                                proImageView.setImageBitmap(Bitmap.createScaledBitmap(image,proImageView.getWidth(),proImageView.getHeight(),true));

                                File f = new File(SharedPrefrenceStorage.getUserFacebookId(getApplicationContext())+".png");
                                FileOutputStream out = openFileOutput(f.getName(), Context.MODE_PRIVATE);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                out.write(bos.toByteArray(), 0, bos.toByteArray().length);
                                out.close();

                            } catch (OutOfMemoryError e) {
                                Toast.makeText(getApplicationContext(),"Error in updating image",Toast.LENGTH_LONG).show();

                            } catch (Exception e) {

                                Toast.makeText(getApplicationContext(),"Error in updating image",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
        }
    }
}
