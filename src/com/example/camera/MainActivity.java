package com.example.camera;

import android.app.Activity;  
import android.content.Intent;  
import android.os.Bundle;  
import android.util.Log;  
import android.view.View;  
import android.widget.Button;  
  
public class MainActivity extends Activity {  
    private final static String LOG_TAG = "MainActivity";  
  
    private Button startButton = null;  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  
  
        startButton = (Button)findViewById(R.id.button_start);  
        startButton.setOnClickListener(
        	new View.OnClickListener() {
        	    @Override  
        	    public void onClick(View v) {  
        	        if(v.equals(startButton)) {  
        	            Intent intent = new Intent("com.example.camera.CameraActivity");  
        	            startActivity(intent);  
        	        }  
        	    }
        	}
        );  
  
        Log.i(LOG_TAG, "Main Activity Created.");  
    }  
  
}  