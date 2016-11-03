package zh.com.andfix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alipay.euler.andfix.annotation.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @version V1.0
 * @FileName: zh.com.andfix.AndFixManager.java
 * @author: ZhaoHao
 * @date: 2016-11-03 11:04
 */
public class AndFixManager {
    private static final String TAG = "AndFixManager";
    private Context context;
    private File optFile;

    public AndFixManager(Context context) {
        this.context = context;
        HandlerNative.init(Build.VERSION.SDK_INT);
    }

    public void fix(File file, final ClassLoader classLoader, List<String> list) {
        optFile = new File(context.getFilesDir(), file.getName());
//        if (optFile.exists()){
//            optFile.delete();
//        }

        try {
            final DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);
            //为了不加载出已经出错的class
            ClassLoader classLoader1 = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    Class clazz = dexFile.loadClass(name, this);
                    if (clazz == null) {
                        clazz = Class.forName(name);
                    }
                    return clazz;
                }
            };

            Enumeration<String> entry = dexFile.entries();
            while (entry.hasMoreElements()) {
                String key = entry.nextElement();
                if (!list.contains(key)) {
                    continue;
                }
                Class realClass = dexFile.loadClass(key, classLoader1);
                if (realClass != null) {
                    fixClass(realClass, classLoader);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fixClass(Class realClazz, ClassLoader classLoader) {
        Method[] methods = realClazz.getMethods();
        for (Method needMethod : methods) {
            MethodReplace methodReplace = needMethod.getAnnotation(MethodReplace.class);
            if (methodReplace == null) {
                continue;
            }
            Log.i(TAG, "找到替换方法   " + methodReplace.toString() + "  clazz 对象  " + realClazz.toString());
            String clazz = methodReplace.clazz();
            String methodName = methodReplace.method();
            replaceMethod(classLoader, clazz, methodName, realClazz, needMethod);
        }
    }

    private void replaceMethod(ClassLoader classLoader, String clazz, String methodName, Class realClazz, Method method) {

        try {
            Class srcClazz = Class.forName(clazz);
            if (srcClazz != null) {
                Method src = srcClazz.getDeclaredMethod(methodName, method.getParameterTypes());
                HandlerNative.replaceMethod(src, method);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
