package edu.neu.android.mhealth.uscteensver1.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.utils.EmailValidator;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.BaseActivity;

public class EmailSetupActivity extends BaseActivity {
	
	private String TAG = "EmailSetupActivity";	
	
	private EditText mEditText;
	private Button   mBtnCancel;
	private Button   mBtnSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, TAG);
        setContentView(R.layout.activity_setemail);
        
        setupViews();
    }
    
    private void setupViews() {
        mEditText  = (EditText) findViewById(R.id.edit_email);
        mBtnSet    = (Button) findViewById(R.id.setemail_set);
        mBtnCancel = (Button) findViewById(R.id.setemail_cancel);
        
        mBtnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = mEditText.getText().toString();
				EmailValidator validator = new EmailValidator();
				
				if (!validator.validate(email)) {
					Toast.makeText(
						getApplicationContext(), "Invalid email address!", Toast.LENGTH_LONG
					).show();
					return;
				}
				
				if (email.contains("@gmail.com")) {					
					DataStorage.SetValue(getApplicationContext(), TeensGlobals.KEY_EMAIL_ADDRESS, email);
					finish();
				} else {
					Toast.makeText(
						getApplicationContext(), "Sorry, but it should be a Gmail :-(", Toast.LENGTH_LONG
					).show();		
				}
			}        	
        });
        
        mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}        	
        });
    }
}
