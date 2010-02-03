package net.intensicode.droid;

import android.content.Context;
import android.media.MediaPlayer;
import net.intensicode.core.*;
import net.intensicode.util.Log;
import net.intensicode.droid.audio.*;

import java.io.IOException;
import java.util.ArrayList;

public final class AndroidAudioManager implements AudioManager, MediaPlayer.OnErrorListener
    {
    private static final int UNKNOWN_NUMBER_OF_CHANNELS = -1;

    public AndroidAudioManager( final Context aContext )
        {
        mySoundBackend = new SoundPoolBackend( aContext.getAssets() );
        myMusicBackend = new MediaPlayerBackend( aContext.getAssets() );
        }

    // From AudioManager

    public final boolean supportsMusicPlusSound()
        {
        return true;
        }

    public final boolean supportsMultiSound()
        {
        return true;
        }

    public final int numberOfSoundChannels()
        {
        return UNKNOWN_NUMBER_OF_CHANNELS;
        }

    public final void enableMusicAndSound()
        {
        myMusicEnabled = true;
        mySoundEnabled = true;
        }

    public final void enableMusicOnly()
        {
        myMusicEnabled = true;
        mySoundEnabled = false;
        }

    public final void enableSoundOnly()
        {
        myMusicEnabled = false;
        mySoundEnabled = true;
        }

    public final void disable()
        {
        myMusicEnabled = false;
        mySoundEnabled = false;
        }

    public final void setMasterMute( final boolean aMutedFlag )
        {
        myMutedFlag = aMutedFlag;
        }

    public final void setMasterVolume( final int aVolumeInPercent )
        {
        myMasterVolume = aVolumeInPercent;
        }

    public final void setMasterMusicMute( final boolean aMutedFlag )
        {
        myMasterMusicMutedFlag = aMutedFlag;
        }

    public final void setMasterSoundMute( final boolean aMutedFlag )
        {
        myMasterSoundMutedFlag = aMutedFlag;
        }

    public final void setMasterMusicVolume( final int aVolumeInPercent )
        {
        myMasterMusicVolume = aVolumeInPercent;
        }

    public final void setMasterSoundVolume( final int aVolumeInPercent )
        {
        myMasterSoundVolume = aVolumeInPercent;
        }

    public final MusicResource loadMusic( final String aMusicName ) throws IOException
        {
        final MusicResource resource = myMusicBackend.loadMusic( aMusicName );
        registerMusicResource( resource );
        return resource;
        }

    public final SoundResource loadSound( final String aSoundName ) throws IOException
        {
        final SoundResource resource = mySoundBackend.loadSound( aSoundName );
        registerSoundResource( resource );
        return resource;
        }

    // From OnErrorListener

    public boolean onError( final MediaPlayer aMediaPlayer, final int i, final int i1 )
        {
        //#if DEBUG
        Log.debug( "media player error {} {}", i, i1 );
        //#endif
        return false;
        }

    // Implementation

    private void registerMusicResource( final MusicResource aMusicResource )
        {
        myMusicResources.add( aMusicResource );
        }

    private void registerSoundResource( final SoundResource aSoundResource )
        {
        mySoundResources.add( aSoundResource );
        }


    private int myMasterVolume;

    private boolean myMutedFlag;

    private boolean myMusicEnabled;

    private boolean mySoundEnabled;

    private int myMasterMusicVolume;

    private int myMasterSoundVolume;

    private boolean myMasterMusicMutedFlag;

    private boolean myMasterSoundMutedFlag;

    private final SoundBackend myMusicBackend;

    private final SoundBackend mySoundBackend;

    private final ArrayList<MusicResource> myMusicResources = new ArrayList<MusicResource>();

    private final ArrayList<SoundResource> mySoundResources = new ArrayList<SoundResource>();
    }
