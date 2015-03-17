package edu.usf.csee.collectdata;


import java.util.ArrayList;


public class StepEventDetector {

    public final static int WINDOW_SAMPLES = 5;
    public final static int SEARCH_WINDOW = WINDOW_SAMPLES * 2;
    private int counter = SEARCH_WINDOW;
    public final static double THRESHOLD = 0.45;
    private static StepEventDetector ourInstance = new StepEventDetector();
    private boolean look_fw = false;
    private boolean prev_high = false;

    public static StepEventDetector getInstance() {
        return ourInstance;
    }


    public int getEvents(TrioArray data) {
        ArrayList<Double> energy = new ArrayList<>();
        Trio biasTrio = new Trio();
        for (Trio sample : data) {
            energy.add(sample.energy());
            biasTrio.addition(sample);
        }

        biasTrio.division(energy.size());

        double bias = biasTrio.energy();

        int steps = 0;

        for (int i = 0; i < energy.size(); i++) {
            double sum = 0;
            int qty = 0;
            for (int j = i - WINDOW_SAMPLES; j <= i + WINDOW_SAMPLES; j++) {
                if (j < 0) continue;
                if (j == energy.size()) break;
                sum += energy.get(j);
                qty++;
            }
            double mean = (sum / ((double) qty)) - bias;
            if (!look_fw && mean >= THRESHOLD) {
                prev_high = true;
                continue;
            } else if (look_fw) counter--;
            if (prev_high && mean < THRESHOLD) {
                prev_high = false;
                look_fw = true;
            }
            if (look_fw && mean >= THRESHOLD) {
                look_fw = false;
                prev_high = true;
                counter = SEARCH_WINDOW;
                continue;
            }
            if (look_fw && mean <= -THRESHOLD) {
                steps++;
                counter = SEARCH_WINDOW;
                look_fw = false;
                continue;
            }
            if (counter == 0) {
                prev_high = false;
                look_fw = false;
                counter = SEARCH_WINDOW;
            }
        }
        return steps;
    }


}
