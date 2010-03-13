package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class multiTicksThreshold extends SeekBarDialogBase
    {
    public multiTicksThreshold( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "multiTicksThreshold";
        }

    protected final String getInfoText()
        {
        return "Number of 'ticks' after initial significant tick before considering a trackball event a multi event. " +
               "The higher this value, the more the trackball has to move before an event triggeres a move value higher than 1. ";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ticks";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        system().analog.multiTicksThreshold = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_MULTI_TICKS_THRESHOLD;
        }

    protected final int getValueForSeekBar()
        {
        return system().analog.multiTicksThreshold;
        }


    private static final int MAXIMUM_MULTI_TICKS_THRESHOLD = 30;
    }