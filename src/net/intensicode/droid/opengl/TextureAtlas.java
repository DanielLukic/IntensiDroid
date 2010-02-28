package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;

public interface TextureAtlas
    {
    boolean enoughRoomFor( final AndroidImageResource aImageResource );

    void add( final AndroidImageResource aImageResource );

    void purge();
    }
