package net.intensicode.droid.opengl;

import android.graphics.Rect;
import net.intensicode.core.*;
import net.intensicode.droid.AndroidFontResource;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.*;

public final class OpenglGraphics extends DirectGraphics
    {
    public final OpenglRenderer renderer;

    public OpenglGraphics( final OpenglRenderer aRenderer )
        {
        renderer = aRenderer;
        }

    // From DirectGraphics

    public final int getColorRGB24()
        {
        return myColorARGB32 & MASK_RGB24;
        }

    public final int getColorARGB32()
        {
        return myColorARGB32;
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        setColorARGB32( MASK_ALPHA32 | aRGB24 );
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        renderer.setColorARGB32( aARGB32 );
        myColorARGB32 = aARGB32;
        }

    public final void setFont( final FontResource aFont )
        {
        myFont = aFont;
        }

    public void clearRGB24( final int aRGB24 )
        {
        setColorRGB24( aRGB24 );
        fillRect( 0, 0, renderer.width(), renderer.height() );
        }

    public void clearARGB32( final int aARGB32 )
        {
        setColorARGB32( aARGB32 );
        fillRect( 0, 0, renderer.width(), renderer.height() );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        if ( aX1 == aX2 && aY1 == aY2 ) renderer.drawPoint( aX1, aY1 );
        else renderer.drawLine( aX1, aY1, aX2, aY2 );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        renderer.drawRect( aX, aY, aWidth, aHeight );
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        renderer.fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        renderer.fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        renderer.fillTriangle( aX1, aY1, aX2, aY2, aX3, aY3 );
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        blendImage( aImage, myFullRect, aX, aY, aAlpha256 );
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        if ( aAlpha256 == FULLY_TRANSPARENT ) return;
        if ( aAlpha256 == FULLY_OPAQUE ) drawImage( aImage, aSourceRect, aX, aY );

        renderer.enableAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        renderer.disableAlpha();
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        drawImage( aImage, myFullRect, aX, aY );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        final Position aligned = getAlignedPosition( aX, aY, aImage.getWidth(), aImage.getHeight(), aAlignment );
        drawImage( aImage, aligned.x, aligned.y );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        renderer.drawImage( aImage, aSourceRect, aTargetX, aTargetY );
        }

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        final AndroidFontResource resource = (AndroidFontResource) myFont;
        resource.paint.getTextBounds( aText, aStart, aEnd, mySubstringRect );

        if ( mySubstringBuffer != null && mySubstringBuffer.getWidth() < mySubstringRect.width() ) mySubstringBuffer = null;
        if ( mySubstringBuffer != null && mySubstringBuffer.getHeight() < mySubstringRect.height() ) mySubstringBuffer = null;
        if ( mySubstringBuffer == null ) mySubstringBuffer = AndroidImageResource.createFrom( mySubstringRect.width(), mySubstringRect.height() );

        final DirectGraphics graphics = mySubstringBuffer.getGraphics();
        graphics.setFont( myFont );
        graphics.drawSubstring( aText, aStart, aEnd, aX, aY );
        }

    public final void drawChar( final char aCharCode, final int aX, final int aY )
        {
        // TODO: Implement like drawSubstring.
        drawSubstring( Character.toString( aCharCode ), 0, 1, aX, aY );
        }

    public final void initialize() throws Exception
        {
        Log.info( "EXECUTING OPENGLGRAPHICS INITIALIZE" );
        renderer.initialize();
        }

    public final void beginFrame()
        {
        renderer.beginFrame();
        }

    public final void endFrame()
        {
        renderer.endFrame();
        }

    public final void cleanup()
        {
        Log.info( "EXECUTING OPENGLGRAPHICS CLEANUP" );
        renderer.destroySafely();
        }


    private int myColorARGB32;

    private FontResource myFont;

    private ImageResource mySubstringBuffer;

    private final Rect mySubstringRect = new Rect();

    private final Rectangle myFullRect = new Rectangle();

    private static final int MASK_RGB24 = 0x00FFFFFF;

    private static final int MASK_ALPHA32 = 0xFF000000;
    }
