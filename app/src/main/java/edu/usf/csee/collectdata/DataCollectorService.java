package edu.usf.csee.collectdata;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class DataCollectorService extends IntentService implements SensorEventListener {

    private static final String TAG = "DataCollectorService";

    private static final String ACTION_START_PHONE_ACC = "edu.usf.csee.collectdata.action.START_PHONE_ACC";
    private static final String ACTION_START_PHONE_GYR = "edu.usf.csee.collectdata.action.START_PHONE_GYR";
    private static final String ACTION_STOP_PHONE_ACC = "edu.usf.csee.collectdata.action.STOP_PHONE_ACC";
    private static final String ACTION_STOP_PHONE_GYR = "edu.usf.csee.collectdata.action.STOP_PHONE_GYR";

    public static final TrioArray phoneAccArray = new TrioArray();
    public static final TrioArray phoneGyrArray = new TrioArray();

    public static boolean listenPhoneAcc = false;
    public static boolean listenPhoneGyr = false;

    private static DataCollectorService phoneAccHandler;
    private static DataCollectorService phoneGyrHandler;

    private static SensorManager sensorManager;
    /**
     * Starts this service to start listening to Accelerometer data from the phone.
     *
     * @see IntentService
     */
    public static void startPhoneAcceleometer(Context context) {
        Intent intent = new Intent(context, DataCollectorService.class);
        intent.setAction(ACTION_START_PHONE_ACC);
        context.startService(intent);
    }

    /**
     * Starts this service to start listening to Gyroscope data from the phone.
     *
     * @see IntentService
     */
    public static void startPhoneGyroscope(Context context) {
        Intent intent = new Intent(context, DataCollectorService.class);
        intent.setAction(ACTION_START_PHONE_GYR);
        context.startService(intent);
    }

    /**
     * Starts this service to start listening to Accelerometer data from the phone.
     *
     * @see IntentService
     */
    public static void stopPhoneAcceleometer(Context context) {
        Intent intent = new Intent(context, DataCollectorService.class);
        intent.setAction(ACTION_STOP_PHONE_ACC);
        context.startService(intent);
    }

    /**
     * Starts this service to start listening to Gyroscope data from the phone.
     *
     * @see IntentService
     */
    public static void stopPhoneGyroscope(Context context) {
        Intent intent = new Intent(context, DataCollectorService.class);
        intent.setAction(ACTION_STOP_PHONE_GYR);
        context.startService(intent);
    }

    public DataCollectorService() {
        super("DataCollectorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if(sensorManager == null)
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            final String action = intent.getAction();
            if (ACTION_START_PHONE_ACC.equals(action)) {
                phoneAccArray.clear();
                Log.d(TAG, "Registering Accelerometer");
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_GAME);
                listenPhoneAcc = true;
            } else if (ACTION_START_PHONE_GYR.equals(action)) {
                phoneGyrArray.clear();
                Log.d(TAG, "Registering Accelerometer");
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_GAME);
                listenPhoneGyr = true;
            } else if (ACTION_STOP_PHONE_ACC.equals(action)){
                Log.d(TAG, "Unregistering Accelerometer");
                listenPhoneAcc = false;
            } else if (ACTION_STOP_PHONE_GYR.equals(action)){
                Log.d(TAG, "Unregistering Gyroscope");
                listenPhoneGyr = false;
            }
        }
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link android.hardware.SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link android.hardware.SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link android.hardware.SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                //Log.d(TAG, "Accelerometer Event received");
                synchronized (phoneAccArray) {
                    phoneAccArray.addValues(MainActivity.experimentId,
                            event.values, event.timestamp);
                    if(!listenPhoneAcc)
                        sensorManager.unregisterListener(this,
                                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                //Log.d(TAG, "Gyroscope Event received");
                synchronized (phoneGyrArray) {
                    phoneGyrArray.addValues(MainActivity.experimentId,
                            event.values, event.timestamp);
                    if(!listenPhoneGyr)
                        sensorManager.unregisterListener(this,
                                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
                }
                break;
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
