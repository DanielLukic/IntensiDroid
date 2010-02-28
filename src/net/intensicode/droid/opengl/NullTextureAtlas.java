package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;

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

    public final void purge()
        {
        throw new UnsupportedOperationException();
        }
    }
