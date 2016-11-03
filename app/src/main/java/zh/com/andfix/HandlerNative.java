package zh.com.andfix;

import java.lang.reflect.Method;

/**
 * @version V1.0
 * @FileName: zh.com.andfix.HandlerNative.java
 * @author: ZhaoHao
 * @date: 2016-11-03 10:37
 */
public class HandlerNative {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void init(int api);


    public  static  native  void replaceMethod(Method src, Method dest);
}
