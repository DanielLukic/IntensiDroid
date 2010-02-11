package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.*;
import java.nio.*;

class StaticSquare
    {
    public StaticSquare()
        {
        final int vertsAcross = 2;
        final int vertsDown = 2;

        mW = vertsAcross;

        int size = vertsAcross * vertsDown;
        final int FLOAT_SIZE = 4;
        final int CHAR_SIZE = 2;

        mVertexBuffer = ByteBuffer.allocateDirect( FLOAT_SIZE * size * 3 )
                .order( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoordBuffer = ByteBuffer.allocateDirect( FLOAT_SIZE * size * 2 )
                .order( ByteOrder.nativeOrder() ).asFloatBuffer();

        int quadW = mW - 1;
        int quadH = vertsDown - 1;
        int quadCount = quadW * quadH;
        int indexCount = quadCount * 6;
        mIndexCount = indexCount;
        mIndexBuffer = ByteBuffer.allocateDirect( CHAR_SIZE * indexCount )
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
        int index = mW * j + i;

        int posIndex = index * 3;
        mVertexBuffer.put( posIndex, x );
        mVertexBuffer.put( posIndex + 1, y );
        mVertexBuffer.put( posIndex + 2, z );

        int texIndex = index * 2;
        mTexCoordBuffer.put( texIndex, u );
        mTexCoordBuffer.put( texIndex + 1, v );
        }

    public final void updateBuffers( final GL10 gl, boolean useTexture )
        {
        if ( mVertBufferIndex == 0 )
            {
            gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, mVertexBuffer );

            if ( useTexture ) gl.glTexCoordPointer( 2, GL10.GL_FLOAT, 0, mTexCoordBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) gl;

            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
            gl11.glVertexPointer( 3, GL10.GL_FLOAT, 0, 0 );

            if ( useTexture )
                {
                gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
                gl11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );
                }

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
        if ( mVertBufferIndex == 0 )
            {
            gl.glDrawElements( GL10.GL_TRIANGLES, mIndexCount,
                               GL10.GL_UNSIGNED_SHORT, mIndexBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) gl;
            gl11.glDrawElements( GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_SHORT, 0 );
            }
        }

    private void original_draw( GL10 gl, boolean useTexture )
        {
        if ( mVertBufferIndex == 0 )
            {
            gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, mVertexBuffer );

            if ( useTexture ) gl.glTexCoordPointer( 2, GL10.GL_FLOAT, 0, mTexCoordBuffer );

            gl.glDrawElements( GL10.GL_TRIANGLES, mIndexCount,
                               GL10.GL_UNSIGNED_SHORT, mIndexBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) gl;

            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
            gl11.glVertexPointer( 3, GL10.GL_FLOAT, 0, 0 );

            if ( useTexture )
                {
                gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
                gl11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );
                }

            gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex );
            gl11.glDrawElements( GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_SHORT, 0 );

            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, 0 );
            gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, 0 );
            }
        }

    public void forgetHardwareBuffers()
        {
        mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        mTextureCoordBufferIndex = 0;
        }

    public void freeHardwareBuffers( GL10 gl )
        {
        if ( mVertBufferIndex == 0 ) return;
        if ( !( gl instanceof GL11 ) ) return;

        GL11 gl11 = (GL11) gl;
        int[] buffer = new int[1];
        buffer[ 0 ] = mVertBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        buffer[ 0 ] = mTextureCoordBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        buffer[ 0 ] = mIndexBufferIndex;
        gl11.glDeleteBuffers( 1, buffer, 0 );

        forgetHardwareBuffers();
        }

    public void generateHardwareBuffers( GL10 gl )
        {
        if ( mVertBufferIndex != 0 ) return;
        if ( !( gl instanceof GL11 ) ) return;

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


    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTexCoordBuffer;

    private CharBuffer mIndexBuffer;

    private int mW;

    private int mIndexCount;

    private int mVertBufferIndex;

    private int mIndexBufferIndex;

    private int mTextureCoordBufferIndex;
    }
