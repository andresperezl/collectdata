package edu.usf.csee.collectdata;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by andres on 2/26/15.
 */
public class DataSenderService extends IntentService {

    private final static String TAG = "DataSenderService";

    private static final String ACTION_START_SERVICE = "edu.usf.csee.collectdata.action.START_DATA_SENDER";
    private static final String ACTION_STOP_SERVICE = "edu.usf.csee.collectdata.action.STOP_DATA_SENDER";
    private static ScheduledThreadPoolExecutor stpe;
    /**
     * Starts this service to start sending data to the server.
     *
     * @see IntentService
     */
    public static void startDataSenderService(Context context) {
        Intent intent = new Intent(context, DataSenderService.class);
        intent.setAction(ACTION_START_SERVICE);
        context.startService(intent);
    }

    /**
     * Starts this service to stop sending data to the server.
     *
     * @see IntentService
     */
    public static void stoptDataSenderService(Context context) {
        Intent intent = new Intent(context, DataSenderService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        context.startService(intent);
    }

    public DataSenderService() {
        super("DataSenderService");
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_SERVICE.equals(action)) {
                stpe = new ScheduledThreadPoolExecutor(2);
                stpe.scheduleWithFixedDelay(sendDataThread, 3, 3, TimeUnit.SECONDS);
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                stpe.shutdown();
            }
        }
    }


    private static final Thread sendDataThread = new Thread(new Runnable() {
        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            boolean send = false;
            try {
                if(DataCollectorService.phoneAccArray.size() > 0) {
                    jsonObject.accumulate("phone_acc", DataCollectorService.phoneAccArray.toJSONArrayAndClear());
                    send = true;
                }
                if(DataCollectorService.phoneGyrArray.size() > 0) {
                    jsonObject.accumulate("phone_gyr", DataCollectorService.phoneGyrArray.toJSONArrayAndClear());
                    send = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(send) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        MainActivity.serverIp + "/data", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, "Data was sent successfully");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
                MainActivity.requestQueue.add(jsonObjectRequest);
            }
        }
    });
}
