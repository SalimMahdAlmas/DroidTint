package sahidalmas.xposed.droidtint;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPref extends LinearLayout {

     SeekBar seekBar;
     TextView mTittle;
    public static String FACTOR_TINT_KEY = "factor_tint_k";

    public SeekBarPref(Context context) {
        super(context);
    }

    public SeekBarPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        inflate(getContext(),R.layout.seek_pref,this);
        seekBar = (SeekBar)findViewById(R.id.tint_factor);
        mTittle = (TextView)findViewById(R.id.title_seekbar);
        seekBar.setProgress(Settings.System.getInt(getContext().getContentResolver(), FACTOR_TINT_KEY, 8));

        mTittle.setText("Set Darker Tint Factor");
        seekBar.setMax(9);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int progress = seekBar.getProgress();

                Settings.System.putInt(getContext().getContentResolver(), FACTOR_TINT_KEY, progress);
            }
        });
    }

}
