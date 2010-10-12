package net.intensicode.droid.audio;

import android.media.AudioTrack;
import net.intensicode.util.Log;

final class AudioTrackRefillThread extends Thread implements AudioTrack.OnPlaybackPositionUpdateListener
    {
    public static final int FRAME_SIZE_IN_BYTES = 4;


    public AudioTrackRefillThread( final AudioTrack aAudioTrack, final byte[] aAudioBuffer, final ModuleEngine aModuleEngine )
        {
        super( "AudioTrackRefillThread" );
        myAudioTrack = aAudioTrack;
        myAudioBuffer = aAudioBuffer;
        myModuleEngine = aModuleEngine;
        myAudioTrack.setPlaybackPositionUpdateListener( this );
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myLoopingFlag = aLoopingFlag;
        myModuleEngine.setLooping( aLoopingFlag );
        }

    public final void startAudioTrack()
        {
        if ( !isAlive() ) start();
        myAudioTrack.play();
        fillAudioTrackBuffer();
        }

    public final void stopAudioTrack()
        {
        myAudioTrack.stop();
        myAudioTrack.flush();
        }

    // From Thread

    public void run()
        {
        //#if DEBUG_AUDIO
        Log.info( "audiotrack run STARTED in thread: {}", Thread.currentThread() );
        //#endif

        while ( true )
            {
            try
                {
                synchronized ( this )
                    {
                    //#if DEBUG_AUDIO
                    Log.info( "AudioTrackRefillThread waiting in thread: {}", Thread.currentThread() );
                    //#endif
                    while ( !myNeedDataFlag ) wait();
                    myNeedDataFlag = false;
                    }
                fillAudioTrackBuffer();
                }
            catch ( final InterruptedException e )
                {
                //#if DEBUG_AUDIO
                Log.debug( "AudioTrackRefillThread interrupted. Bailing out.." );
                //#endif
                break;
                }
            }

        //#if DEBUG_AUDIO
        Log.info( "audiotrack run ENDED in thread: {}", Thread.currentThread() );
        //#endif
        }

    // From AudioTrack.OnPlaybackPositionUpdateListener

    public final void onMarkerReached( final AudioTrack aAudioTrack )
        {
        //#if DEBUG_AUDIO
        Log.info( "onMarkerReached audiotrack thread: {}", Thread.currentThread() );
        //#endif
        synchronized ( this )
            {
            myNeedDataFlag = true;
            notify();
            }
        }

    public final void onPeriodicNotification( final AudioTrack aAudioTrack )
        {
        //#if DEBUG_AUDIO
        Log.info( "onPeriodicNotification audiotrack thread: {}", Thread.currentThread() );
        //#endif
        }

    public final void fillAudioTrackBuffer()
        {
        //#if DEBUG_AUDIO
        Log.info( "fillAudioTrackBuffer audiotrack thread: {}", Thread.currentThread() );
        //#endif

        final int bytesWritten = myModuleEngine.getStereoAudio( myAudioBuffer );

        final boolean musicStillPlaying = bytesWritten > 0;
        final boolean musicIsEndingOrHasEnded = bytesWritten < myAudioBuffer.length;
        final boolean musicHasEnded = bytesWritten == 0;

        int bytesWrittenToTrack = 0;

        if ( musicStillPlaying )
            {
            final int writtenToTrack = myAudioTrack.write( myAudioBuffer, 0, bytesWritten );
            if ( writtenToTrack > 0 ) bytesWrittenToTrack += writtenToTrack;

            if ( !musicIsEndingOrHasEnded ) myAudioTrack.setNotificationMarkerPosition( bytesWritten / FRAME_SIZE_IN_BYTES * 3 / 4 );
            }

        if ( musicIsEndingOrHasEnded && myLoopingFlag )
            {
            myModuleEngine.restart();

            final int moreBytesWritten = myModuleEngine.getStereoAudio( myAudioBuffer );
            final int writtenToTrack = myAudioTrack.write( myAudioBuffer, 0, moreBytesWritten );
            if ( writtenToTrack > 0 ) bytesWrittenToTrack += writtenToTrack;

            myAudioTrack.setNotificationMarkerPosition( bytesWrittenToTrack / FRAME_SIZE_IN_BYTES * 3 / 4 );
            }

        if ( musicHasEnded && !myLoopingFlag ) stopAudioTrack();
        }


    private boolean myNeedDataFlag;

    private boolean myLoopingFlag;

    private final byte[] myAudioBuffer;

    private final AudioTrack myAudioTrack;

    private final ModuleEngine myModuleEngine;
    }
