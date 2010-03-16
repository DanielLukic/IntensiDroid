package net.intensicode.configuration;

import android.content.Context;
import net.intensicode.*;

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
            else
                {
                throw new RuntimeException( "nyi" );
                }
        }

    private final Context myContext;
    }
