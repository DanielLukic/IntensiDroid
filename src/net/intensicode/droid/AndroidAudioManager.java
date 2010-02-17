package net.intensicode.droid;

import android.content.Context;
import net.intensicode.ReleaseProperties;
import net.intensicode.core.*;
import net.intensicode.droid.audio.*;

public final class AndroidAudioManager extends AudioManager
    {
    public AndroidAudioManager( final Context aContext )
        {
        mySoundBackend = new SoundPoolBackend( aContext.getAssets() );

        if ( ReleaseProperties.MUSIC_FORMAT_SUFFIX.equals( ".mod" ) )
            {
            myMusicBackend = new MuxmPlayerBackend( aContext.getAssets() );
            }
        else
            {
            myMusicBackend = new MediaPlayerBackend( aContext.getAssets() );
            }
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


    private final AudioBackend myMusicBackend;

    private final AudioBackend mySoundBackend;
    }
