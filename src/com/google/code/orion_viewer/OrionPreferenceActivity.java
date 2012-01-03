package com.google.code.orion_viewer;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.code.orion_viewer.device.NookDevice;

/**
 * User: mike
 * Date: 02.01.12
 * Time: 17:35
 */
public class OrionPreferenceActivity extends PreferenceActivity {

    private boolean isNook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isNook = Common.createDevice() instanceof NookDevice;
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
        if (isNook) {
            ImageButton button = (ImageButton) findViewById(R.id.preferences_close);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

//    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(isNook ? R.layout.nook_preferences : layoutResID);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
