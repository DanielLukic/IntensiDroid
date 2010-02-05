package net.intensicode.droid.audio;

import android.content.res.*;
import android.media.*;
import net.intensicode.ReleaseProperties;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.util.*;

import java.io.IOException;

public final class SoundPoolBackend implements AudioBackend
    {
    public static final int DEFAULT_NUMBER_OF_CHANNELS = 5; // 4 sounds + 1 music expected


    public SoundPoolBackend( final AssetManager aAssetManager )
        {
        myAssetManager = aAssetManager;
        myNumberOfChannels = DEFAULT_NUMBER_OF_CHANNELS;
        mySoundPool = new SoundPool( DEFAULT_NUMBER_OF_CHANNELS, AudioManager.STREAM_MUSIC, DEFAULT_SOUND_CONVERSION_QUALITY );

        //#if DEBUG
        // The API suggests mySoundPool may be null!?
        Assert.notNull( "sound pool created", mySoundPool );
        //#endif
        }

    // From AudioBackend

    public final int numberOfChannels()
        {
        return myNumberOfChannels;
        }

    public final AudioResourceEx loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.MUSIC_FOLDER, aMusicName, ReleaseProperties.MUSIC_FORMAT_SUFFIX );
        //#if DEBUG
        Log.debug( "loading music resource {}", resourceFilePath );
        //#endif
        return createAudioResource( resourceFilePath );
        }

    public final AudioResourceEx loadSound( final String aSoundName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.SOUND_FOLDER, aSoundName, ReleaseProperties.SOUND_FORMAT_SUFFIX );
        //#if DEBUG
        Log.debug( "loading sound resource {}", resourceFilePath );
        //#endif
        return createAudioResource( resourceFilePath );
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

    private AudioResourceEx createAudioResource( final String aResourceFilePath ) throws IOException
        {
        final AssetFileDescriptor fd = myAssetManager.openFd( aResourceFilePath );
        final int soundID = mySoundPool.load( fd, DEFAULT_SOUND_PRIORITY );
        return new SoundPoolAudioResource( mySoundPool, soundID, aResourceFilePath );
        }


    private final int myNumberOfChannels;

    private final SoundPool mySoundPool;

    private final AssetManager myAssetManager;

    private static final int DEFAULT_SOUND_PRIORITY = 1;

    private static final int DEFAULT_SOUND_CONVERSION_QUALITY = 0;
    }
