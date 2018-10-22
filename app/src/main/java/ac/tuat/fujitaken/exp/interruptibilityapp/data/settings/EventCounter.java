package ac.tuat.fujitaken.exp.interruptibilityapp.data.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ac.tuat.fujitaken.exp.interruptibilityapp.data.status.Notify;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.status.PC;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.status.Screen;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.status.Walking;

/**
 * イベントの検出回数・回答の回数を記録するクラス
 * Created by Kyouhei on 2015/12/03.
 */
public class EventCounter {

    private static final String
            WALK_START = "WALK_START",
            WALK_STOP = "WALK_STOP",

            SELF_SCREEN_ON = "SELF_SCREEN_ON",
            NOTIFICATION_ON = "NOTIFICATION_ON",
            SELF_SCREEN_OFF = "SELF_SCREEN_OFF",
            NOTIFICATION_OFF = "NOTIFICATION_OFF",

            WALK_TO_PC = "WALK_TO_PC",
            PC_TO_WALK = "PC_TO_WALK",

            SP_TO_PC_BY_SELF = "SP_TO_PC_BY_SELF",
            SP_TO_PC_BY_NOTE = "SP_TO_PC_BY_NOTE",
            PC_TO_SP_BY_SELF = "PC_TO_SP_BY_SELF",
            PC_TO_SP_BY_NOTE = "PC_TO_SP_BY_NOTE";

    public static final int
            WALK_START_FLAG = Walking.WALK_START,
            WALK_STOP_FLAG = Walking.WALK_STOP,

            SELF_SCREEN_ON_FLAG = Screen.SCREEN_ON,
            NOTIFICATION_ON_FLAG = Screen.SCREEN_ON | Notify.NOTIFICATION,
            SELF_SCREEN_OFF_FLAG = Screen.SCREEN_OFF,
            NOTIFICATION_OFF_FLAG = Screen.SCREEN_OFF | Notify.NOTIFICATION,

            PC_TO_WALK_FLAG = Walking.WALK_START | PC.FROM_PC,
            WALK_TO_PC_FLAG = Walking.WALK_STOP | PC.FROM_PC,

            PC_TO_SP_BY_SELF_FLAG = Screen.SCREEN_ON | PC.FROM_PC,
            PC_TO_SP_BY_NOTE_FLAG = Screen.SCREEN_ON | Notify.NOTIFICATION | PC.FROM_PC,
            SP_TO_PC_BY_SELF_FLAG = Screen.SCREEN_OFF | PC.FROM_PC,
            SP_TO_PC_BY_NOTE_FLAG = Screen.SCREEN_OFF | Notify.NOTIFICATION | PC.FROM_PC;

    private static final HashMap<Integer, String> EVENT_KEYS_FROM_FLAGS = new HashMap<Integer, String>(){
        {
            put(WALK_START_FLAG, WALK_START);
            put(WALK_STOP_FLAG, WALK_STOP);

            put(SELF_SCREEN_ON_FLAG, SELF_SCREEN_ON);
            put(NOTIFICATION_ON_FLAG, NOTIFICATION_ON);
            put(SELF_SCREEN_OFF_FLAG, SELF_SCREEN_OFF);
            put(NOTIFICATION_OFF_FLAG, NOTIFICATION_OFF);

            put(WALK_TO_PC_FLAG, WALK_TO_PC);
            put(PC_TO_WALK_FLAG, PC_TO_WALK);

            put(SP_TO_PC_BY_SELF_FLAG, SP_TO_PC_BY_SELF);
            put(SP_TO_PC_BY_NOTE_FLAG, SP_TO_PC_BY_NOTE);
            put(PC_TO_SP_BY_SELF_FLAG, PC_TO_SP_BY_SELF);
            put(PC_TO_SP_BY_NOTE_FLAG, PC_TO_SP_BY_NOTE);
        }
    };

    private Map<Integer, Integer> evaluations;

    private SharedPreferences preferences;

    private int min = 0;
    private double mean = 0;

    /**
     * 記録済みのデータが有る場合は読み込み，無ければ初期化
     * @param context a
     */
    @SuppressLint("UseSparseArrays")
    EventCounter(Context context){

        evaluations = new HashMap<>();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for(Map.Entry<Integer, String> entry: EVENT_KEYS_FROM_FLAGS.entrySet()){
            evaluations.put(entry.getKey(), preferences.getInt(entry.getValue(), 0));
            Log.d("EVENTS_FLAG", entry.getValue() + " is " + Integer.toBinaryString(entry.getKey()) + " ( " + evaluations.get(entry.getKey()) + " ) ");
        }
        calcEvaluation();
    }

    public Integer getEvaluations(String e){

        for (Map.Entry<Integer, String> set: EVENT_KEYS_FROM_FLAGS.entrySet()){
            String value = set.getValue();
            if(value.equals(e)){
                return evaluations.get(set.getKey());
            }
        }
        return null;
    }

    public Integer getEvaluations(int e){
        return evaluations.get(e);
    }

    public void addEvaluations(int e){
        Integer t = evaluations.get(e);
        if(t == null ){
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        evaluations.put(e, t+1);
        editor.putInt(EVENT_KEYS_FROM_FLAGS.get(e), t+1);
        editor.apply();
        calcEvaluation();
    }

    public int getEvaluationMin() {
        return min;
    }

    public int getEvaluationMin(int[] events) {
        min = evaluations.get(events[0]);
        for(int e: events){
            int value = evaluations.get(e);
            Log.d("event", Integer.toBinaryString(e) + " is " + value);
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public double getEvaluationMean() {
        return mean;
    }

    private void calcEvaluation(){
        min = evaluations.get(WALK_START_FLAG);
        mean = 0;
        int cnt = 0;
        for(Integer value: evaluations.values()){
            cnt++;
            mean += value;
            if (value < min) {
                min = value;
            }
        }
        mean /= cnt;
    }

    public Map<String, Integer> getEvaluations() {
        //noinspection unchecked
        Map<String, Integer> ret = new TreeMap();
        for(Map.Entry<Integer, Integer> entry: evaluations.entrySet()){
            ret.put(EVENT_KEYS_FROM_FLAGS.get(entry.getKey()), entry.getValue());
        }
        return ret;
    }

    public void putEvaluation(String e, int c){
        int event = 0;

        for (Map.Entry<Integer, String> entry: EVENT_KEYS_FROM_FLAGS.entrySet()){
            String value = entry.getValue();
            if(value.equals(e)){
                event = entry.getKey();
                break;
            }
        }
        if(evaluations.get(event) == null){
            return;
        }
        evaluations.put(event, c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(e, c);
        editor.apply();
        calcEvaluation();
    }

    public String getEventName(int event){
        String ret = EVENT_KEYS_FROM_FLAGS.get(event);
        ret = (ret == null)? "": ret;
        return ret;
    }

    public void initialize(){
        SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<Integer, String> set: EVENT_KEYS_FROM_FLAGS.entrySet()){
            evaluations.put(set.getKey(), 0);
            editor.putInt(set.getValue(), 0);
            editor.apply();
        }
        editor.apply();
    }
}