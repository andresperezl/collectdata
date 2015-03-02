package edu.usf.csee.collectdata;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends Activity {

    private final static String TAG = "CollectData";

    public static int experimentId;
    public static RequestQueue requestQueue;
    public static String serverIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        Button experimentBtn = (Button) findViewById(R.id.experiment_btn);
        experimentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Experiment Button Click");
                requestNewExperiment();
            }
        });

        final ToggleButton servicesBtn = (ToggleButton) findViewById(R.id.services_btn);
        servicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servicesBtn.isChecked()){
                    startServices();
                } else {
                    stopServices();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getServerIp(){
        EditText et = (EditText)findViewById(R.id.server_ip);
        serverIp = "http://"+et.getText().toString();
        return serverIp;
    }

    private void refreshExperimentId(){
        TextView tv = (TextView) findViewById(R.id.experiment_id);
        tv.setText("Experiment ID: "+experimentId);
    }

    private void requestNewExperiment(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getServerIp()+"/experiment", null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject){
                        Log.d(TAG, "Experiment ID: "+experimentId);
                        experimentId = jsonObject.optInt("id");
                        Log.d(TAG, "Experiment ID: "+experimentId);
                        refreshExperimentId();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "REquest Processing an error");
            }
        });

        requestQueue.add(request);
    }

    public void startServices(){
        DataCollectorService.startPhoneAcceleometer(this);
        DataCollectorService.startPhoneGyroscope(this);
        DataSenderService.startDataSenderService(MainActivity.this);
    }

    public void stopServices(){
        DataCollectorService.stopPhoneAcceleometer(this);
        DataCollectorService.stopPhoneGyroscope(this);
        DataSenderService.stoptDataSenderService(this);
    }


}
