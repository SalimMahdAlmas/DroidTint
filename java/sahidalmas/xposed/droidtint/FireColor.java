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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

public class FireColor {

    private static String LAUNCH_COMMAND = "droidtint";


    public static void postResult(Activity activity) {

        try {


            Bitmap bitmap = Utils.transferViewIntoBitmap(activity.getWindow().getDecorView());
            Rect rect = Utils.getRectFromView(activity.getWindow().getDecorView());
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, rect.top, bitmap.getWidth(), rect.top);
            int raw_color = Utils.grabColorFromBitmap(bitmap1);
            int darkO = Utils.toDarker(raw_color);
            postColor(darkO, activity);
        }catch (Exception e) {
            postColor(Color.TRANSPARENT,activity);
        }




    }
    public static void postColor(int color,Activity activity) {

        Intent intent = new Intent(LAUNCH_COMMAND);
        if (color == Color.TRANSPARENT) {
            color = Utils.toDarker(color);
        }
        intent.putExtra("object", color);
        activity.sendBroadcast(intent);
    }


}
