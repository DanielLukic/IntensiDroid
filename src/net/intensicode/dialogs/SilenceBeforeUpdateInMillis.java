package net.intensicode.dialogs;

import net.intensicode.core.AnalogController;
import net.intensicode.ConfigurableValue;

public final class SilenceBeforeUpdateInMillis implements ConfigurableValue
    {
    public SilenceBeforeUpdateInMillis( final AnalogController aAnalogController )
        {
        myAnalogController = aAnalogController;
        }

    // From SeekBarDialogBase

    public final String getTitle()
        {
        return "silenceBeforeUpdateInMillis";
        }

    public final String getInfoText()
        {
        return "Milliseconds before the current trackball changes are considered stable. " +
               "Can be considered the responsiveness of the trackball. " +
               "Unfortunately it is not that easy.. :)";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        myAnalogController.silenceBeforeUpdateInMillis = aSeekBarValue;
        }

    public final int getMaxValue()
        {
        return MAXIMUM_SILENCE_IN_MILLIS;
        }

    public final int getCurrentValue()
        {
        return myAnalogController.silenceBeforeUpdateInMillis;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AnalogController myAnalogController;

    private static final int MAXIMUM_SILENCE_IN_MILLIS = 250;
    }
