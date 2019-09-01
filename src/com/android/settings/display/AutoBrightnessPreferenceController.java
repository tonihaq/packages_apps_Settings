/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import com.android.settings.search.DatabaseIndexingUtils;
import com.android.settings.search.InlineSwitchPayload;
import com.android.settings.search.ResultPayload;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.R;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

public class AutoBrightnessPreferenceController extends TogglePreferenceController {

    private final String SYSTEM_KEY = SCREEN_BRIGHTNESS_MODE;
    private final int DEFAULT_VALUE = SCREEN_BRIGHTNESS_MODE_MANUAL;

    private static final String KEY_AUTO_BRIGHTNESS = "auto_brightness";

    public AutoBrightnessPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_AUTO_BRIGHTNESS;
    }

    @Override
    public boolean isAvailable() {
        return mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_automatic_brightness_available)
                ? AVAILABLE
                : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void updateState(Preference preference) {
        int value = Settings.Secure.getInt(
                mContext.getContentResolver(), SYSTEM_KEY, DEFAULT_VALUE);
        ((SwitchPreference) preference).setChecked(value != DEFAULT_VALUE);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value = (Boolean) newValue;
        Settings.Secure.putInt(
                mContext.getContentResolver(), SYSTEM_KEY, value ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : DEFAULT_VALUE);
        return true;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "auto_brightness");
    }

    @Override
    public ResultPayload getResultPayload() {
        // TODO remove result payload
        final Intent intent = DatabaseIndexingUtils.buildSearchResultPageIntent(mContext,
                DisplaySettings.class.getName(), getPreferenceKey(),
                mContext.getString(R.string.display_settings));

        return new InlineSwitchPayload(SYSTEM_KEY,
                ResultPayload.SettingsSource.SYSTEM, SCREEN_BRIGHTNESS_MODE_AUTOMATIC, intent,
                isAvailable(), DEFAULT_VALUE);
    }
}
