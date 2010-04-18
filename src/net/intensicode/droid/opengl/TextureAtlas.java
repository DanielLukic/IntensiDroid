package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;
import android.graphics.Bitmap;

public interface TextureAtlas
    {
    boolean enoughRoomFor( final AndroidImageResource aImageResource );

    void add( final AndroidImageResource aImageResource );

    void purge();

    Bitmap dumpLayout();
    }
