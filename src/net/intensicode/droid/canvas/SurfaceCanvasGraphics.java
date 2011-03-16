package net.intensicode.droid.canvas;

import android.graphics.Paint;
import net.intensicode.droid.SurfaceProjection;


public final class SurfaceCanvasGraphics extends CanvasGraphics
    {
    public SurfaceCanvasGraphics( final SurfaceProjection aSurfaceProjection )
        {
        mySurfaceProjection = aSurfaceProjection;
        myActivePaint.setTextAlign( Paint.Align.LEFT );
        }

    // From DirectGraphics

    public final void initialize() throws Exception
        {
        }

    public final void beginFrame()
        {
        myCanvas = mySurfaceProjection.holder.lockCanvas();
        myCanvas.save();
        myCanvas.translate( mySurfaceProjection.offsetX, mySurfaceProjection.offsetY );
        myCanvas.scale( mySurfaceProjection.scaleX, mySurfaceProjection.scaleY );
        }

    public final void endFrame()
        {
        myCanvas.restore();

        final int screenWidth = mySurfaceProjection.screen.width;
        final int screenHeight = mySurfaceProjection.screen.height;

        final float offsetX = mySurfaceProjection.offsetX;
        final float offsetY = mySurfaceProjection.offsetY;

        if ( offsetY > 0 )
            {
            myCanvas.drawRect( 0, 0, screenWidth, offsetY, myClearPaint );
            myCanvas.drawRect( 0, screenHeight - offsetY, screenWidth, screenHeight, myClearPaint );
            }
        if ( offsetX > 0 )
            {
            myCanvas.drawRect( 0, 0, offsetX, screenHeight, myClearPaint );
            myCanvas.drawRect( screenWidth - offsetX, 0, screenWidth, screenHeight, myClearPaint );
            }

        mySurfaceProjection.holder.unlockCanvasAndPost( myCanvas );
        }

    public final void cleanup()
        {
        }

    private final Paint myClearPaint = new Paint();

    private final SurfaceProjection mySurfaceProjection;
    }
