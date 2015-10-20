package com.example.accelerometer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SendSMSActivity extends Activity{
	
	Button buttonSend, location;
	EditText textPhoneNo;
	//EditText textSMS;
	TextView textSMS;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sent_sms);
 
		buttonSend = (Button) findViewById(R.id.buttonSend);
		location = (Button) findViewById(R.id.location);
		textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
		//textSMS = (EditText) findViewById(R.id.editTextSMS);
		textSMS = (TextView) findViewById(R.id.TextSMS);
		textSMS.setText("Warning: Your Child is running and out of range!!!"); 
 
		buttonSend.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
 
			  String phoneNo = textPhoneNo.getText().toString();
			  //String sms = textSMS.getText().toString() + "Warning: Your Child is out of reach!!!";
			 
			  
			  
			  try {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneNo, null, "sdd", null, null);
				Toast.makeText(getApplicationContext(), "SMS Sent!",
							Toast.LENGTH_LONG).show();
			  } catch (Exception e) {
				Toast.makeText(getApplicationContext(),
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
				e.printStackTrace();
			  }
			  
			}
		});
		
		location.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 if(v.getId() == R.id.location){
					  
						Intent intent = new Intent(SendSMSActivity.this, LocationActivity.class);
						startActivity(intent);
						
			  }
			}
			
		});
		
	}

}
