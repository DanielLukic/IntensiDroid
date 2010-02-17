package net.intensicode.droid.audio;

import net.intensicode.core.AudioResourceEx;

public final class MuxmPlayerAudioResource implements AudioResourceEx
    {
    public MuxmPlayerAudioResource( final MuxmPlayer aPlayer, final String aResourcePath )
        {
        myPlayer = aPlayer;
        myResourcePath = aResourcePath;
        }

    // From AudioResourceEx

    public final void enable()
        {
        if ( myEnabledFlag ) return;

        myEnabledFlag = true;

        if ( myPlayingFlag ) myPlayer.start();
        }

    public final void disable()
        {
        if ( !myEnabledFlag ) return;

        myPlayingFlag = myPlayer.isPlaying();
        myEnabledFlag = false;

        if ( myPlayer.isPlaying() ) myPlayer.pause();
        }

    // From AudioResource

    public final void setLoopForever()
        {
        myPlayer.setLooping( true );
        }

    public final void setVolume( final int aVolumeInPercent )
        {
        myCurrentVolume = aVolumeInPercent;

        myPlayer.setVolume( aVolumeInPercent );
        }

    public final void mute()
        {
        myPlayer.setVolume( 0 );
        }

    public final void unmute()
        {
        setVolume( myCurrentVolume );
        }

    public final void start()
        {
        if ( myPlayer.isPlaying() ) stop();
        if ( myEnabledFlag ) myPlayer.start();
        else triggerPlayAfterEnable();
        }

    public final void stop()
        {
        if ( myPlayer.isPlaying() ) myPlayer.stop();
        }

    public final void pause()
        {
        if ( myPlayer.isPlaying() ) myPlayer.pause();
        }

    public final void resume()
        {
        if ( myEnabledFlag ) myPlayer.start();
        else triggerPlayAfterEnable();
        }

    // Implementation

    private void triggerPlayAfterEnable()
        {
        myPlayingFlag = true;
        }


    private int myCurrentVolume;

    private boolean myPlayingFlag;

    private boolean myEnabledFlag = true;

    private final MuxmPlayer myPlayer;

    private final String myResourcePath;
    }
