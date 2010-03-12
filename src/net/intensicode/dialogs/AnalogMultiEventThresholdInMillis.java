package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class AnalogMultiEventThresholdInMillis extends SeekBarDialogBase
    {
    public AnalogMultiEventThresholdInMillis( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "multiEventThresholdInMillis";
        }

    protected final String getInfoText()
        {
        return "Milliseconds before the current trackball changes are considered multiple events. " +
               "This value determines in what interval trackball events are fired if the user continously moves the trackball. ";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        system().analog.multiEventThresholdInMillis = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_THRESHOLD_IN_MILLIS;
        }

    protected final int getValueForSeekBar()
        {
        return system().analog.multiEventThresholdInMillis;
        }


    private static final int MAXIMUM_THRESHOLD_IN_MILLIS = 250;
    }
