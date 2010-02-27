package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.GL10;


public final class GeometryDrawer
    {
    public GL10 gl;

    public boolean enableTextureCoordinates;


    public final void updateHardwareBuffers()
        {
        myFillRectSquare.freeHardwareBuffers( gl );
        myFillRectSquare.generateHardwareBuffers( gl );
        }

    public final void drawPoint( final int aX1, final int aY1 )
        {
        myTriangle.set( 0, aX1, aY1 );
        myTriangle.drawPoint( gl );
        myBuffersDirty = true;
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myTriangle.set( 0, aX1, aY1 );
        myTriangle.set( 1, aX2, aY2 );
        myTriangle.drawLine( gl );
        myBuffersDirty = true;
        }

    public final void drawSquare( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( myBuffersDirty ) updateBuffers();
        myFillRectSquare.draw( gl, aX, aY, aWidth, aHeight, enableTextureCoordinates );
        }

    public final void drawTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myTriangle.set( 0, aX1, aY1 );
        myTriangle.set( 1, aX2, aY2 );
        myTriangle.set( 2, aX3, aY3 );
        myTriangle.drawTriangle( gl );
        myBuffersDirty = true;
        }

    // Implementation

    private void updateBuffers()
        {
        myFillRectSquare.updateBuffers( gl, true );
        myBuffersDirty = false;
        }


    private boolean myBuffersDirty = true;

    private final MutableTriangle myTriangle = new MutableTriangle();

    private final StaticSquare myFillRectSquare = new StaticSquare();
    }
