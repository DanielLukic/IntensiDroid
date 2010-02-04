package net.intensicode.droid.audio;

import android.media.SoundPool;
import net.intensicode.core.*;
import net.intensicode.util.Log;

public final class SoundPoolAudioResource implements AudioResource, MusicResource, SoundResource
    {
    public SoundPoolAudioResource( final SoundPool aSoundPool, final int aSoundID, final String aResourceFilePath )
        {
        mySoundPool = aSoundPool;
        mySoundID = aSoundID;
        myResourceFilePath = aResourceFilePath;
        setVolume( 100 );
        }

    // From AudioResource

    public final void setVolume( final int aVolumeInPercent )
        {
        myVolume = aVolumeInPercent * 1.0f / 100;
        if ( myPlayingFlag ) mySoundPool.setVolume( myPlayID, myVolume, myVolume );
        }

    public final void mute()
        {
        myMutedFlag = true;
        if ( myPlayingFlag ) mySoundPool.setVolume( myPlayID, 0, 0 );
        }

    public final void unmute()
        {
        if ( myPlayingFlag ) mySoundPool.setVolume( myPlayID, myVolume, myVolume );
        myMutedFlag = false;
        }

    public final void play()
        {
        if ( myPlayingFlag ) stop();

        final float volume = myMutedFlag ? 0.0f : myVolume;
        do
            {
            myPlayID = mySoundPool.play( mySoundID, volume, volume, STREAM_PRIORITY, DO_NOT_LOOP, PLAYBACK_RATE );
            //#if DEBUG
            if ( myPlayID == 0 ) sleepTenthOfOneSecond();
            //#endif
            }
        while ( myPlayID == 0 && ++myPlayRetries < MAX_PLAY_RETRIES );

        //#if DEBUG
        if ( myPlayID == 0 ) Log.debug( "failed playing sound {}: {}", mySoundID, myResourceFilePath );
        //#endif

        myPlayingFlag = myPlayID != PLAY_FAILED;
        }

    public final void stop()
        {
        if ( myPlayingFlag ) mySoundPool.stop( myPlayID );
        myPlayingFlag = false;
        }

    public final void pause()
        {
        if ( myPlayingFlag ) mySoundPool.pause( myPlayID );
        }

    public final void resume()
        {
        if ( myPlayingFlag ) mySoundPool.resume( myPlayID );
        }

    // Implementation

    private static void sleepTenthOfOneSecond()
        {
        try
            {
            Thread.sleep( 100 );
            }
        catch ( InterruptedException e )
            {
            e.printStackTrace();
            }
        }


    private int myPlayID;

    private int myPlayRetries;

    private float myVolume;

    private boolean myMutedFlag;

    private final int mySoundID;

    private final String myResourceFilePath;

    private boolean myPlayingFlag;

    private final SoundPool mySoundPool;

    private static final int DO_NOT_LOOP = 0;

    private static final int PLAY_FAILED = 0;

    private static final int STREAM_PRIORITY = 0;

    private static final float PLAYBACK_RATE = 1.0f;

    private static final int MAX_PLAY_RETRIES = 10;
    }
