package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import java.util.ArrayList;

public final class DirectTextureManager implements TexturePurger
    {
    public final void addTexture( final AndroidImageResource aImageResource )
        {
        Log.debug( "making texture for {}", aImageResource.resourcePath );

        aImageResource.texture = makeTexture( aImageResource.bitmap );
        aImageResource.texturePurger = this;
        myTexturizedImageResources.add( aImageResource );
        }

    public final void purgeAllTextures()
        {
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.get( 0 ) );
            }
        Assert.isTrue( "all textures purged", myTexturizedImageResources.isEmpty() );
        myTexturizedImageResources.clear();
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        Log.debug( "purging texture of {}", aImageResource.resourcePath );

        if ( aImageResource.texture instanceof DirectTexture )
            {
            final DirectTexture directTexture = (DirectTexture) aImageResource.texture;
            directTexture.purge();
            }
        aImageResource.texture = null;
        aImageResource.texturePurger = null;

        final boolean removed = myTexturizedImageResources.remove( aImageResource );
        Assert.isTrue( "failed removing texturized image from internal list", removed );
        }

    // Implementation

    private DirectTexture makeTexture( final Bitmap aOriginalBitmap )
        {
        final int originalWidth = aOriginalBitmap.getWidth();
        final int originalHeight = aOriginalBitmap.getHeight();
        if ( originalWidth > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();
        if ( originalHeight > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();

        final int properWidth = findNextPowerOfTwo( originalWidth );
        final int properHeight = findNextPowerOfTwo( originalHeight );

        if ( originalWidth == properWidth && originalHeight == properHeight )
            {
            final DirectTexture texture = new DirectTexture();
            texture.makeUsing( aOriginalBitmap );
            return texture;
            }
        else
            {
            final DirectTexture texture = new DirectTexture();
            texture.makeUsing( aOriginalBitmap, properWidth, properHeight );
            return texture;
            }
        }

    private int findNextPowerOfTwo( final int aPositiveInteger )
        {
        if ( aPositiveInteger == 0 ) return 1;
        int value = aPositiveInteger - 1;
        for ( int i = 1; i < MAX_TEXTURE_SIZE_SHIFT_BITS; i <<= 1 )
            {
            value |= value >> i;
            }
        return value + 1;
        }


    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();

    private static final int MAX_TEXTURE_SIZE_SHIFT_BITS = 10;

    private static final int MAX_TEXTURE_SIZE_IN_PIXELS = 512;
    }
