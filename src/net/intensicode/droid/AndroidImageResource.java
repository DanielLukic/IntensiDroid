package net.intensicode.droid;

import android.graphics.Bitmap;
import net.intensicode.core.*;
import net.intensicode.droid.canvas.AndroidCanvasGraphics;
import net.intensicode.droid.opengl.Texture;
import net.intensicode.util.Assert;

public final class AndroidImageResource implements ImageResource
    {
    public static final String RUNTIME_IMAGE = "[runtime_image]";

    public final String resourcePath;

    public final Bitmap bitmap;

    public TexturePurger texturePurger;

    public Texture texture;


    public AndroidImageResource( final Bitmap aBitmap )
        {
        this( RUNTIME_IMAGE, aBitmap );
        }

    public AndroidImageResource( final String aResourcePath, final Bitmap aBitmap )
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
        if ( myGraphics == null ) myGraphics = new AndroidCanvasGraphics( bitmap );
        return myGraphics;
        }

    public final void getRGB( final int[] aBuffer, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight )
        {
        bitmap.getPixels( aBuffer, aOffsetX, aScanlineSize, aX, aY, aWidth, aHeight );
        }

    public final void purge()
        {
        bitmap.recycle();
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


    private AndroidCanvasGraphics myGraphics;
    }
