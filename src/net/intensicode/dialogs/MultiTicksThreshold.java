package net.intensicode.dialogs;

import net.intensicode.core.AnalogController;
import net.intensicode.ConfigurableValue;

public final class MultiTicksThreshold implements ConfigurableValue
    {
    public MultiTicksThreshold( final AnalogController aAnalogController )
        {
        myAnalogController = aAnalogController;
        }

    // From SeekBarDialogBase

    public final String getTitle()
        {
        return "multiTicksThreshold";
        }

    public final String getInfoText()
        {
        return "Number of 'ticks' after initial significant tick before considering a trackball event a multi event. " +
               "The higher this value, the more the trackball has to move before an event triggeres a move value higher than 1. ";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ticks";
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        myAnalogController.multiTicksThreshold = aSeekBarValue;
        }

    public final int getMaxValue()
        {
        return MAXIMUM_MULTI_TICKS_THRESHOLD;
        }

    public final int getCurrentValue()
        {
        return myAnalogController.multiTicksThreshold;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AnalogController myAnalogController;

    private static final int MAXIMUM_MULTI_TICKS_THRESHOLD = 30;
    }
