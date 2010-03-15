package net.intensicode.dialogs;

import net.intensicode.core.AnalogController;
import net.intensicode.ConfigurableValue;

public final class MultiEventThresholdInMillis implements ConfigurableValue
    {
    public MultiEventThresholdInMillis( final AnalogController aAnalogController )
        {
        myAnalogController = aAnalogController;
        }

    // From SeekBarDialogBase

    public final String getTitle()
        {
        return "multiEventThresholdInMillis";
        }

    public final String getInfoText()
        {
        return "Milliseconds before the current trackball changes are considered multiple events. " +
               "This value determines in what interval trackball events are fired if the user continously moves the trackball. ";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        myAnalogController.multiEventThresholdInMillis = aSeekBarValue;
        }

    public final int getMaxValue()
        {
        return MAXIMUM_THRESHOLD_IN_MILLIS;
        }

    public final int getCurrentValue()
        {
        return myAnalogController.multiEventThresholdInMillis;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AnalogController myAnalogController;

    private static final int MAXIMUM_THRESHOLD_IN_MILLIS = 250;
    }
