package ac.tuat.fujitaken.exp.interruptibilityapp.data.save;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ac.tuat.fujitaken.exp.interruptibilityapp.LogEx;  //s 自作Log
import ac.tuat.fujitaken.exp.interruptibilityapp.data.settings.AppSettings;
import ac.tuat.fujitaken.exp.interruptibilityapp.data.settings.Settings;

/**
 * データを一定時間で保存するクラス
 * Created by hi on 2015/11/09.
 */
//s SaveData のインスタンスにどんどんデータを格納していって SaveTask で勝手に端末への記録がされるっぽい
//s データは RowData の形式で保持する
public class SaveData{

    private File file;      //保存ファイル
    protected List<RowData> data = new ArrayList<>();    //保存するデータ
    private String header, category;
    public boolean lock = false;  //s 名前変更：rock → lock　これがtrueだとsaveしないっぽい

    /**
     * コンストラクタ
     * @param category  ファイルのカテゴリー
     */
    //public SaveData(String s, String s1) {  //s コメントアウト
    public SaveData(String category, String header) {  //s 変更：引数名をわかりやすいのに
        this.header = header;
        this.category = category;
        updateFile(0);
    }

    private void updateFile(int number){
        AppSettings settings = Settings.getAppSettings();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();;
        if(settings.isSaveMode()){  //s SDカードに保存する設定が オン だった場合
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                String path = getSdCardFilesDirPathListForLollipop(Settings.getContext());
                if(!"".equals(path)){
                    externalStorageDirectory = new File(path);
                }
            }
        }

        do {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
            String fileName = sdf.format(System.currentTimeMillis()) + "_" + category + "_" + (number++);
            String fileName1 = externalStorageDirectory.getPath() + "/EventLog/" + fileName;
            file = new File(fileName1 + ".csv");
        }while(file.exists());
        LogEx.d("DirPath", file.getPath());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getSdCardFilesDirPathListForLollipop(Context context) {
        List<String> sdCardFilesDirPathList = new ArrayList<>();

        // getExternalFilesDirsはAndroid4.4から利用できるAPI。
        // filesディレクトリのリストを取得できる。
        File[] dirArr = context.getExternalFilesDirs(null);

        for (File dir : dirArr) {
            if (dir != null) {
                String path = dir.getAbsolutePath();

                // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
                // 取り外し可能かどうか（SDカードかどうか）を判定している。
                if (Environment.isExternalStorageRemovable(dir)) {

                    // 取り外し可能であればSDカード。
                    if (!sdCardFilesDirPathList.contains(path)) {
                        sdCardFilesDirPathList.add(path);
                        return path.substring(0, path.length() - 61);
                    }

                }
            }
        }
        return "";
    }


    public File getFile() {
        return file;
    }

    public String getHeader() {
        return "time," + header;
    }

    /**
     * データ行の追加
     * @param newLine   新しい行
     */
    //s InterruptTiming.run()→NotificationController.save(), MainService.onCreate()内のラムダ式, 自作Log.printLog() で呼ばれる
    public void addLine(RowData newLine){
        if(data != null) {
            data.add(newLine);
        }
    }

    public List<RowData> refresh(){
        List<RowData> temp = data;
        data = new ArrayList<>();
        return temp;
    }
}
