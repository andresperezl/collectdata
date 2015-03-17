package edu.usf.csee.collectdata;

import org.json.JSONArray;

import java.util.ArrayList;

public class TrioArray extends ArrayList<Trio> {

    public static final StepEventDetector stepEventDetector = StepEventDetector.getInstance();

    protected JSONArray toJSONArray(){
        JSONArray jsonArray = new JSONArray();
        for(Trio t : this)
            jsonArray.put(t.toJSONObject());
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

    public synchronized int countSteps(){
        return stepEventDetector.getEvents(this);
    }

}
