package net.intensicode.configuration;

import android.os.Debug;
import net.intensicode.ConfigurableActionValue;

public final class StopProfiling implements ConfigurableActionValue
    {
    public final String getTitle()
        {
        return "Stop profiling";
        }

    public final String getInfoText()
        {
        return "Stop profiling and dump data to SD card";
        }

    public final void trigger()
        {
        Debug.stopMethodTracing();
        }
    }
