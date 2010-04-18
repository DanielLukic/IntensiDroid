package net.intensicode.configuration;

import android.graphics.Bitmap;
import android.os.Environment;
import net.intensicode.ConfigurableActionValue;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.util.Log;

import java.io.*;

public final class DumpTextureAtlases implements ConfigurableActionValue
    {
    public DumpTextureAtlases( final OpenglGraphics aOpenglGraphics )
        {
        myOpenglGraphics = aOpenglGraphics;
        }

    public final String getTitle()
        {
        return "Dump Texture Atlases";
        }

    public final String getInfoText()
        {
        return "Dump texture atlases to SD card";
        }

    public final void trigger()
        {
        try
            {
            final File dataDirectory = Environment.getExternalStorageDirectory();
            final File intensigameFolder = new File( dataDirectory, "intensigame" );
            intensigameFolder.mkdirs();

            final Bitmap[] bitmaps = myOpenglGraphics.dumpTextureAtlases();
            for ( int idx = 0; idx < bitmaps.length; idx++ )
                {
                final File dumpFile = new File( intensigameFolder, "atlas" + ( 1 + idx ) + ".png" );
                dumpFile.createNewFile();
                final FileOutputStream stream = new FileOutputStream( dumpFile );
                final boolean done = bitmaps[ idx ].compress( Bitmap.CompressFormat.PNG, 100, stream );
                if ( !done ) throw new IOException();
                stream.close();
                }
            }
        catch ( final Exception e )
            {
            Log.error( "failed dumping texture atlases", e );
            }
        }

    private final OpenglGraphics myOpenglGraphics;
    }
