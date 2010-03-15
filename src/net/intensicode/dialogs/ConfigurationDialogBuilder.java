package net.intensicode.dialogs;

import android.content.Context;
import net.intensicode.ConfigurableValue;

public final class ConfigurationDialogBuilder
    {
    public ConfigurationDialogBuilder( final Context aContext )
        {
        myContext = aContext;
        }

    public final ConfigurableSeekBarDialog using( final ConfigurableValue aConfigurableValue )
        {
        return new ConfigurableSeekBarDialog( myContext, aConfigurableValue );
        }

    private final Context myContext;
    }
