package net.intensicode.droid.opengl;

import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;

public final class Texture
    {
    public int id;

    public int width;

    public int height;

    public int originalWidth;

    public int originalHeight;


    public final void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / (float) originalWidth;
        aMatrix4x4[ 5 ] = -aSourceRect.height / (float) originalHeight;
        aMatrix4x4[ 12 ] = aSourceRect.x / (float) originalWidth;
        aMatrix4x4[ 13 ] = aSourceRect.y / (float) originalHeight - aMatrix4x4[ 5 ];
        }

    public final boolean cropTexture( final GL11 aGL, final Rectangle aRect )
        {
        if ( myActiveCropRect.equals( aRect ) ) return false;

        final float xFactor = width / (float) originalWidth;
        final float yFactor = height / (float) originalHeight;
        final float x = aRect.x * xFactor;
        final float y = aRect.y * yFactor;
        final float scaledWidth = aRect.width * xFactor;
        final float scaledHeight = aRect.height * yFactor;
        theCropWorkspace[ 0 ] = (int) x;
        theCropWorkspace[ 1 ] = (int) ( y + scaledHeight );
        theCropWorkspace[ 2 ] = (int) scaledWidth;
        theCropWorkspace[ 3 ] = (int) -scaledHeight;

        aGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );

        myActiveCropRect.setTo( aRect );

        return true;
        }


    private final Rectangle myActiveCropRect = new Rectangle();

    private static final int[] theCropWorkspace = new int[4];
    }
