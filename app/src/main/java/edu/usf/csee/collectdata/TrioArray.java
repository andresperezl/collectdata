package edu.usf.csee.collectdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class TrioArray extends ArrayList<Trio> {

    protected JSONArray toJSONArray(){
        JSONArray jsonArray = new JSONArray();
        for(Iterator<Trio> it = iterator(); it.hasNext();)
            jsonArray.put(it.next().toJSONObject());
        return jsonArray;
    }

    public synchronized void addValues(int experiment, float[] values, long timestamp){
        add(new Trio(experiment, values, timestamp));
    }

    public synchronized JSONArray toJSONArrayAndClear(){
        JSONArray jsonArray = toJSONArray();
        clear();
        return jsonArray;
    }


}
