package com.android.settings.ui;

import static com.android.settingslib.display.BrightnessUtils.GAMMA_SPACE_MAX;
import static com.android.settingslib.display.BrightnessUtils.convertLinearToGammaFloat;

import android.content.Context;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

public class BrightnessSeekbarPreferenceCategory extends WaveSeekbarPreferenceCategory
        implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "BrightnessSeekbarPreferenceCategory";

    private boolean isManuallyTouchingSeekbar;
    private CallbackBrightness mCallback;
    private Context mContext;

    private int mBrightness;
    private int mDefBrightness;
    private int mDefVrBrightness;
    private int mMaxBrightness;
    private int mMaxVrBrightness;
    private int mMinBrightness;
    private int mMinVrBrightness;

    private SeekBar mSeekBar;

    public interface CallbackBrightness {
        void onBrightValueChanged(int i, int i2);

        void onBrightValueStartTrackingTouch(int i);

        void saveBrightnessDataBase(int i);
    }

    public BrightnessSeekbarPreferenceCategory(Context context) {
        super(context);
        initView(context);
    }

    public BrightnessSeekbarPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BrightnessSeekbarPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    @VisibleForTesting
    IVrManager safeGetVrManager() {
        return IVrManager.Stub.asInterface(ServiceManager.getService(
                Context.VR_SERVICE));
    }

    @VisibleForTesting
    boolean isInVrMode() {
        IVrManager vrManager = safeGetVrManager();
        if (vrManager != null) {
            try {
                return vrManager.getVrModeState();
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to check vr mode!", e);
            }
        }
        return false;
    }

    private void initView(Context context) {
        mContext = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mMinBrightness = pm.getMinimumScreenBrightnessSetting();
        mMaxBrightness = pm.getMaximumScreenBrightnessSetting();
        mDefBrightness = pm.getDefaultScreenBrightnessSetting();
        mMinVrBrightness = pm.getMinimumScreenBrightnessForVrSetting();
        mMaxVrBrightness = pm.getMaximumScreenBrightnessForVrSetting();
        mDefVrBrightness = pm.getDefaultScreenBrightnessForVrSetting();

        if (isInVrMode()) {
            mBrightness = convertLinearToGammaFloat(Settings.System.getIntForUser(
                          mContentResolver, System.SCREEN_BRIGHTNESS_FOR_VR_FLOAT, mDefVrBrightness),
                          mMinVrBrightness, mMaxVrBrightness);
        } else {
            mBrightness = convertLinearToGammaFloat(Settings.System.getIntForUser(
                          mContentResolver, System.SCREEN_BRIGHTNESS_FLOAT, mDefBrightness),
                          mMinBrightness, mMaxBrightness);
        }

    }

    public void setCallback(CallbackBrightness callback) {
        mCallback = callback;
    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setMax(GAMMA_SPACE_MAX);
        mSeekBar.setProgress(mBrightness);
        mSeekBar.setOnSeekBarChangeListener(this);
        view.setDividerAllowedAbove(false);
    }

    public void setBrightness(int brightness) {
        mBrightness = brightness;
        notifyChanged();
    }

    public int getBrightness() {
        return mBrightness;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isManuallyTouchingSeekbar) {
            mCallback.onBrightValueChanged(0, mSeekBar.getProgress());
            mBrightness = mSeekBar.getProgress();
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isManuallyTouchingSeekbar = true;
        if (mCallback != null && mSeekBar != null) {
            mCallback.onBrightValueStartTrackingTouch(mSeekBar.getProgress());
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isManuallyTouchingSeekbar = false;
        if (mCallback != null && mSeekBar != null) {
            mCallback.saveBrightnessDataBase(mSeekBar.getProgress());
        }
    }
}
