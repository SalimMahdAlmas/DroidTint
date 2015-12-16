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

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import java.io.ByteArrayOutputStream;

public class Utils {
    /**
     * This method grab the highest used color from bitmap
     * @param bitmap Bitmap you want to get color
     * @return Return the color of bitmap
     */
    public static int grabColorFromBitmap(Bitmap bitmap) {
        int color = 0;
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new ByteArrayOutputStream());
            color = bitmap.getPixel(bitmap.getWidth() / 2, 1);
        }catch (Exception a) {
            a.printStackTrace();
        }
        return color;
    }
    /**
     * This method make view into bitmap using drawing cache
     * @param view The view you want to transfer
     * @return Bitmap based on the view
     */
    public static synchronized Bitmap transferViewIntoBitmap(View view) {
        Bitmap result;

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        result = view.getDrawingCache();


        return result;
    }
    /**
     * Get rect from view
     * @param view The view from which you want Rect
     * @return Rect from the view
     */
    public static Rect getRectFromView(View view) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        return rect;
    }
    public static int toDarker(int colorZ) {
        float[] hsv = new float[3];
        int color = colorZ;
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        color = android.graphics.Color.HSVToColor(hsv);
        return color;

    }
    public static boolean isKeyguardLocked(Context context) {
        KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean keyguardLocked;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            keyguardLocked = kgm.isKeyguardLocked();
        } else {
            keyguardLocked = kgm.inKeyguardRestrictedInputMode();
        }
        return keyguardLocked;
    }



}