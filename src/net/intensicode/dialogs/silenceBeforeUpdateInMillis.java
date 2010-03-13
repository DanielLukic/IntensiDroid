package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class silenceBeforeUpdateInMillis extends SeekBarDialogBase
    {
    public silenceBeforeUpdateInMillis( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "silenceBeforeUpdateInMillis";
        }

    protected final String getInfoText()
        {
        return "Milliseconds before the current trackball changes are considered stable. " +
               "Can be considered the responsiveness of the trackball. " +
               "Unfortunately it is not that easy.. :)";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        system().analog.silenceBeforeUpdateInMillis = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_SILENCE_IN_MILLIS;
        }

    protected final int getValueForSeekBar()
        {
        return system().analog.silenceBeforeUpdateInMillis;
        }


    private static final int MAXIMUM_SILENCE_IN_MILLIS = 250;
    }
