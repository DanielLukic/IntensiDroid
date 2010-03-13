package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.core.GameSystem;
import net.intensicode.util.FixedMath;

public final class directionIgnoreFactorFixed extends SeekBarDialogBase
    {
    public directionIgnoreFactorFixed( final Context aContext, final GameSystem aGameSystem )
        {
        super( aContext, aGameSystem );
        }

    // From SeekBarDialogBase

    protected final String getTitle()
        {
        return "directionIgnoreFactorFixed";
        }

    protected final String getInfoText()
        {
        return "Factor to apply to a direction when checking whether it is too small to be considered. " +
               "The higher this value, the more likely are diagonal or non-unidirectional movements. ";
        }

    protected final String getValueAsText( final int aSeekBarValue )
        {
        final float value = aSeekBarValue / 50f;
        return Float.toString( value );
        }

    protected final void setNewValue( final int aSeekBarValue )
        {
        // input: 0..50
        // output float: 1.0..5.0
        // output fixed: F1..F5
        final int fixedRange = FixedMath.FIXED_5 - FixedMath.FIXED_1;
        final int scaled = aSeekBarValue * fixedRange / 50;
        final int scaledAndTransposed = scaled + FixedMath.FIXED_1;
        system().analog.directionIgnoreFactorFixed = scaledAndTransposed;
        }

    protected final int getMaximumForSeekBar()
        {
        return 50;
        }

    protected final int getValueForSeekBar()
        {
        final int valueFixed = system().analog.directionIgnoreFactorFixed;
        final int deposed = valueFixed - FixedMath.FIXED_1;
        final int fixedRange = FixedMath.FIXED_5 - FixedMath.FIXED_1;
        final int deposedAndDescaled = deposed * 50 / fixedRange;
        return deposedAndDescaled;
        }
    }
