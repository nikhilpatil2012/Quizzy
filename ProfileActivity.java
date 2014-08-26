import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import dev.support.SharedPrefrenceStorage;

public class ProfileActivity extends Activity {

    private EditText name,mobile;
    private RadioGroup radioGroup;
    private DatePicker datePicker;
    private Button submit;
    private String usr_name,usr_mobile,usr_bday,usr_bmonth,usr_byear,usr_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        getActionBar().hide();

        Bundle b = SharedPrefrenceStorage.getProfileData(getApplicationContext());

        init(b.getString("Name"),b.getString("userMobile"),b.getString("userGender"),b.getString("birthDay"),b.getString("birthMonth"),b.getString("birthYear"));


    }

    private void init(String u_name,String u_mobile,String u_gender,String ubday,String ubmonth,String u_byear)
    {
        name = (EditText)findViewById(R.id.name);
        mobile = (EditText)findViewById(R.id.mobile);
        datePicker = (DatePicker)findViewById(R.id.datePicker1);
        radioGroup = (RadioGroup)findViewById(R.id.gender);

        submit = (Button)findViewById(R.id.submit);

        name.setText(u_name);
        mobile.setText(u_mobile);

        try {
            datePicker.updateDate(Integer.valueOf(u_byear), Integer.valueOf(ubmonth), Integer.valueOf(ubday));
        }catch (Exception e){

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

}
