package com.example.accelerometer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener, OnClickListener  {

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	TextView title, tv, tv1, tv2, lab_Acc;
	RelativeLayout layout;
	private LinearLayout lilayout;
	private Button btnStart, btnStop, btnTxt;
	//mysQLiteHelper myHelper;
	float x;
	float y, z; 
	private boolean started = false;
	private ArrayList<AccelData> sensorData;
	private View mChart;
	private String sdcard;
	BufferedWriter out;
	private float lowX = 0, lowY = 0, lowZ = 0;
	private final float FILTERING_VALAUE = 0.1f;
	//private String filename = "MySampleFile.txt";
	private String filepath = "MyFileStorage";
	long startime = 0;
	
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
	    File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
		
		//get the sensor service
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//get the accelerometer sensor
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//GET layout
		layout = (RelativeLayout)findViewById(R.id.relative);
		//sensorData = new ArrayList();
		
		List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		mAccelerometer = sensorList.get(0);
		
		btnStart = (Button)findViewById(R.id.start);
		btnStop = (Button)findViewById(R.id.stop);
		btnTxt = (Button) findViewById(R.id.btnTxt);
		 
		btnStart.setOnClickListener(this);	
        btnStop.setOnClickListener(this);
        btnTxt.setOnClickListener(this);
        //btnUpload.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);	
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
 
        lilayout = (LinearLayout) findViewById(R.id.chart_container);
       /* if (sensorData == null || sensorData.size() == 0) {
            btnUpload.setEnabled(false);
        }*/
       
		title = (TextView)findViewById(R.id.name);
		tv = (TextView)findViewById(R.id.xval);
		tv1 = (TextView)findViewById(R.id.yval);
		tv2 = (TextView)findViewById(R.id.zval);
		lab_Acc = (TextView)findViewById(R.id.accuracy);
		//btnUpload = (TextView)findViewById(R.id.upload);
		
	}
 
@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.start:
			btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            //btnUpload.setEnabled(false);
            sensorData = new ArrayList<AccelData>();
            // save previous data if available
            started = true;
            Sensor accel = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, accel,
                    SensorManager.SENSOR_DELAY_FASTEST);
            startime = System.currentTimeMillis();
            
          
			break;
		case R.id.stop:
			 	btnStart.setEnabled(true);
	            btnStop.setEnabled(false);
	            //btnUpload.setEnabled(true);
	            started = false;
	            mSensorManager.unregisterListener(this);
	            lilayout.removeAllViews();
	            openChart();
	        	//weka we = new weka();
	        	
	    		/*try {
	    			if(we.RetrieveData() != null){
	    				btnTxt.setVisibility(View.VISIBLE);
	    				//lab_Acc.setVisibility(View.VISIBLE);
	    				lab_Acc.setText("Classifier Accuracy:" + "\t\t" + we.RetrieveData());
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}*/
			//stop reading and show data in chart
			break;
		case R.id.btnTxt:
			//warning text
			try{
			Intent intent = new Intent(MainActivity.this, SendSMSActivity.class);
			startActivity(intent);
			}catch(ActivityNotFoundException e){
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
	}


private void openChart() {
    if (sensorData != null || sensorData.size() > 0) {
        long t = sensorData.get(0).getTimestamp();
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        XYSeries xSeries = new XYSeries("X");
        XYSeries ySeries = new XYSeries("Y");
        XYSeries zSeries = new XYSeries("Z");

        for (AccelData data: sensorData) {
            xSeries.add(data.getTimestamp() - t, data.getX());
            ySeries.add(data.getTimestamp() - t, data.getY());
            zSeries.add(data.getTimestamp() - t, data.getZ());
        }

        dataset.addSeries(xSeries);
        dataset.addSeries(ySeries);
        dataset.addSeries(zSeries);

        XYSeriesRenderer xRenderer = new XYSeriesRenderer();
        xRenderer.setColor(Color.RED);
        xRenderer.setPointStyle(PointStyle.CIRCLE);
        xRenderer.setFillPoints(true);
        xRenderer.setLineWidth(1);
        xRenderer.setDisplayChartValues(false);

        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        yRenderer.setColor(Color.GREEN);
        yRenderer.setPointStyle(PointStyle.CIRCLE);
        yRenderer.setFillPoints(true);
        yRenderer.setLineWidth(1);
        yRenderer.setDisplayChartValues(false);

        XYSeriesRenderer zRenderer = new XYSeriesRenderer();
        zRenderer.setColor(Color.BLUE);
        zRenderer.setPointStyle(PointStyle.CIRCLE);
        zRenderer.setFillPoints(true);
        zRenderer.setLineWidth(1);
        zRenderer.setDisplayChartValues(false);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setLabelsColor(Color.RED);
        multiRenderer.setChartTitle("t vs (x,y,z)");
        multiRenderer.setXTitle("Sensor Data");
        multiRenderer.setYTitle("Values of Acceleration");
        multiRenderer.setZoomButtonsVisible(true);
        for (int i = 0; i < sensorData.size(); i++) {

            multiRenderer.addXTextLabel(i + 1, ""
                    + (sensorData.get(i).getTimestamp() - t));
        }
        for (int i = 0; i < 12; i++) {
            multiRenderer.addYTextLabel(i + 1, ""+i);
        }

        multiRenderer.addSeriesRenderer(xRenderer);
        multiRenderer.addSeriesRenderer(yRenderer);
        multiRenderer.addSeriesRenderer(zRenderer);

        // Getting a reference to LinearLayout of the MainActivity Layout

        // Creating a Line Chart
        mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                multiRenderer);

        // Adding the Line Chart to the LinearLayout
        lilayout.addView(mChart);
        

    }
}

	
	public void onStartClick(View view) throws IOException {
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onStopClick(View view) throws IOException {
	    mSensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	
	
	 public void writeFileSdcard(String fileName,String message) {
	        try {
	        	
	        	FileOutputStream fout = openFileOutput(fileName, Context.MODE_APPEND);
	        	byte [] bytes = message.getBytes();
	        	
	        	fout.write(bytes);
	        	fout.close();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	    }
	 
	@Override
	public void onSensorChanged(SensorEvent event) {
		// Many sensor return 3 values, one for each axis.
		
		String message = new String();
		long timestop = System.currentTimeMillis();
	
		if(started){
		
			if((timestop - startime) <= 10000 ){
				
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			
			//Low-Pass Filter
	        lowX = x * FILTERING_VALAUE + lowX * (1.0f - FILTERING_VALAUE);
	        lowY = y * FILTERING_VALAUE + lowY * (1.0f - FILTERING_VALAUE);
	        lowZ = z * FILTERING_VALAUE + lowZ * (1.0f - FILTERING_VALAUE);

	        //High-pass filter
	        float highX  = x -  lowX;
	        float highY  = y -  lowY;
	        float highZ  = z -  lowZ;
			double highA = Math.sqrt(highX * highX + highY * highY + highZ * highZ);
			
			
			DecimalFormat df = new DecimalFormat("#,##0.000");
			
			message = df.format(highX) + " , ";
			message += df.format(highY) + " , ";
			message += df.format(highZ) + " , ";
			message += df.format(highA) + "\n";
			
			
			AccelData data = new AccelData(timestop, x, y, z);
	
			sensorData.add(data);
			
			//writeFileSdcard("acc.txt", message);
		
			writeFileSdcard("10smix.csv", data.toString());
			// Display values using TextView
			title.setText(R.string.app_name);
			tv.setText("X axis" + "\t\t" +  x);
			tv1.setText("Y axis" + "\t\t" + y);
			tv2.setText("Z axis" + "\t\t" + z);
			
			}
			else{
				started = false;	
			}
		 
		}
		
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		if(started == true)
		mSensorManager.unregisterListener(this);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
