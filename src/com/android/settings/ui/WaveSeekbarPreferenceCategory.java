package com.android.settings.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.android.settings.R;
import com.android.settingslib.RestrictedPreference;

public class WaveSeekbarPreferenceCategory extends RestrictedPreference {

    public WaveSeekbarPreferenceCategory(Context context) {
        super(context);
        initViews(context);
    }

    public WaveSeekbarPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public WaveSeekbarPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(R.layout.wave_seekbar_preference_category);
    }
}
