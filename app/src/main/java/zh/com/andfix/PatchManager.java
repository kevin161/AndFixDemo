package zh.com.andfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.zip.CheckedOutputStream;

/**
 * @version V1.0
 * @FileName: zh.com.andfix.PatchManager.java
 * @author: ZhaoHao
 * @date: 2016-11-03 10:30
 */
public class PatchManager {

    private static final String TAG = "PatchManager";
    private Context context;
    AndFixManager andFixManager;
    File src;

    public PatchManager(Context context){
        this.context = context;
        init();
    }

    private void init(){
        andFixManager = new AndFixManager(context);
    }

    public void loadPatch(String patch){
        src=new File(patch);
        Patch patch1 = new Patch(src,context);
        loadPatch(patch1);
    }

    public void loadPatch(Patch patch) {
        ClassLoader classLoader = context.getClassLoader();
        List<String> list;
        for (String name:patch.getPatchNames()){
            list = patch.getClasses(name);
            andFixManager.fix(src,classLoader,list);
        }

    }

}
