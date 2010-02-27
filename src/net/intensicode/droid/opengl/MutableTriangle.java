package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.GL10;
import java.nio.*;

class MutableTriangle
    {
    MutableTriangle()
        {
        final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

        myIndexBuffer = ByteBuffer.allocateDirect( CHAR_SIZE * MAX_VERTICES ).order( NATIVE_ORDER ).asCharBuffer();
        myVertexBuffer = ByteBuffer.allocateDirect( FLOAT_SIZE * MAX_VERTICES * DIMENSIONS ).order( NATIVE_ORDER ).asFloatBuffer();

        myIndexBuffer.put( 0, (char) 0 );
        myIndexBuffer.put( 1, (char) 1 );
        myIndexBuffer.put( 2, (char) 2 );

        // Clear z values..
        myVertexBuffer.put( 2, 0 );
        myVertexBuffer.put( 4, 0 );
        myVertexBuffer.put( 6, 0 );
        }

    final void set( final int aVertexIndex, final float aX, final float aY )
        {
        final int bufferIndex = aVertexIndex * DIMENSIONS;
        myVertexBuffer.put( bufferIndex, aX );
        myVertexBuffer.put( bufferIndex + 1, aY );
        }

    final void drawTriangle( final GL10 gl )
        {
        gl.glVertexPointer( DIMENSIONS, GL10.GL_FLOAT, 0, myVertexBuffer );
        gl.glDrawElements( GL10.GL_TRIANGLES, 3, GL10.GL_UNSIGNED_SHORT, myIndexBuffer );
        }

    final void drawLine( final GL10 gl )
        {
        gl.glVertexPointer( DIMENSIONS, GL10.GL_FLOAT, 0, myVertexBuffer );
        gl.glDrawElements( GL10.GL_LINES, 2, GL10.GL_UNSIGNED_SHORT, myIndexBuffer );
        }

    final void drawPoint( final GL10 gl )
        {
        gl.glVertexPointer( DIMENSIONS, GL10.GL_FLOAT, 0, myVertexBuffer );
        gl.glDrawElements( GL10.GL_POINTS, 1, GL10.GL_UNSIGNED_SHORT, myIndexBuffer );
        }


    private CharBuffer myIndexBuffer;

    private FloatBuffer myVertexBuffer;

    private final static int CHAR_SIZE = 2;

    private final static int FLOAT_SIZE = 4;

    private final static int DIMENSIONS = 3;

    private final static int MAX_VERTICES = 3;
    }
