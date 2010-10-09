package net.intensicode.droid.canvas;

import android.graphics.*;
import android.view.SurfaceHolder;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;


public final class CanvasGraphics extends DirectGraphics
    {
    public float scale;

    public int offsetX;

    public int offsetY;

    private final Size myScreenSize = new Size();

    private final Size myTargetSize = new Size();

    public final void setScreenSize( final int aWidth, final int aHeight )
        {
        myScreenSize.width = aWidth;
        myScreenSize.height = aHeight;
        }

    public final void setTargetSize( final int aWidth, final int aHeight )
        {
        myTargetSize.width = aWidth;
        myTargetSize.height = aHeight;
        }

    public SurfaceHolder surfaceHolder;


    public CanvasGraphics()
        {
        myActivePaint.setTextAlign( Paint.Align.LEFT );
        }

    public CanvasGraphics( final Bitmap aBitmap )
        {
        this();
        myCanvas = new Canvas( aBitmap );
        myClearPaint.setARGB( 255, 0, 0, 0 );
        myClearPaint.setStyle( Paint.Style.FILL );
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
        myCanvas.drawColor( 0xFF000000 | ( aRGB24 & 0x00FFFFFF ) );
        }

    public void clearARGB32( final int aARGB32 )
        {
        myCanvas.drawColor( aARGB32 );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myActivePaint.setStyle( Paint.Style.STROKE );
        if ( aX1 == aX2 && aY1 == aY1 ) myCanvas.drawPoint( aX1, aY1, myActivePaint );
        else myCanvas.drawLine( aX1, aY1, aX2, aY2, myActivePaint );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myActivePaint.setStyle( Paint.Style.STROKE );
        myCanvas.drawRect( aX, aY, aX + aWidth, aY + aHeight, myActivePaint );
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        myCanvas.drawBitmap( aARGB32, aOffsetX, aScanlineSize, aX, aY, aWidth, aHeight, aUseAlpha, myImagePaint );
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myActivePaint.setStyle( Paint.Style.FILL );
        myCanvas.drawRect( aX, aY, aX + aWidth, aY + aHeight, myActivePaint );
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
        myCanvas.drawPath( myTrianglePath, myActivePaint );
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        if ( aAlpha256 == FULLY_TRANSPARENT )
            {
            // Nothing to do..
            }
        else if ( aAlpha256 == FULLY_OPAQUE )
            {
            drawImage( aImage, aX, aY );
            }
        else
            {
            final AndroidImageResource imageResource = (AndroidImageResource) aImage;
            myImagePaint.setAlpha( aAlpha256 );
            myCanvas.drawBitmap( imageResource.bitmap, aX, aY, myImagePaint );
            myImagePaint.setAlpha( 255 );
            }
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        if ( aAlpha256 == FULLY_TRANSPARENT )
            {
            // Nothing to do..
            }
        else if ( aAlpha256 == FULLY_OPAQUE )
            {
            drawImage( aImage, aSourceRect, aX, aY );
            }
        else
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
            myCanvas.drawBitmap( imageResource.bitmap, mySourceRect, myTargetRect, myImagePaint );
            myImagePaint.setAlpha( 255 );
            }
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        myCanvas.drawBitmap( imageResource.bitmap, aX, aY, myImagePaint );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int width = imageResource.getWidth();
        final int height = imageResource.getHeight();
        final Position aligned = DirectGraphics.getAlignedPosition( aX, aY, width, height, aAlignment );
        myCanvas.drawBitmap( imageResource.bitmap, aligned.x, aligned.y, myImagePaint );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        mySourceRect.left = aSourceRect.x;
        mySourceRect.top = aSourceRect.y;
        mySourceRect.right = aSourceRect.x + aSourceRect.width;
        mySourceRect.bottom = aSourceRect.y + aSourceRect.height;
        myTargetRect.left = aTargetX;
        myTargetRect.top = aTargetY;
        myTargetRect.right = aTargetX + aSourceRect.width;
        myTargetRect.bottom = aTargetY + aSourceRect.height;

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        myCanvas.drawBitmap( imageResource.bitmap, mySourceRect, myTargetRect, myImagePaint );
        }

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        myCanvas.drawText( aText, aStart, aEnd, aX, aY - myFontMetrics.ascent, myActivePaint );
        }

    public final void drawChar( final char aCharCode, final int aX, final int aY )
        {
        myCharSequence.delete( 0, myCharSequence.length() );
        myCharSequence.append( aCharCode );
        myCanvas.drawText( myCharSequence, 0, 1, aX, aY - myFontMetrics.ascent, myActivePaint );
        }

    public final void beginFrame()
        {
        myCanvas = surfaceHolder.lockCanvas();

        final float scaleX = myScreenSize.width / (float) myTargetSize.width;
        final float scaleY = myScreenSize.height / (float) myTargetSize.height;

        scale = Math.min( scaleX, scaleY );

        offsetX = (int) (( myScreenSize.width - myTargetSize.width * scale ) / 2);
        offsetY = (int) (( myScreenSize.height - myTargetSize.height * scale ) / 2);

        myCanvas.save();
        myCanvas.translate( offsetX, offsetY );
        myCanvas.scale( scale, scale );
        }

    public final void endFrame()
        {
        myCanvas.restore();
        if ( offsetY > 0 )
            {
            myCanvas.drawRect( 0, 0, myScreenSize.width, offsetY, myClearPaint );
            myCanvas.drawRect( 0, myScreenSize.height - offsetY, myScreenSize.width, myScreenSize.height, myClearPaint );
            }
        if ( offsetX > 0 )
            {
            myCanvas.drawRect( 0, 0, offsetX, myScreenSize.height, myClearPaint );
            myCanvas.drawRect( myScreenSize.width - offsetX, 0, myScreenSize.width, myScreenSize.height, myClearPaint );
            }
        surfaceHolder.unlockCanvasAndPost( myCanvas );
        }


    private Canvas myCanvas;

    private Path myTrianglePath = new Path();

    private final Rect mySourceRect = new Rect();

    private final Rect myTargetRect = new Rect();

    private final Paint myImagePaint = new Paint();

    private final Paint myActivePaint = new Paint();

    private final Paint myClearPaint = new Paint();

    private final StringBuilder myCharSequence = new StringBuilder();

    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    }
