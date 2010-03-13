package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class additionalMultiTicksThreshold extends SeekBarDialogBase
    {
    public additionalMultiTicksThreshold( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "additionalMultiTicksThreshold";
        }

    protected final String getInfoText()
        {
        return "Number of 'ticks' after first multi event tick before considering adding another multi event step. " +
               "The higher this value, the more the trackball has to move before additional move steps are issued. ";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ticks";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        system().analog.additionalMultiTicksThreshold = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_ADDITIONAL_MULTI_TICKS_THRESHOLD;
        }

    protected final int getValueForSeekBar()
        {
        return system().analog.additionalMultiTicksThreshold;
        }


    private static final int MAXIMUM_ADDITIONAL_MULTI_TICKS_THRESHOLD = 30;
    }