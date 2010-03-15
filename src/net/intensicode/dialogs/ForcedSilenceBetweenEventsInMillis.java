package net.intensicode.dialogs;

import net.intensicode.core.AnalogController;
import net.intensicode.ConfigurableValue;

public final class ForcedSilenceBetweenEventsInMillis implements ConfigurableValue
    {
    public ForcedSilenceBetweenEventsInMillis( final AnalogController aAnalogController )
        {
        myAnalogController = aAnalogController;
        }

    // From ConfigurableValue

    public final String getTitle()
        {
        return "forcedSilenceBetweenEventsInMillis";
        }

    public final String getInfoText()
        {
        return "Milliseconds in which trackball changes are discared before starting a new event.";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        return aSeekBarValue + " ms";
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        myAnalogController.forcedSilenceBetweenEventsInMillis = aSeekBarValue;
        }

    public final int getMinValue()
        {
        return 0;
        }

    public final int getMaxValue()
        {
        return MAXIMUM_SILENCE_IN_MILLIS;
        }

    public final int getCurrentValue()
        {
        return myAnalogController.forcedSilenceBetweenEventsInMillis;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AnalogController myAnalogController;

    private static final int MAXIMUM_SILENCE_IN_MILLIS = 250;
    }
