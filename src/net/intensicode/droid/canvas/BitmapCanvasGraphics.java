package net.intensicode.droid.canvas;

import android.graphics.*;


public final class BitmapCanvasGraphics extends CanvasGraphics
    {
    public BitmapCanvasGraphics( final Bitmap aBitmap )
        {
        myActivePaint.setTextAlign( Paint.Align.LEFT );
        myCanvas = new Canvas( aBitmap );
        }

    // From DirectGraphics

    public final void initialize() throws Exception
        {
        throw new UnsupportedOperationException();
        }

    public final void beginFrame()
        {
        throw new UnsupportedOperationException();
        }

    public final void endFrame()
        {
        throw new UnsupportedOperationException();
        }

    public final void cleanup()
        {
        throw new UnsupportedOperationException();
        }
    }
