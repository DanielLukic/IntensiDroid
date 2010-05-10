package net.intensicode.droid.opengl;

import javax.microedition.khronos.opengles.*;
import java.nio.*;

class StaticRectangle
    {
    public StaticRectangle()
        {
        mW = VERTS_ACROSS;

        final int size = VERTS_ACROSS * VERTS_DOWN;

        mVertexBuffer = ByteBuffer.allocateDirect( BYTES_PER_FLOAT * size * VERTEX_COORDS_PER_ENTRY )
                .order( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoordBuffer = ByteBuffer.allocateDirect( BYTES_PER_FLOAT * size * TEX_COORDS_PER_ENTRY )
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

        final int posIndex = index * VERTEX_COORDS_PER_ENTRY;
        mVertexBuffer.put( posIndex, x );
        mVertexBuffer.put( posIndex + 1, y );
        mVertexBuffer.put( posIndex + 2, z );

        final int texIndex = index * TEX_COORDS_PER_ENTRY;
        mTexCoordBuffer.put( texIndex, u );
        mTexCoordBuffer.put( texIndex + 1, v );
        }

    public final void updateTextureBuffer( final GL10 aGL )
        {
        if ( !hasHardwareBuffers() )
            {
            aGL.glTexCoordPointer( TEX_COORDS_PER_ENTRY, GL10.GL_FLOAT, 0, mTexCoordBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) aGL;
            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
            gl11.glTexCoordPointer( TEX_COORDS_PER_ENTRY, GL11.GL_FLOAT, 0, 0 );
            }
        }

    public final void updateVertexBuffer( final GL10 aGL )
        {
        if ( !hasHardwareBuffers() )
            {
            aGL.glVertexPointer( VERTEX_COORDS_PER_ENTRY, GL10.GL_FLOAT, 0, mVertexBuffer );
            }
        else
            {
            final GL11 gl11 = (GL11) aGL;
            gl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
            gl11.glVertexPointer( VERTEX_COORDS_PER_ENTRY, GL10.GL_FLOAT, 0, 0 );

            gl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex );
            }
        }

    public void draw( GL10 gl, int aX, int aY, int aWidth, int aHeight )
        {
        gl.glPushMatrix();
        gl.glTranslatef( aX, aY, 0 );
        gl.glScalef( aWidth, aHeight, 1 );
        drawDirect( gl );
        gl.glPopMatrix();
        }

    private void drawDirect( GL10 gl )
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

    public final boolean hasHardwareBuffers()
        {
        return mVertBufferIndex != 0;
        }

    public void freeHardwareBuffers( GL10 gl )
        {
        if ( !hasHardwareBuffers() ) return;

        GL11 gl11 = (GL11) gl;

        myBufferWorkspace[ 0 ] = mVertBufferIndex;
        gl11.glDeleteBuffers( 1, myBufferWorkspace, 0 );

        myBufferWorkspace[ 0 ] = mTextureCoordBufferIndex;
        gl11.glDeleteBuffers( 1, myBufferWorkspace, 0 );

        myBufferWorkspace[ 0 ] = mIndexBufferIndex;
        gl11.glDeleteBuffers( 1, myBufferWorkspace, 0 );

        mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        mTextureCoordBufferIndex = 0;
        }

    public void generateHardwareBuffers( GL10 gl )
        {
        if ( hasHardwareBuffers() ) freeHardwareBuffers( gl );

        GL11 gl11 = (GL11) gl;
        generateIndexBuffer( gl11 );
        generateVertexBuffer( gl11 );
        generateTexCoordBuffer( gl11 );
        }

    private void generateTexCoordBuffer( final GL11 aGl11 )
        {
        aGl11.glGenBuffers( 1, myBufferWorkspace, 0 );
        mTextureCoordBufferIndex = myBufferWorkspace[ 0 ];
        aGl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex );
        final int texCoordSize = mTexCoordBuffer.capacity() * BYTES_PER_FLOAT;
        aGl11.glBufferData( GL11.GL_ARRAY_BUFFER, texCoordSize, mTexCoordBuffer, GL11.GL_STATIC_DRAW );

        aGl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, 0 );
        }

    private void generateVertexBuffer( final GL11 aGl11 )
        {
        aGl11.glGenBuffers( 1, myBufferWorkspace, 0 );
        mVertBufferIndex = myBufferWorkspace[ 0 ];
        aGl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, mVertBufferIndex );
        final int vertexSize = mVertexBuffer.capacity() * BYTES_PER_FLOAT;
        aGl11.glBufferData( GL11.GL_ARRAY_BUFFER, vertexSize, mVertexBuffer, GL11.GL_STATIC_DRAW );

        aGl11.glBindBuffer( GL11.GL_ARRAY_BUFFER, 0 );
        }

    private void generateIndexBuffer( final GL11 aGl11 )
        {
        aGl11.glGenBuffers( 1, myBufferWorkspace, 0 );
        mIndexBufferIndex = myBufferWorkspace[ 0 ];
        aGl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex );
        final int indexSize = mIndexBuffer.capacity() * BYTES_PER_CHAR;
        aGl11.glBufferData( GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, mIndexBuffer, GL11.GL_STATIC_DRAW );

        aGl11.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, 0 );
        }


    private int mW;

    private int mIndexCount;

    private int mVertBufferIndex;

    private int mIndexBufferIndex;

    private int mTextureCoordBufferIndex;

    private CharBuffer mIndexBuffer;

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTexCoordBuffer;

    private final int[] myBufferWorkspace = new int[1];

    private static final int BYTES_PER_FLOAT = 4;

    private static final int BYTES_PER_CHAR = 2;

    private static final int VERTS_DOWN = 2;

    private static final int VERTS_ACROSS = 2;

    private static final int TEX_COORDS_PER_ENTRY = 2;

    private static final int VERTEX_COORDS_PER_ENTRY = 3;
    }
