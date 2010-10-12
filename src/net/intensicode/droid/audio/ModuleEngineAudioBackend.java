package net.intensicode.droid.audio;

import net.intensicode.ReleaseProperties;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.core.ResourcesManager;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.MuxmModuleEngine;

import java.io.IOException;

public final class ModuleEngineAudioBackend implements AudioBackend
    {
    public ModuleEngineAudioBackend( final ResourcesManager aResourcesManager )
        {
        myResourcesManager = aResourcesManager;
        }

    // From AudioBackend

    public final AudioResourceEx loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.MUSIC_FOLDER, aMusicName, ReleaseProperties.MUSIC_FORMAT_SUFFIX );
        final ModuleEnginePlayer player = createPlayerForModule( resourceFilePath );
        return new ModuleEngineAudioResource( player );
        }

    public final AudioResourceEx loadSound( final String aSoundName )
        {
        throw new UnsupportedOperationException();
        }

    // Implementation

    private String makeResourceFilePath( final String aResourceFolder, final String aMusicName, final String aFileNameSuffix )
        {
        final StringBuilder builder = new StringBuilder();
        builder.append( aResourceFolder );
        builder.append( "/" );
        builder.append( aMusicName );
        builder.append( aFileNameSuffix );
        return builder.toString();
        }

    private ModuleEnginePlayer createPlayerForModule( final String aMusicResourceFilePath ) throws IOException
        {
        //#if DEBUG
        Log.debug( "loading module {}", aMusicResourceFilePath );
        //#endif
        final byte[] moduleData = myResourcesManager.loadData( aMusicResourceFilePath );

        final ModuleEngine engine = createModuleEngine();
        engine.load( moduleData );
        return new ModuleEnginePlayer( engine );
        }

    private ModuleEngine createModuleEngine()
        {
        try
            {
            return new ModplugModuleEngine();
            }
        catch ( final Throwable t )
            {
            Log.error( "failed creating native module player engine", t );
            return new MuxmModuleEngine();
            }
        }

    private final ResourcesManager myResourcesManager;
    }
