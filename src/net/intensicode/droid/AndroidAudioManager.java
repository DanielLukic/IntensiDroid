package net.intensicode.droid;

import android.content.Context;
import android.media.MediaPlayer;
import net.intensicode.core.*;
import net.intensicode.droid.audio.*;
import net.intensicode.util.Log;

public final class AndroidAudioManager extends AudioManager implements MediaPlayer.OnErrorListener
    {
    private static final int UNKNOWN_NUMBER_OF_CHANNELS = -1;

    public AndroidAudioManager( final Context aContext )
        {
        mySoundBackend = new SoundPoolBackend( aContext.getAssets() );
        myMusicBackend = new MediaPlayerBackend( aContext.getAssets() );
        }

    // From AudioManager

    protected AudioResourceEx loadMusicUnsafe( final String aMusicName ) throws Exception
        {
        return myMusicBackend.loadMusic( aMusicName );
        }

    protected AudioResourceEx loadSoundUnsafe( final String aSoundName ) throws Exception
        {
        return mySoundBackend.loadSound( aSoundName );
        }

    // From OnErrorListener

    public boolean onError( final MediaPlayer aMediaPlayer, final int i, final int i1 )
        {
        //#if DEBUG
        Log.debug( "media player error {} {}", i, i1 );
        //#endif
        return false;
        }


    private final AudioBackend myMusicBackend;

    private final AudioBackend mySoundBackend;
    }
