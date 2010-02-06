package net.intensicode.droid.audio;

import android.media.SoundPool;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.util.Log;

public final class SoundPoolAudioResource implements AudioResourceEx
    {
    public SoundPoolAudioResource( final SoundPool aSoundPool, final int aSoundID, final String aResourcePath )
        {
        mySoundPool = aSoundPool;
        mySoundID = aSoundID;
        myResourcePath = aResourcePath;
        }

    // From AudioResourceEx

    public final void enable()
        {
        if ( myEnabledFlag ) return;

        myEnabledFlag = true;

        if ( myWasPlayingFlag ) resume();
        }

    public final void disable()
        {
        if ( !myEnabledFlag ) return;

        myWasPlayingFlag = myLoopingFlag;
        myEnabledFlag = false;

        if ( isProbablyPlaying() ) pause();
        }

    // From AudioResource

    public final void setLoopForever()
        {
        myLoopingFlag = true;
        mySoundPool.setLoop( mySoundID, LOOP_FOREVER );
        }

    public final void setVolume( final int aVolumeInPercent )
        {
        myVolume = aVolumeInPercent * 1.0f / 100;
        if ( isProbablyPlaying() ) mySoundPool.setVolume( myPlayID, myVolume, myVolume );
        }

    public final void mute()
        {
        myMutedFlag = true;
        if ( isProbablyPlaying() ) mySoundPool.setVolume( myPlayID, 0, 0 );
        }

    public final void unmute()
        {
        myMutedFlag = false;
        if ( isProbablyPlaying() ) mySoundPool.setVolume( myPlayID, myVolume, myVolume );
        }

    public final void start()
        {
        if ( isProbablyPlaying() ) stop();

        final float volume = myMutedFlag ? 0.0f : myVolume;
        do
            {
            myPlayID = mySoundPool.play( mySoundID, volume, volume, STREAM_PRIORITY, DO_NOT_LOOP, PLAYBACK_RATE );
            //#if DEBUG
            if ( isNotPlaying() ) sleepTenthOfOneSecond();
            //#endif
            }
        while ( isNotPlaying() && ++myPlayRetries < MAX_PLAY_RETRIES );

        //#if DEBUG
        if ( isNotPlaying() ) Log.debug( "failed playing sound {}: {}", mySoundID, myResourcePath );
        //#endif
        }

    public final void stop()
        {
        if ( isProbablyPlaying() ) mySoundPool.stop( myPlayID );
        myPlayID = NOT_PLAYING;
        }

    public final void pause()
        {
        if ( isProbablyPlaying() ) mySoundPool.pause( myPlayID );
        }

    public final void resume()
        {
        if ( isProbablyPlaying() ) mySoundPool.resume( myPlayID );
        }

    // Implementation

    private boolean isProbablyPlaying()
        {
        return myPlayID != NOT_PLAYING;
        }

    private boolean isNotPlaying()
        {
        return myPlayID == NOT_PLAYING;
        }

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

    private boolean myLoopingFlag;

    private boolean myWasPlayingFlag;

    private boolean myEnabledFlag = true;

    private final int mySoundID;

    private final String myResourcePath;

    private final SoundPool mySoundPool;

    private static final int DO_NOT_LOOP = 0;

    private static final int NOT_PLAYING = 0;

    private static final int STREAM_PRIORITY = 0;

    private static final float PLAYBACK_RATE = 1.0f;

    private static final int MAX_PLAY_RETRIES = 10;

    private static final int LOOP_FOREVER = -1;
    }
