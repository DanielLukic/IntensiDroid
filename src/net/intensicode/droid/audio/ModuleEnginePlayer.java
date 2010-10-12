package net.intensicode.droid.audio;

import android.media.*;
import net.intensicode.util.Log;

final class ModuleEnginePlayer
    {
    public ModuleEnginePlayer( final ModuleEngine aModuleEngine )
        {
        myModuleEngine = aModuleEngine;

        final int sampleRate = aModuleEngine.getSampleRate();

        final int minBufferSize = AudioTrack.getMinBufferSize( sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT );
        final int frameCompatibleBuffer = minBufferSize * AudioTrackRefillThread.FRAME_SIZE_IN_BYTES;

        //#if DEBUG_AUDIO
        Log.info( "AudioTrack Native Sample Rate: {}", AudioTrack.getNativeOutputSampleRate( AudioTrack.MODE_STREAM ) );
        Log.info( "AudioTrack Volume Range: {} - {}", AudioTrack.getMinVolume(), AudioTrack.getMaxVolume() );
        Log.info( "AudioTrack MinBufferSize: {}", minBufferSize );
        Log.info( "Choosen BufferSize: {}", frameCompatibleBuffer );
        //#endif

        final AudioTrack audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, frameCompatibleBuffer, AudioTrack.MODE_STREAM );
        audioTrack.setPlaybackRate( sampleRate );

        myAudioTrack = audioTrack;
        myBufferSizeInBytes = frameCompatibleBuffer;
        myAudioBuffer = new byte[myBufferSizeInBytes];

        myRefillThread = new AudioTrackRefillThread( myAudioTrack, myAudioBuffer, myModuleEngine );
        myRefillThread.setDaemon( true );
        myRefillThread.start();

        //#if DEBUG_AUDIO
        Log.info( "initial audiotrack thread: {}", Thread.currentThread() );
        //#endif
        }

    public final void start()
        {
        //#if DEBUG_AUDIO
        Log.info( "start audiotrack thread: {}", Thread.currentThread() );
        //#endif

        if ( isPlaying() ) return;
        myRefillThread.startAudioTrack();
        }

    public final boolean isPlaying()
        {
        return myAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
        }

    public final void pause()
        {
        if ( isPlaying() ) myAudioTrack.pause();
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myRefillThread.setLooping( aLoopingFlag );
        }

    public final void setVolume( final int aVolumeInPercent )
        {
        final float volume = ( myAudioTrack.getMaxVolume() - myAudioTrack.getMinVolume() ) * aVolumeInPercent / 100.0f;
        myAudioTrack.setStereoVolume( volume, volume );
        }

    public final void stop()
        {
        if ( isStopped() ) return;
        myRefillThread.stopAudioTrack();
        }

    private boolean isStopped()
        {
        return myAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED;
        }


    private final AudioTrackRefillThread myRefillThread;

    private final byte[] myAudioBuffer;

    private final int myBufferSizeInBytes;

    private final AudioTrack myAudioTrack;

    private final ModuleEngine myModuleEngine;
    }
