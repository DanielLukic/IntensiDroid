package net.intensicode;

public final class WrappedFloatAsIntegerValue implements ConfigurableIntegerValue
    {
    public WrappedFloatAsIntegerValue( final ConfigurableFloatValue aFloatValue )
        {
        myFloatValue = aFloatValue;
        }

    // From ConfigurableIntegerValue

    public String getValueAsText( final int aConfiguredValue )
        {
        return myFloatValue.getValueAsText( aConfiguredValue / MULTIPLIER );
        }

    public void setNewValue( final int aConfiguredValue )
        {
        myFloatValue.setNewValue( aConfiguredValue / MULTIPLIER );
        }

    public int getMaxValue()
        {
        return (int) (myFloatValue.getValueRange() * MULTIPLIER);
        }

    public int getCurrentValue()
        {
        return (int) (myFloatValue.getCurrentValue() * MULTIPLIER);
        }

    public int getStepSize()
        {
        return (int) (myFloatValue.getStepSize() * MULTIPLIER);
        }

    // From ConfigurableValue

    public final String getTitle()
        {
        return myFloatValue.getTitle();
        }

    public final String getInfoText()
        {
        return myFloatValue.getInfoText();
        }


    private final ConfigurableFloatValue myFloatValue;

    private static final float MULTIPLIER = 1000;
    }
