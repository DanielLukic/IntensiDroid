package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;

public final class forcedSilenceBetweenEventsInMillis extends SeekBarDialogBase
    {
    public forcedSilenceBetweenEventsInMillis( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "forcedSilenceBetweenEventsInMillis";
        }

    protected final String getInfoText()
        {
        return "Milliseconds in which trackball changes are discared before starting a new event.";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
//        system().analog.forcedSilenceBetweenEventsInMillis = aSeekBarValue;
        }

    protected final int getMaximumForSeekBar()
        {
        return MAXIMUM_SILENCE_IN_MILLIS;
        }

    protected final int getValueForSeekBar()
        {
//        return system().analog.forcedSilenceBetweenEventsInMillis;
        return 0;
        }


    private static final int MAXIMUM_SILENCE_IN_MILLIS = 250;
    }
