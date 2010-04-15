package net.intensicode.configuration;

import android.os.*;
import net.intensicode.ConfigurableActionValue;
import net.intensicode.util.Log;

import java.io.*;

public final class StartProfiling implements ConfigurableActionValue
    {
    public final String getTitle()
        {
        return "Start profiling";
        }

    public final String getInfoText()
        {
        return "Start profiling method calls";
        }

    public final void trigger()
        {
        try
            {
            final File dataDirectory = Environment.getExternalStorageDirectory();
            final File intensigameFolder = new File( dataDirectory, "intensigame" );
            intensigameFolder.mkdirs();
            final File profilingDataFile = new File( intensigameFolder, "profiling.dat" );
            profilingDataFile.createNewFile();
            Debug.startMethodTracing( profilingDataFile.getAbsolutePath(), BUFFERSIZE_16_MB );
            }
        catch ( IOException e )
            {
            Log.error( "failed dumping atlas image", e );
            }
        }

    private static final int BUFFERSIZE_16_MB = 16 * 1024 * 1024;
    }
