package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.*;
import java.nio.*;

class StaticSquare
    {
    public StaticSquare()
        {
        mW = VERTS_ACROSS;

        final int size = VERTS_ACROSS * VERTS_DOWN;

        mVertexBuffer = ByteBuffer.allocateDirect( BYTES_PER_FLOAT * size * BYTES_PER_VERTEX_COORD )
                .order( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoordBuffer = ByteBuffer.allocateDirect( BYTES_PER_FLOAT * size * BYTES_PER_TEX_COORD )
                .order( ByteOrder.nativeOrder() ).asFloatBuffer();

        int quadW = mW - 1;
        int quadH = VERTS_DOWN - 1;
        int quadCount = quadW * quadH;
        int indexCount = quadCount * 6;
        mIndexCount = indexCount;
        mIndexBuffer = ByteBuffer.allocateDirect( BYTES_PER_CHAR * indexCount )
                .order( ByteOrder.nativeOrder() ).asCharBuffer();

        int i = 0;
        for ( int y = 0; y < quadH; y++ )
            {
            for ( int x = 0; x < quadW; x++ )
                {
                char a = (char) ( y * mW + x );
                char b = (char) ( y * mW + x + 1 );
                char c = (char) ( ( y + 1 ) * mW + x );
                char d = (char) ( ( y + 1 ) * mW + x + 1 );

                mIndexBuffer.put( i++, a );
                mIndexBuffer.put( i++, b );
                mIndexBuffer.put( i++, c );

                mIndexBuffer.put( i++, b );
                mIndexBuffer.put( i++, c );
                mIndexBuffer.put( i++, d );
                }
            }

        mVertBufferIndex = 0;

        set( 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
        set( 1, 0, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f );
        set( 0, 1, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f );
        set( 1, 1, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f );
        }

    private void set( int i, int j, float x, float y, float z, float u, float v )
        {
        final int index = mW * j + i;

        final int posIndex = index * BYTES_PER_VERTEX_COORD;
        mVertexBuffer.put( posIndex, x );
        mVertexBuffer.put( posIndex + 1, y );
        mVertexBuffer.put( posIndex + 2, z );

        final int texIndex = index * BYTES_PER_TEX_COORD;
        mTexCoordBuffer.put( texIndex, u );
        mTexCoordBuffer.put( texIndex + 1, v );
        }

    public final void updateTextureBuffer( final GL10 aGL )
        {
        if ( !hasHardwareBuffers() )
            {
            aGL.glTexCoordPointer( BYTES_PER_TEX_COORD, GL10.GL_FLOAT, 0, mTexCoordBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) aGL;
            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
            gl11.glTexCoordPointer( BYTES_PER_TEX_COORD, GL11.GL_FLOAT, 0, 0 );
            }
        }

    public final void updateVertexBuffer( final GL10 aGL )
        {
        if ( !hasHardwareBuffers() )
            {
            aGL.glVertexPointer( BYTES_PER_VERTEX_COORD, GL10.GL_FLOAT, 0, mVertexBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) aGL;
            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
            gl11.glVertexPointer( BYTES_PER_VERTEX_COORD, GL10.GL_FLOAT, 0, 0 );

            gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex );
            }
        }

    public void draw( GL10 gl, int aX, int aY, int aWidth, int aHeight, boolean useTexture )
        {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef( aX, aY, 0 );
        gl.glScalef( aWidth, aHeight, 1 );
        drawDirect( gl, useTexture );
        gl.glPopMatrix();
        }

    private void drawDirect( GL10 gl, boolean useTexture )
        {
        if ( !hasHardwareBuffers() )
            {
            gl.glDrawElements( GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) gl;
            gl11.glDrawElements( GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_SHORT, 0 );
            }
        }

    public void freeHardwareBuffers( GL10 gl )
        {
        if ( !hasHardwareBuffers() ) throw new IllegalStateException();

        GL11 gl11 = (GL11) gl;
        int[] buffer = new int[1];
        buffer[ 0 ] = mVertBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        buffer[ 0 ] = mTextureCoordBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        buffer[ 0 ] = mIndexBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        mTextureCoordBufferIndex = 0;
        }

    public void generateHardwareBuffers( GL10 gl )
        {
        if ( hasHardwareBuffers() ) throw new IllegalStateException();

        GL11 gl11 = (GL11) gl;
        int[] buffer = new int[1];

        // Allocate and fill the vertex buffer.
        gl11.glGenBuffers( 1, buffer, 0 );
        mVertBufferIndex = buffer[ 0 ];
        gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
        final int vertexSize = mVertexBuffer.capacity() * Float.SIZE;
        gl11.glBufferData( GL11.GL_ARRAY_BUFFER, vertexSize, mVertexBuffer, GL11.GL_STATIC_DRAW );

        // Allocate and fill the texture coordinate buffer.
        gl11.glGenBuffers( 1, buffer, 0 );
        mTextureCoordBufferIndex = buffer[ 0 ];
        gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
        final int texCoordSize = mTexCoordBuffer.capacity() * Float.SIZE;
        gl11.glBufferData( GL11.GL_ARRAY_BUFFER, texCoordSize, mTexCoordBuffer, GL11.GL_STATIC_DRAW );

        // Unbind the array buffer.
        gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, 0 );

        // Allocate and fill the index buffer.
        gl11.glGenBuffers( 1, buffer, 0 );
        mIndexBufferIndex = buffer[ 0 ];
        gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex );
        // A char is 2 bytes.
        final int indexSize = mIndexBuffer.capacity() * 2;
        gl11.glBufferData( GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, mIndexBuffer, GL11.GL_STATIC_DRAW );

        // Unbind the element array buffer.
        gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, 0 );
        }

    private boolean hasHardwareBuffers()
        {
        return mVertBufferIndex != 0;
        }


    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTexCoordBuffer;

    private CharBuffer mIndexBuffer;

    private int mW;

    private int mIndexCount;

    private int mVertBufferIndex;

    private int mIndexBufferIndex;

    private int mTextureCoordBufferIndex;

    private static final int BYTES_PER_FLOAT = 4;

    private static final int BYTES_PER_CHAR = 2;

    private static final int VERTS_DOWN = 2;

    private static final int VERTS_ACROSS = 2;

    private static final int BYTES_PER_TEX_COORD = 2;

    private static final int BYTES_PER_VERTEX_COORD = 3;
    }
