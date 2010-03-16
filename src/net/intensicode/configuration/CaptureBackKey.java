package net.intensicode.configuration;

import net.intensicode.ConfigurableValue;
import net.intensicode.droid.AndroidKeysHandler;

public final class CaptureBackKey implements ConfigurableValue
    {
    public CaptureBackKey( final AndroidKeysHandler aKeysHandler )
        {
        myKeysHandler = aKeysHandler;
        }

    // From SeekBarDialogBase

    public final String getTitle()
        {
        return "Capture BACK key";
        }

    public final String getInfoText()
        {
        return "If enabled, the BACK key will not exit the application.";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue != 0 ? "ON" : "OFF";
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        myKeysHandler.captureBackKey = aSeekBarValue != 0;
        }

    public final int getMaxValue()
        {
        return 1;
        }

    public final int getCurrentValue()
        {
        return 0;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AndroidKeysHandler myKeysHandler;
    }
