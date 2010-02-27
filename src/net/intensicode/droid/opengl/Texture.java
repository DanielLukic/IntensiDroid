package net.intensicode.droid.opengl;

import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;

public final class Texture
    {
    //#if DEBUG_OPENGL
    public static int theTextureCropResets;
    //#endif

    public int id;

    public int width;

    public int height;

    public int originalWidth;

    public int originalHeight;


    public final void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / (float) width;
        aMatrix4x4[ 5 ] = -aSourceRect.height / (float) height;
        aMatrix4x4[ 12 ] = aSourceRect.x / (float) width;
        aMatrix4x4[ 13 ] = aSourceRect.y / (float) height - aMatrix4x4[ 5 ];
        }

    public final void cropTexture( final GL11 aGL, final Rectangle aRect )
        {
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
        }


    private static final int[] theCropWorkspace = new int[4];
    }
