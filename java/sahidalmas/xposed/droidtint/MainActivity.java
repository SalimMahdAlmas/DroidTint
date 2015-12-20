package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;


public class MainActivity extends PreferenceActivity {


    private CheckBoxPreference mDarker;
    public static String DARKER_TINT_KEY = "dark_t";

    public boolean sDarkerTint = false;
    private Activity mActivity;
     Preference perApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);



        mDarker = (CheckBoxPreference)findPreference("darker_tint");
        mDarker.setSummaryOn("Enable");
        mDarker.setSummaryOff("Disable");

        this.mActivity = this;
        int code = Settings.System.getInt(getContentResolver(),DARKER_TINT_KEY,1);
        if (code == 0) {
            sDarkerTint = true;
        }


        findPreference("darker_tint_factor").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FireColor.postResult(mActivity);
                return true;
            }
        });


        mDarker.setChecked(sDarkerTint);
        mDarker.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int a;
                if (mDarker.isChecked()) {
                    a = 0;
                } else {
                    a = 1;
                }

                Settings.System.putInt(getContentResolver(), DARKER_TINT_KEY, a);
                FireColor.postResult(mActivity);

                return true;
            }
        });

        perApp = findPreference("per_app");
        perApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), PerAppTintList.class);
                startActivity(intent);
                return true;
            }
        });


    }
}