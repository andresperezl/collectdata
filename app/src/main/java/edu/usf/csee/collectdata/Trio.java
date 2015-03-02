package edu.usf.csee.collectdata;

import org.json.JSONException;
import org.json.JSONObject;

public class Trio{
    public int id;
    public float x;
    public float y;
    public float z;
    public long ts;

    public Trio(int experiment, float[] values, long ts){
        id = experiment;
        x = values[0];
        y = values[1];
        z = values[2];
        this.ts = ts;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("experiment_id", id)
                    .putOpt("x", x)
                    .putOpt("y", y)
                    .putOpt("z", z)
                    .putOpt("ts", ts);
        } catch (JSONException ex){
            ex.printStackTrace();
        }
        return jsonObject;
    }
}
