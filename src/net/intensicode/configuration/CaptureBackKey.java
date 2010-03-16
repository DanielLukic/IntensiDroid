package net.intensicode.configuration;

import net.intensicode.ConfigurableBooleanValue;
import net.intensicode.droid.AndroidKeysHandler;

public final class CaptureBackKey implements ConfigurableBooleanValue
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

    public final String getValueAsText( final boolean aConfiguredValue )
        {
        return aConfiguredValue ? "ON" : "OFF";
        }

    public final void setNewValue( final boolean aConfiguredValue )
        {
        myKeysHandler.captureBackKey = aConfiguredValue;
        }

    public final boolean getCurrentValue()
        {
        return myKeysHandler.captureBackKey;
        }


    private final AndroidKeysHandler myKeysHandler;
    }
