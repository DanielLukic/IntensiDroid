package net.intensicode.droid.audio;

import net.intensicode.core.AudioResourceEx;

import java.io.IOException;

public interface AudioBackend
    {
    AudioResourceEx loadMusic( String aMusicName ) throws IOException;

    AudioResourceEx loadSound( String aSoundName ) throws IOException;
    }
