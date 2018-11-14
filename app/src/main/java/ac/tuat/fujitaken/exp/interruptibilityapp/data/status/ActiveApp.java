package ac.tuat.fujitaken.exp.interruptibilityapp.data.status;

//import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ac.tuat.fujitaken.exp.interruptibilityapp.Log;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.base.Data;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.base.StringData;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.receiver.DataReceiver;

/**
 * s 追加：アプリの切り替えを検出する（シンプル）
 * Created by hi on 2018/11/08.
 */
public class ActiveApp {
    public static final int APP_SWITCH = 1 << 9;
    private static String prevActiveAppName = "";  //s 前回アクティブだったアプリの名前
    private static Set<String> ignoreAppPatternSet = new HashSet<String>() {  //s アプリ切替に含めないアプリ の 正規表現 の集合
        {
            add(".*設定");
            add(".*操作記録.*");  //s 動的に変更出来たらいいけどめんどくさいので静的
            add("ZenUI Launcher");
            //add(".*ホーム");
            add(".*System.*");
        }
    };

    //s アクティブなアプリが変わったか判定をする
    //s InterruptTiming.run() から定期的に呼ばれる：引数で渡されるのは mAllData.getData
    //s 返り値は イベントのフラグの アプリ切替 に関連するビット
    public static int judge(Map<String, Data> data){
        String nowActiveAppName = ( (StringData)data.get(DataReceiver.APPLICATION) ).value;  //s 現在アクティブなアプリ
        //Log.d("ActiveApp", "Active: " + nowActiveAppName);

        //s 今回アクティブなアプリがアプリ切替として扱いたくないアプリだった場合は、アプリの切り替わりを無視する
        for (String str : ignoreAppPatternSet) {
            if (nowActiveAppName.matches(str)) {
                nowActiveAppName = prevActiveAppName;
            }
        }

        //s 今回のアクティブなアプリが前回のと違うかを判定
        boolean appSwitched = !nowActiveAppName.equals(prevActiveAppName);

        int ret = 0;
        if (appSwitched && prevActiveAppName.length() > 0) {  //s アプリ切替があった＆前回情報が空ではない
            ret = APP_SWITCH;
        }

        //s 前回情報を更新
        prevActiveAppName = nowActiveAppName;

        return ret;
    }
}