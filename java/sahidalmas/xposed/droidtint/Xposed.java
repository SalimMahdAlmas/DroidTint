package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Xposed implements IXposedHookLoadPackage,IXposedHookZygoteInit {
    public static String TAG_NAME = "DroidTint";
    private static View mStatusBarView;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        mStatusBarView = null;
        handleActivity(null);
    }

    private void handleActivity(ClassLoader classLoader) {
        final Class<?> classActivity = XposedHelpers.findClass("android.app.Activity", classLoader);



        Method focusChanged = XposedHelpers.findMethodBestMatch(classActivity, "onWindowFocusChanged", Boolean.class);

        XposedBridge.hookMethod(focusChanged, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                FireColor.postResult((Activity) param.thisObject);

            }
        });

        Method onResume = XposedHelpers.findMethodBestMatch(classActivity,"onResume");
        XposedBridge.hookMethod(onResume, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                log(LOG.INFO, "onResume() called from "+activity.getPackageName());
            }
        });

        Method onPause = XposedHelpers.findMethodBestMatch(classActivity,"onPause");
        XposedBridge.hookMethod(onPause, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                log(LOG.INFO, "onPause() called from " + activity.getPackageName());
            }
        });

        Method onStop = XposedHelpers.findMethodBestMatch(classActivity,"onStop");
        XposedBridge.hookMethod(onStop, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                log(LOG.INFO, "onStop() called from " + activity.getPackageName());
            }
        });

    }

    public enum LOG {
        INFO,DEBUG,ERROR
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        handleStatusBar(loadPackageParam.classLoader);
        handleActivity(loadPackageParam.classLoader);

    }

    private void handleStatusBar(ClassLoader classLoader) {
        Class<?> PhoneStatusBarView = XposedHelpers.findClass("com.android.systemui.statusbar.phone.PhoneStatusBarView", classLoader);

        XposedBridge.hookAllConstructors(PhoneStatusBarView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mStatusBarView = (View) param.thisObject;

                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intentz) {
                        int color = intentz.getExtras().getInt("object");

                        setViewBackground(mStatusBarView,new ColorDrawable(color));


                    }
                };

                mStatusBarView.getContext().registerReceiver(receiver,new IntentFilter("droidtint"));
            }
        });

    }

    public static void log(LOG log,String message ) {
        String output;
        switch (log) {
            case INFO:
                output = "I/"+TAG_NAME+"{"+message+"}";
                break;
            case DEBUG:
                output = "D/"+TAG_NAME+"{"+message+"}";
                break;
            case ERROR:
                output = "E/"+TAG_NAME+"{"+message+"}";
                break;
            default:
                output = message;
        }
        XposedBridge.log(output);
    }
    public static void setViewBackground(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
