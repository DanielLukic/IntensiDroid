package net.intensicode.droid.opengl;

import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;

public class Texture
    {
    //#if DEBUG_OPENGL
    public static int theTextureCropResets;
    //#endif

    public int id;

    public int width;

    public int height;

    public int originalWidth;

    public int originalHeight;

    public boolean isCropped;


    public final void resetCropRect( final GL11 aGL )
        {
        if ( !isCropped ) throw new IllegalStateException();

        theCropWorkspace[ 0 ] = 0;
        theCropWorkspace[ 1 ] = height;
        theCropWorkspace[ 2 ] = width;
        theCropWorkspace[ 3 ] = -height;

        aGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );

        isCropped = false;

        theTextureCropResets++;
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

        isCropped = true;
        }


    private static final int[] theCropWorkspace = new int[4];
    }
