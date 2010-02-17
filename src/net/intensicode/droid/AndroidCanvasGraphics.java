package net.intensicode.droid;

import android.graphics.*;
import net.intensicode.core.*;
import net.intensicode.util.*;


public final class AndroidCanvasGraphics extends DirectGraphics
    {
    public Canvas lockedCanvas;


    public AndroidCanvasGraphics()
        {
        myActivePaint.setTextAlign( Paint.Align.LEFT );
        }

    // From DirectGraphics

    public final int getColorRGB24()
        {
        return myActivePaint.getColor() & 0x00FFFFFF;
        }

    public final int getColorARGB32()
        {
        return myActivePaint.getColor();
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        myActivePaint.setColor( 0xFF000000 | ( aRGB24 & 0x00FFFFFF ) );
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        myActivePaint.setColor( aARGB32 );
        }

    public final void setFont( final FontResource aFont )
        {
        final AndroidFontResource fontResource = (AndroidFontResource) aFont;
        myActivePaint.setTypeface( fontResource.paint.getTypeface() );
        myActivePaint.setTextSize( fontResource.paint.getTextSize() );
        myActivePaint.getFontMetrics( myFontMetrics );
        }

    public void clearRGB24( final int aRGB24 )
        {
        lockedCanvas.drawColor( 0xFF000000 | ( aRGB24 & 0x00FFFFFF ) );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myActivePaint.setStyle( Paint.Style.STROKE );
        if ( aX1 == aX2 && aY1 == aY1 ) lockedCanvas.drawPoint( aX1, aY1, myActivePaint );
        else lockedCanvas.drawLine( aX1, aY1, aX2, aY2, myActivePaint );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myActivePaint.setStyle( Paint.Style.STROKE );
        lockedCanvas.drawRect( aX, aY, aX + aWidth, aY + aHeight, myActivePaint );
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        lockedCanvas.drawBitmap( aARGB32, aOffsetX, aScanlineSize, aX, aY, aWidth, aHeight, aUseAlpha, myImagePaint );
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myActivePaint.setStyle( Paint.Style.FILL );
        lockedCanvas.drawRect( aX, aY, aX + aWidth, aY + aHeight, myActivePaint );
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myTrianglePath.reset();
        myTrianglePath.moveTo( aX1, aY1 );
        myTrianglePath.lineTo( aX2, aY2 );
        myTrianglePath.lineTo( aX3, aY3 );
        myTrianglePath.lineTo( aX1, aY1 );
        myTrianglePath.close();
        myActivePaint.setStyle( Paint.Style.FILL );
        lockedCanvas.drawPath( myTrianglePath, myActivePaint );
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        myImagePaint.setAlpha( aAlpha256 );
        lockedCanvas.drawBitmap( imageResource.bitmap, aX, aY, myImagePaint );
        myImagePaint.setAlpha( 255 );
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        myImagePaint.setAlpha( aAlpha256 );
        mySourceRect.left = aSourceRect.x;
        mySourceRect.top = aSourceRect.y;
        mySourceRect.right = aSourceRect.x + aSourceRect.width;
        mySourceRect.bottom = aSourceRect.y + aSourceRect.height;
        myTargetRect.left = aX;
        myTargetRect.top = aY;
        myTargetRect.right = aX + aSourceRect.width;
        myTargetRect.bottom = aY + aSourceRect.height;
        lockedCanvas.drawBitmap( imageResource.bitmap, mySourceRect, myTargetRect, myImagePaint );
        myImagePaint.setAlpha( 255 );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        lockedCanvas.drawBitmap( imageResource.bitmap, aX, aY, myImagePaint );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int width = imageResource.getWidth();
        final int height = imageResource.getHeight();
        final Position aligned = DirectGraphics.getAlignedPosition( aX, aY, width, height, aAlignment );
        lockedCanvas.drawBitmap( imageResource.bitmap, aligned.x, aligned.y, myImagePaint );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        mySourceRect.left = aSourceRect.x;
        mySourceRect.top = aSourceRect.y;
        mySourceRect.right = aSourceRect.x + aSourceRect.width;
        mySourceRect.bottom = aSourceRect.y + aSourceRect.height;
        myTargetRect.left = aTargetX;
        myTargetRect.top = aTargetY;
        myTargetRect.right = aTargetX + aSourceRect.width;
        myTargetRect.bottom = aTargetY + aSourceRect.height;

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        lockedCanvas.drawBitmap( imageResource.bitmap, mySourceRect, myTargetRect, myImagePaint );
        }

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        lockedCanvas.drawText( aText, aStart, aEnd, aX, aY - myFontMetrics.ascent, myActivePaint );
        }

    public void drawChar( final char aCharCode, final int aX, final int aY )
        {
        myCharSequence.setLength( 0 );
        myCharSequence.append( aCharCode );
        lockedCanvas.drawText( myCharSequence, 0, 1, aX, aY - myFontMetrics.ascent, myActivePaint );
        }


    private Path myTrianglePath = new Path();

    private final Rect mySourceRect = new Rect();

    private final Rect myTargetRect = new Rect();

    private final Paint myImagePaint = new Paint();

    private final Paint myActivePaint = new Paint();

    private final StringBuilder myCharSequence = new StringBuilder();

    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    }
