package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
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
        log(LOG.DEBUG, "Hooking part of system");
        log(LOG.INFO, "Module Path " + startupParam.modulePath)
        ;
        mStatusBarView = null;
        handleActivity(null);



    }

    private void handleActivity(final ClassLoader classLoader) {


        assert classLoader != null;


        XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Activity activity = (Activity) param.thisObject;
                        boolean hasFocus = (Boolean) param.args[0];
                        if (hasFocus) {
                            FireColor.postResult(activity);
                        }

                    }
                });

         XposedHelpers.findAndHookMethod(Activity.class, "finish", new XC_MethodHook() {
             @Override
             protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                 Activity activity = (Activity) param.thisObject;
                 FireColor.postColor(0,activity);
             }
         });



        XposedHelpers.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                FireColor.postResult((Activity) param.thisObject);
            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onPostResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                FireColor.postResult((Activity) param.thisObject);
            }
        });

        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                FireColor.postResult((Activity) param.thisObject);
            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onStart", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                FireColor.postResult((Activity) param.thisObject);
            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "dispatchKeyEvent", KeyEvent.class ,new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                FireColor.postResult((Activity) param.thisObject);
            }
        });



    }

    public enum LOG {
        INFO,DEBUG,ERROR
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equalsIgnoreCase("com.android.systemui")) {
            try {


                handleStatusBar(loadPackageParam.classLoader);

            }catch (Exception e) {

                XposedBridge.log(e.getCause());
            }
        }
        if (loadPackageParam.packageName.equalsIgnoreCase("android")) {
            try {



                handleActivity(loadPackageParam.classLoader);


            }catch (Exception e) {

                XposedBridge.log(e.getCause());
            }
        }



    }

    private void handleStatusBar(ClassLoader classLoader) {
        Class<?> PhoneStatusBarView;
        try {
            PhoneStatusBarView = XposedHelpers.findClass("com.android.systemui.statusbar.phone.PhoneStatusBarView", classLoader);
        }catch (XposedHelpers.ClassNotFoundError classNotFoundError) {
           PhoneStatusBarView =  XposedHelpers.findClass("com.android.systemui.statusbar.StatusBarView", classLoader);
        }

        XposedBridge.hookAllConstructors(PhoneStatusBarView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mStatusBarView = (View) param.thisObject;

                log(LOG.DEBUG,"Hooked StatusBar");
                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intentz) {
                        int color = intentz.getExtras().getInt("object");


                     boolean enable_dark = true;

                        boolean nullSys = color == 4573;

                        int code = Settings.System.getInt(mStatusBarView.getContext().getContentResolver(),MainActivity.DARKER_TINT_KEY,0);
                        if (code == 1) {
                            enable_dark = false;
                        }
                      int factor =   Settings.System.getInt(mStatusBarView.getContext().getContentResolver(), SeekBarPref.FACTOR_TINT_KEY, 10);

                        if (!nullSys ) {
                            if (enable_dark) {


                                if (factor > 10) {
                                    factor = 9;
                                }

                                int colorZ = FireColor.darkenColor(color, FireColor.strip(factor));


                                setViewBackground(mStatusBarView, new ColorDrawable(colorZ));
                            } else {
                                setViewBackground(mStatusBarView, new ColorDrawable(color));

                            }

                        }else {
                            setViewBackground(mStatusBarView,null);
                        }


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