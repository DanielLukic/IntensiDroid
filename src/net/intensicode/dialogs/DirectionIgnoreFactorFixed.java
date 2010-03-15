package net.intensicode.dialogs;

import net.intensicode.core.AnalogController;
import net.intensicode.util.FixedMath;
import net.intensicode.ConfigurableValue;

public final class DirectionIgnoreFactorFixed implements ConfigurableValue
    {
    public DirectionIgnoreFactorFixed( final AnalogController aAnalogController )
        {
        myAnalogController = aAnalogController;
        }

    // From SeekBarDialogBase

    public final String getTitle()
        {
        return "directionIgnoreFactorFixed";
        }

    public final String getInfoText()
        {
        return "Factor to apply to a direction when checking whether it is too small to be considered. " +
               "The higher this value, the more likely are diagonal or non-unidirectional movements. ";
        }

    public final String getValueAsText( final int aSeekBarValue )
        {
        final float value = aSeekBarValue / (float) MAXIMUM_DECI_STEPS;
        return Float.toString( value );
        }

    public final void setNewValue( final int aSeekBarValue )
        {
        // input: 0..MAXIMUM_DECI_STEPS
        // output float: 1.0..5.0
        // output fixed: F1..F5
        final int fixedRange = FixedMath.FIXED_5 - FixedMath.FIXED_1;
        final int scaled = aSeekBarValue * fixedRange / MAXIMUM_DECI_STEPS;
        final int scaledAndTransposed = scaled + FixedMath.FIXED_1;
        myAnalogController.directionIgnoreFactorFixed = scaledAndTransposed;
        }

    public final int getMaxValue()
        {
        return MAXIMUM_DECI_STEPS;
        }

    public final int getCurrentValue()
        {
        final int valueFixed = myAnalogController.directionIgnoreFactorFixed;
        final int deposed = valueFixed - FixedMath.FIXED_1;
        final int fixedRange = FixedMath.FIXED_5 - FixedMath.FIXED_1;
        final int deposedAndDescaled = deposed * MAXIMUM_DECI_STEPS / fixedRange;
        return deposedAndDescaled;
        }

    public final int getStepSize()
        {
        return DEFAULT_STEP_SIZE;
        }


    private final AnalogController myAnalogController;

    private static final int MAXIMUM_DECI_STEPS = 50;
    }
