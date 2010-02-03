package net.intensicode.droid.audio;

import android.content.res.*;
import android.media.AudioManager;
import android.media.*;
import net.intensicode.core.*;
import net.intensicode.util.*;

import java.io.IOException;

public final class SoundPoolBackend implements SoundBackend
    {
    public static final int DEFAULT_NUMBER_OF_CHANNELS = 5; // 4 sounds + 1 music expected

    public String musicFolder = "music";

    public String musicSuffix = ".mp3";

    public String soundFolder = "sound";

    public String soundSuffix = ".mp3";


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

    // From SoundBackend

    public final int numberOfChannels()
        {
        return myNumberOfChannels;
        }

    public final MusicResource loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( musicFolder, aMusicName, musicSuffix );
        //#if DEBUG
        Log.debug( "loading music resource {}", resourceFilePath );
        //#endif
        return (MusicResource) createAudioResource( resourceFilePath );
        }

    public final SoundResource loadSound( final String aSoundName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( soundFolder, aSoundName, soundSuffix );
        //#if DEBUG
        Log.debug( "loading sound resource {}", resourceFilePath );
        //#endif
        return (SoundResource) createAudioResource( resourceFilePath );
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

    private AudioResource createAudioResource( final String aResourceFilePath ) throws IOException
        {
        final AssetFileDescriptor fd = myAssetManager.openFd( aResourceFilePath );
        final int soundID = mySoundPool.load( fd, DEFAULT_SOUND_PRIORITY );
        return new SoundPoolAudioResource( mySoundPool, soundID );
        }


    private final int myNumberOfChannels;

    private final SoundPool mySoundPool;

    private final AssetManager myAssetManager;

    private static final int DEFAULT_SOUND_PRIORITY = 1;

    private static final int DEFAULT_SOUND_CONVERSION_QUALITY = 0;
    }
