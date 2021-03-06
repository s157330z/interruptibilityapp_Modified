package ac.tuat.fujitaken.exp.interruptibilityapp.data.base;

import java.io.Serializable;

/**
 * 記録データの形式を一般化するためのインタフェース
 * Created by hi on 2015/11/10.
 */
public abstract class Data implements Cloneable, Serializable {
    /**
     * インスタンスのコピーを作成する
     * @return  コピー
     */
    @Override
    public Data clone(){
        Data copy = null;
        try {
            copy = (Data) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return copy;
    }

    /**
     * データを文字列にして出力する
     * @return  データの文字列
     */
    public abstract String getString();
}
