package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.droid.*;
import net.intensicode.util.Log;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class TextureManager implements TexturePurger
    {
    public GL10 gl;

    public boolean useGlUtils;


    public void addTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "making texture for {}", aImageResource.resourcePath );
        //#endif

        aImageResource.texture = makeTexture( aImageResource.bitmap );
        aImageResource.texturePurger = this;
        myTexturizedImageResources.add( aImageResource );
        }

    public void purgeAllTextures()
        {
        //#if DEBUG
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif
        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.get( myTexturizedImageResources.size() - 1 ) );
            }
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "purging texture {} ({})", aImageResource.texture.id, aImageResource.resourcePath );
        //#endif

        aImageResource.texture.purge();
        aImageResource.texture = null;
        aImageResource.texturePurger = null;

        final boolean removed = myTexturizedImageResources.remove( aImageResource );
        //#if DEBUG
        if ( !removed ) Log.debug( "failed removing texturized image from internal list" );
        //#endif
        }

    // Implementation

    private Texture makeTexture( final Bitmap aOriginalBitmap )
        {
        final int originalWidth = aOriginalBitmap.getWidth();
        final int originalHeight = aOriginalBitmap.getHeight();
        if ( originalWidth > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();
        if ( originalHeight > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();

        final int properWidth = findNextPowerOfTwo( originalWidth );
        final int properHeight = findNextPowerOfTwo( originalHeight );

        if ( originalWidth == properWidth && originalHeight == properHeight )
            {
            final Texture texture = new Texture( gl );
            texture.makeUsing( aOriginalBitmap );
            return texture;
            }
        else
            {
            final Texture texture = new Texture( gl );
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
