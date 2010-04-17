package net.intensicode;

import android.content.Context;

public final class ConfigurationDialogBuilder
    {
    public ConfigurationDialogBuilder( final Context aContext )
        {
        myContext = aContext;
        }

    public final ConfigurableDialog using( final ConfigurableValue aConfigurableValue )
        {
        if ( aConfigurableValue instanceof ConfigurableIntegerValue )
            {
            return new ConfigurableSeekBarDialog( myContext, (ConfigurableIntegerValue) aConfigurableValue );
            }
        else if ( aConfigurableValue instanceof ConfigurableBooleanValue )
            {
            return new ConfigurableToggleDialog( myContext, (ConfigurableBooleanValue) aConfigurableValue );
            }
        else if ( aConfigurableValue instanceof ConfigurableActionValue )
                {
                return new ConfigurableActionDialog( myContext, (ConfigurableActionValue) aConfigurableValue );
                }
            else if ( aConfigurableValue instanceof ConfigurableFloatValue )
                    {
                    final ConfigurableFloatValue floatValue = (ConfigurableFloatValue) aConfigurableValue;
                    return new ConfigurableSeekBarDialog( myContext, new WrappedFloatAsIntegerValue( floatValue ) );
                    }
                else
                    {
                    throw new RuntimeException( "nyi" );
                    }
        }

    private final Context myContext;
    }
