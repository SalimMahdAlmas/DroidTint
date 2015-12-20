package sahidalmas.xposed.droidtint;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class SetPerAppTint extends Activity {
    ColorPicker colorPicker;
    CheckBox useDefault;
    String pkg,Name;
    Button apply;


    SVBar svBar;
    SaturationBar saturationBar;
    OpacityBar opacityBar;
    ValueBar valueBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_tint_activity);

        colorPicker = (ColorPicker)findViewById(R.id.picker);
        saturationBar = (SaturationBar)findViewById(R.id.satru);
        opacityBar = (OpacityBar)findViewById(R.id.opacity);
        svBar = (SVBar)findViewById(R.id.sv);
        valueBar = (ValueBar)findViewById(R.id.value);

        colorPicker.addOpacityBar(opacityBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addSVBar(svBar);
        colorPicker.addValueBar(valueBar);

        useDefault = (CheckBox)findViewById(R.id.default_Tint_Per);
        Name = getIntent().getExtras().getString("name");
        pkg = getIntent().getExtras().getString("pkg");
        setTitle(Name);
        apply = (Button)findViewById(R.id.apply_tint);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (useDefault.isChecked()) {

                    Settings.System.putInt(getContentResolver(),pkg,7354);

                }else {
                    Settings.System.putInt(getContentResolver(),pkg,colorPicker.getColor());
                }


            }
        });
    }


}
