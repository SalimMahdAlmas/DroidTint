package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PerAppTintList extends Activity {

    private LinearLayout mContent;
    private PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.per_app_tint_list);
        mContent  = (LinearLayout)findViewById(R.id.tint_list_content);
        new AppLoader().execute();
    }
    private ArrayList<ResolveInfo> getApps(){

        pm = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return (ArrayList<ResolveInfo>) pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
    }

    class AppLoader extends AsyncTask<Void,Void,Void> {

        class Application {
            public String APP_NAME,PACKAGE_NAME;
            public Drawable APP_ICON;
        }
        private ArrayList<Application> mApps = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... params) {
            for (ResolveInfo rInfo : getApps()) {


                Application newInfo = new Application();
                newInfo.APP_ICON = rInfo.loadIcon(pm);
                newInfo.APP_NAME = rInfo.loadLabel(pm).toString();
                newInfo.PACKAGE_NAME = rInfo.activityInfo.packageName;

                mApps.add(newInfo);

            }
            Collections.sort(mApps, new Comparator<Application>() {
                @Override
                public int compare(Application lhs, Application rhs) {
                    return lhs.APP_NAME.compareToIgnoreCase(rhs.APP_NAME);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.loadingSign).setVisibility(View.GONE);

                    for (final Application application : mApps ) {
                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.app_plate, null);

                        linearLayout.setPadding(15, 15, 15, 15);
                        TextView name = (TextView)linearLayout.findViewById(R.id.app_Title);
                        name.setText(application.APP_NAME);

                        TextView pkg = (TextView)linearLayout.findViewById(R.id.app_PackageName);
                        pkg.setText(application.PACKAGE_NAME);

                        ImageView imageView = (ImageView)linearLayout.findViewById(R.id.app_Icon);
                        imageView.setImageDrawable(application.APP_ICON);

                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(),SetPerAppTint.class);
                                intent.putExtra("pkg", application.PACKAGE_NAME);
                                intent.putExtra("name", application.APP_NAME);
                                startActivity(intent);
                            }
                        });
                        mContent.addView(linearLayout);
                    }
                }
            });
        }


    }


}
