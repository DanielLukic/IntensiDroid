package net.intensicode.droid.opengl;

import net.intensicode.core.*;
import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.GL;


public final class OpenglGraphics extends DirectGraphics
    {
    public GL gl;

    // From DirectGraphics

    public void clearRGB24( final int aRGB24 )
        {
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        }

    public final void setFont( final FontResource aFont )
        {
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        }

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        }

    public void drawChar( final char aCharCode, final int aX, final int aY )
        {
        }
    }
