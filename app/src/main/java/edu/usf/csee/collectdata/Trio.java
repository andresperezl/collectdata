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

    public Trio(){
        x = 0;
        y = 0;
        z = 0;
    }

    public void addition(Trio t){
        x += t.x;
        y += t.y;
        z += t.z;
    }

    public void division (int i) {
        this.x /= i;
        this.y /= i;
        this.z /= i;
    }

    public double energy(){
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
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
