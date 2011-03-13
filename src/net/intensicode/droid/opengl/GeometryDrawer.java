package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.GL10;


public final class GeometryDrawer
    {
    public final void attach( final GL10 aGL )
        {
        if ( aGL == null ) throw new NullPointerException( "real GL must not be null" );
        myGL = aGL;
        }

    public final void reset()
        {
        freeHardwareBuffers();
        myTextureBufferIsUpToDate = myVertexBufferIsUpToDate = false;
        }

    public final void updateHardwareBuffers()
        {
        freeHardwareBuffers();
        myFillRectRectangle.generateHardwareBuffers( myGL );
        }

    public final void freeHardwareBuffers()
        {
        if ( myFillRectRectangle.hasHardwareBuffers() ) myFillRectRectangle.freeHardwareBuffers( myGL );
        }

    public final void drawPoint( final int aX1, final int aY1 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.drawPoint( myGL );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.drawLine( myGL );
        myVertexBufferIsUpToDate = false;
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.set( 2, aX3, aY3 );
        myGeometry.fillTriangle( myGL );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myGeometry.set( 0, aX1, aY1 );
        myGeometry.set( 1, aX2, aY2 );
        myGeometry.set( 2, aX3, aY3 );
        myGeometry.set( 3, aX1, aY1 );
        myGeometry.drawTriangle( myGL );
        myVertexBufferIsUpToDate = false;
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myGeometry.set( 0, aX, aY );
        myGeometry.set( 1, aX + aWidth, aY );
        myGeometry.set( 2, aX + aWidth, aY + aHeight );
        myGeometry.set( 3, aX, aY + aHeight );
        myGeometry.set( 4, aX, aY );
        myGeometry.drawRectangle( myGL );
        myVertexBufferIsUpToDate = false;
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( !myTextureBufferIsUpToDate )
            {
            myFillRectRectangle.updateTextureBuffer( myGL );
            myTextureBufferIsUpToDate = true;
            }

        if ( !myVertexBufferIsUpToDate )
            {
            myFillRectRectangle.updateVertexBuffer( myGL );
            myVertexBufferIsUpToDate = true;
            }

        myFillRectRectangle.draw( myGL, aX, aY, aWidth, aHeight );
        }


    private GL10 myGL = NoGL.INSTANCE;

    private boolean myVertexBufferIsUpToDate;

    private boolean myTextureBufferIsUpToDate;

    private final MutableGeometry myGeometry = new MutableGeometry();

    private final StaticRectangle myFillRectRectangle = new StaticRectangle();
    }
