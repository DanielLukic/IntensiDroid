package net.intensicode.configuration;

import android.os.*;
import net.intensicode.ConfigurableActionValue;
import net.intensicode.util.Log;

import java.io.*;

public final class DumpHprofData implements ConfigurableActionValue
    {
    public final String getTitle()
        {
        return "Dump hprof data";
        }

    public final String getInfoText()
        {
        return "Dump hprof data to SD card";
        }

    public final void trigger()
        {
        try
            {
            final File dataDirectory = Environment.getExternalStorageDirectory();
            final File intensigameFolder = new File( dataDirectory, "intensigame" );
            intensigameFolder.mkdirs();
            final File profilingDataFile = new File( intensigameFolder, "memory" );
            profilingDataFile.createNewFile();
            Debug.dumpHprofData( profilingDataFile.getAbsolutePath() );
            }
        catch ( IOException e )
            {
            Log.error( "failed dumping hprof data", e );
            }
        }
    }
