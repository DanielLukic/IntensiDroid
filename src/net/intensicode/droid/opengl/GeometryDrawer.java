package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.GL10;


public final class GeometryDrawer
    {
    public GL10 gl;

    public boolean enableTextureCoordinates;


    public final void reset()
        {
        freeHardwareBuffers();
        myTextureBufferIsUpToDate = myVertexBufferIsUpToDate = false;
        }

    public final void updateHardwareBuffers()
        {
        freeHardwareBuffers();
        myFillRectRectangle.generateHardwareBuffers( gl );
        }

    public final void freeHardwareBuffers()
        {
        if ( myFillRectRectangle.hasHardwareBuffers() ) myFillRectRectangle.freeHardwareBuffers( gl );
        }

    public final void drawPoint( final int aX1, final int aY1 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.drawPoint( gl );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.drawLine( gl );
        myVertexBufferIsUpToDate = false;
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.set( 2, aX3, aY3 );
        myGeometry.fillTriangle( gl );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.set( 2, aX3, aY3 );
        myGeometry.set( 3, aX1, aY1 );
        myGeometry.drawTriangle( gl );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myGeometry.set( 0, aX, aY );
        myGeometry.set( 1, aX + aWidth, aY );
        myGeometry.set( 2, aX + aWidth, aY + aHeight );
        myGeometry.set( 3, aX, aY + aHeight );
        myGeometry.set( 4, aX, aY );
        myGeometry.drawRectangle( gl );
        myVertexBufferIsUpToDate = false;
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( !myTextureBufferIsUpToDate )
            {
            myFillRectRectangle.updateTextureBuffer( gl );
            myTextureBufferIsUpToDate = true;
            }

        if ( !myVertexBufferIsUpToDate )
            {
            myFillRectRectangle.updateVertexBuffer( gl );
            myVertexBufferIsUpToDate = true;
            }

        myFillRectRectangle.draw( gl, aX, aY, aWidth, aHeight );
        }


    private boolean myVertexBufferIsUpToDate;

    private boolean myTextureBufferIsUpToDate;

    private final MutableGeometry myGeometry = new MutableGeometry();

    private final StaticRectangle myFillRectRectangle = new StaticRectangle();
    }
