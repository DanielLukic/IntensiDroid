package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class initialTicksThreshold extends SeekBarDialogBase
    {
    public initialTicksThreshold( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "initialTicksThreshold";
        }

    protected final String getInfoText()
        {
        return "Initial 'ticks' to ignore before considering a trackball event valid. " +
               "The higher this value, the more the trackball has to move before an event is triggered. ";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ticks";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        system().analog.initialTicksThreshold = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_INITIAL_TICKS_THRESHOLD;
        }

    protected final int getValueForSeekBar()
        {
        return system().analog.initialTicksThreshold;
        }


    private static final int MAXIMUM_INITIAL_TICKS_THRESHOLD = 30;
    }
