/*
 * Copyright (C) 2015 AndroidFire
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Sahid Almas
 *
 */
package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.provider.Settings;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FireColor {


    public static int NULL_COLOR = 4573;
    public static void postResult(Activity activity) {



        int color = Settings.System.getInt(activity.getContentResolver(),activity.getApplicationInfo().packageName,7354);
        if (color != 7354) {
            postColor(color,activity);
        }else {

            if (activity.getPackageName().equalsIgnoreCase("android") || activity.getPackageName().equalsIgnoreCase("com.android.systemui") || checkLauncher(activity.getPackageName(),activity)) {
                postColor(NULL_COLOR,activity);
            }else {
                tintIt(activity);
            }
        }

    }




    private static void tintIt(final Activity activity) {


            try {
                final Rect rect = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                final int top = rect.top;
                final View decorView = activity.getWindow().getDecorView();
                decorView.setDrawingCacheEnabled(true);
                final Bitmap drawingCache = decorView.getDrawingCache();

                assert drawingCache != null;
                final Bitmap bitmap = Bitmap.createBitmap(drawingCache, 0, top, drawingCache.getWidth(), top);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new ByteArrayOutputStream());
                postColor(bitmap.getPixel(bitmap.getWidth() / 2, 1), activity);
            } catch (Exception ex) {
                //  return -16711681;
                postColor(Color.BLACK, activity);
            }

    }


    public static void postColor(int color,Activity activity) {

        String LAUNCH_COMMAND = "droidtint";
        Intent intent = new Intent(LAUNCH_COMMAND);

       // color = toDarker(color);

        intent.putExtra("object", color);
        activity.sendBroadcast(intent);
    }
    public static int darkenColor(int color, float value) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= value; // value component

        return Color.HSVToColor(hsv);
    }
    public static float strip(int nu) {
        String result = "";

        // 8



        if (nu < 10) {
            result += ".";
            result += nu;
        }else {
            result = "0.9";
        }

        return Float.parseFloat(result);
    }
    private static List<String> getLaunchers(Activity activity) {
        List<String> arr = new ArrayList<>();
        PackageManager pm = activity.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
        for (ResolveInfo resolveInfo : lst) {
           // Log.d("Test", "New Launcher Found: " + resolveInfo.activityInfo.packageName);
            arr.add(resolveInfo.activityInfo.packageName);
        }
        return arr;

    }
    public static boolean checkLauncher(String packageName,Activity activity) {
        boolean result = false;
        for (String s : getLaunchers(activity) ) {
            if (packageName.equalsIgnoreCase(s)) {
                result = true;
                break;
            }
        }
        return result;
    }

}
