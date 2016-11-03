#include <jni.h>
#include "dalvik.h"
#include <dlfcn.h>
//#include "common.h"

extern "C"
{

JNIEXPORT void JNICALL
Java_zh_com_andfix_HandlerNative_init(JNIEnv *env, jclass type, jint api) {
    void *handle=dlopen("libdvm.so",RTLD_NOW);

    if(handle)
    {
        const char *name = api > 10 ? "_Z20dvmDecodeIndirectRefP6ThreadP8_jobject" :
                           "dvmDecodeIndirectRef";
        dvmDecodeIndirectRef_fnPtr = (dvmDecodeIndirectRef_func) dlsym(handle, name);

        dvmThreadSelf_fnPtr = (dvmThreadSelf_func) dlsym(handle, api > 10 ? "_Z13dvmThreadSelfv"
                                                                          : "dvmThreadSelf");
        jclass clazz = env->FindClass("java/lang/reflect/Method");
        jClassMethod = env->GetMethodID(clazz,"getDeclaringClass","()Ljava/lang/Class;");

    }

}

JNIEXPORT void JNICALL
Java_zh_com_andfix_HandlerNative_replaceMethod(JNIEnv *env, jclass type, jobject src,
                                               jobject dest) {

    jobject clazz= env->CallObjectMethod(dest,jClassMethod);
    ClassObject *clz = (ClassObject *) dvmDecodeIndirectRef_fnPtr(dvmThreadSelf_fnPtr(), clazz);
    clz->status = CLASS_INITIALIZED;
    Method *meth = (Method *) env->FromReflectedMethod(src);
    Method *target= (Method *) env->FromReflectedMethod(dest);

    meth->clazz = target->clazz;
    meth->accessFlags = target->accessFlags;
    meth->methodIndex = target->methodIndex;
    meth->jniArgInfo = target->jniArgInfo;
    meth->registersSize = target->registersSize;
    meth->outsSize = target->outsSize;
    meth->insns = target->insns;
    meth->insSize = target->insSize;
    meth->nativeFunc = target->nativeFunc;


}
}