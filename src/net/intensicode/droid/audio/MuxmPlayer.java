package net.intensicode.droid.audio;

import android.media.*;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.ModuleEngine;

public final class MuxmPlayer
    {
    public static final int DEFAULT_SAMPLE_RATE = 11025;

    public MuxmPlayer( final ModuleEngine aModuleEngine )
        {
        myModuleEngine = aModuleEngine;

        final int minBufferSize = AudioTrack.getMinBufferSize( DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT );
        final int frameCompatibleBuffer = minBufferSize * AudioTrackRefillThread.FRAME_SIZE_IN_BYTES;

        Log.info( "AudioTrack Native Sample Rate: {}", AudioTrack.getNativeOutputSampleRate( AudioTrack.MODE_STREAM ) );
        Log.info( "AudioTrack Volume Range: {} - {}", AudioTrack.getMinVolume(), AudioTrack.getMaxVolume() );
        Log.info( "AudioTrack MinBufferSize: {}", minBufferSize );
        Log.info( "Choosen BufferSize: {}", frameCompatibleBuffer );

        final AudioTrack audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, frameCompatibleBuffer, AudioTrack.MODE_STREAM );
        audioTrack.setPlaybackRate( DEFAULT_SAMPLE_RATE );

        myAudioTrack = audioTrack;
        myBufferSizeInBytes = frameCompatibleBuffer;
        myAudioBuffer = new byte[myBufferSizeInBytes];

        myModuleEngine.setSampleRate( DEFAULT_SAMPLE_RATE );

        myRefillThread = new AudioTrackRefillThread( myAudioTrack, myAudioBuffer, myModuleEngine );
        myRefillThread.setDaemon( true );
        myRefillThread.start();

        Log.info( "initial audiotrack thread: {}", Thread.currentThread() );
        }

    public final void start()
        {
        Log.info( "start audiotrack thread: {}", Thread.currentThread() );

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
