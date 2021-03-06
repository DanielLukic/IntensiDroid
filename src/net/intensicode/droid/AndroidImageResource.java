package net.intensicode.droid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import net.intensicode.core.DirectGraphics;
import net.intensicode.core.ImageResource;
import net.intensicode.droid.canvas.BitmapCanvasGraphics;
import net.intensicode.droid.canvas.CanvasGraphics;
import net.intensicode.droid.opengl.Texture;
import net.intensicode.util.Assert;
import net.intensicode.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

public final class AndroidImageResource implements ImageResource
    {
    public final String resourcePath;

    public final Bitmap bitmap;

    public TexturePurger texturePurger;

    public Texture texture;


    public static ImageResource createFrom( final String aResourcePath, final InputStream aResourceStream )
        {
        final Bitmap bitmap = BitmapFactory.decodeStream( aResourceStream );
        final AndroidImageResource resource = new AndroidImageResource( aResourcePath, bitmap );
        theResources.add( resource );
        return resource;
        }

    public static ImageResource createFrom( final int aWidth, final int aHeight )
        {
        final Bitmap bitmap = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        final AndroidImageResource resource = new AndroidImageResource( bitmap );
        theResources.add( resource );
        return resource;
        }

    public static ImageResource createFrom( final Bitmap aBitmap )
        {
        final AndroidImageResource resource = new AndroidImageResource( aBitmap );
        theResources.add( resource );
        return resource;
        }

    public static void purgeAll()
        {
        Log.debug( "purging {} AndroidImageResource instances", theResources.size() );
        while ( !theResources.isEmpty() ) theResources.get( 0 ).purge();
        }

    public static void purgeAllTextures()
        {
        Log.debug( "purging {} AndroidImageResource textures", theResources.size() );
        for ( final AndroidImageResource resource : theResources )
            {
            if ( resource.texture != null ) resource.texturePurger.purge( resource );
            }
        }

    private static final ArrayList<AndroidImageResource> theResources = new ArrayList<AndroidImageResource>();

    private AndroidImageResource( final Bitmap aBitmap )
        {
        this( makeRuntimeImageId( aBitmap ), aBitmap );
        }

    private static String makeRuntimeImageId( final Bitmap aBitmap )
        {
        return aBitmap.getWidth() + "x" + aBitmap.getHeight();
        }

    private AndroidImageResource( final String aResourcePath, final Bitmap aBitmap )
        {
        //#if DEBUG
        Assert.notNull( "bitmap must be valid", aBitmap );
        //#endif
        resourcePath = aResourcePath;
        bitmap = aBitmap;
        }

    public final void dropTexture()
        {
        texture = null;
        texturePurger = null;
        }

    // From ImageResource

    public final int getWidth()
        {
        return bitmap.getWidth();
        }

    public final int getHeight()
        {
        return bitmap.getHeight();
        }

    public final DirectGraphics getGraphics()
        {
        if ( myGraphics == null ) myGraphics = new BitmapCanvasGraphics( bitmap );
        return myGraphics;
        }

    public final void getRGB( final int[] aBuffer, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight )
        {
        bitmap.getPixels( aBuffer, aOffsetX, aScanlineSize, aX, aY, aWidth, aHeight );
        }

    public final void purge()
        {
        Log.debug( "puring AndroidImageResource {}", resourcePath );
        final boolean removed = theResources.remove( this );
        if ( !removed ) Log.debug( "purged unknown AndroidImageResource" );
        if ( texture != null ) texturePurger.purge( this );
        }

    // From Object

    //#if DEBUG

    public String toString()
        {
        final StringBuilder builder = new StringBuilder( resourcePath );
        builder.append( '[' );
        builder.append( getWidth() );
        builder.append( 'x' );
        builder.append( getHeight() );
        builder.append( ']' );
        return builder.toString();
        }

    //#endif


    private CanvasGraphics myGraphics;
    }
