package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Position;

public final class NullTextureAtlas implements TextureAtlas
    {
    public final boolean enoughRoomFor( final AndroidImageResource aImageResource )
        {
        return false;
        }

    public final void add( final AndroidImageResource aImageResource )
        {
        throw new UnsupportedOperationException();
        }

    public final void add( final AndroidImageResource aImageResource, final Position aInsertPosition )
        {
        throw new UnsupportedOperationException();
        }

    public final void purge()
        {
        throw new UnsupportedOperationException();
        }

    public final Bitmap dumpLayout()
        {
        throw new UnsupportedOperationException();
        }
    }
