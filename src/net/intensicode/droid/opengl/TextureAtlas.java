package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Position;
import android.graphics.Bitmap;

public interface TextureAtlas
    {
    boolean enoughRoomFor( final AndroidImageResource aImageResource );

    void add( final AndroidImageResource aImageResource );

    void add( final AndroidImageResource aImageResource, final Position aInsertPosition );

    void purge();

    Bitmap dumpLayout();
    }
