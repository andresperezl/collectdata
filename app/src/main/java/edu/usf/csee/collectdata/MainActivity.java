package edu.usf.csee.collectdata;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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

    private final int MAX_STEPS = 10;
    private static int steps;

    public static int experimentId;
    public static RequestQueue requestQueue;
    public static String serverIp;

    private static ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
    private TextView exStatus;
    private static StepsHandler stepsHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        stepsHandler = new StepsHandler();
        Button experimentBtn = (Button) findViewById(R.id.experiment_btn);
        experimentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Experiment Button Click");
                requestNewExperiment();
            }
        });
        exStatus = (TextView) findViewById(R.id.ex_status_text);
        final ToggleButton servicesBtn = (ToggleButton) findViewById(R.id.services_btn);
        servicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servicesBtn.isChecked()){
                    countDownTimer.start();
                } else {
                    countDownTimer.cancel();
                    stopServices();
                    exStatus.setText("Experiment NOT running");
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
        exStatus.setText("Experiment NOT running");
    }

    private void requestNewExperiment(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getServerIp()+"/experiment", (String) null,
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
                Log.d(TAG, "Request Processing an error");

            }
        });

        requestQueue.add(request);
    }

    public void startServices(){
        steps = 0;
        tone.startTone(ToneGenerator.TONE_PROP_ACK);
        DataCollectorService.startPhoneAcceleometer(this);
        DataCollectorService.startPhoneGyroscope(this);
        DataSenderService.startDataSenderService(MainActivity.this, stepsHandler);
    }

    public void stopServices(){
        DataCollectorService.stopPhoneAcceleometer(this);
        DataCollectorService.stopPhoneGyroscope(this);
        DataSenderService.stoptDataSenderService(this);
    }

    CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(final long millisUntilFinished) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    exStatus.setText("Put device on the holster\n"+(millisUntilFinished/1000) + " seconds before start...");
                }
            });

        }

        @Override
        public void onFinish() {
            startServices();
        }
    };

    private class StepsHandler extends Handler{

        @Override
        public void handleMessage(Message msg){
            steps += msg.what;
            if(steps >= MAX_STEPS) {
                exStatus.setText("Experiment completed!\n"+steps+" total steps");
                //stopServices();
                final ToggleButton servicesBtn = (ToggleButton) findViewById(R.id.services_btn);
                servicesBtn.setChecked(false);
                stopServices();
                tone.startTone(ToneGenerator.TONE_PROP_BEEP2);
            }
            else exStatus.setText(steps+"/"+MAX_STEPS+" steps completed.");
        }

    }
}
