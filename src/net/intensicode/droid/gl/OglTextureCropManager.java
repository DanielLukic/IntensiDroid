package net.intensicode.droid.gl;

import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;

public final class OglTextureCropManager implements TextureCropManager
    {
    public OglTextureCropManager( final GL11 aGL )
        {
        myGL = aGL;
        }

    // From TextureCropManager

    public final void reset()
        {
        myPreviousCropHash = 0;
        }

    public final void setCrop( final Rectangle aCropRect )
        {
        final int x = aCropRect.x;
        final int y = aCropRect.y;
        final int width = aCropRect.width;
        final int height = aCropRect.height;

        final long cropHash = x | y << 16 | width << 32 | height << 48;
        if ( myPreviousCropHash == cropHash ) return;
        myPreviousCropHash = cropHash;

        theCropWorkspace[ 0 ] = x;
        theCropWorkspace[ 1 ] = y + height;
        theCropWorkspace[ 2 ] = width;
        theCropWorkspace[ 3 ] = -height;
        myGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );
        }

    private long myPreviousCropHash;

    private final GL11 myGL;

    private static final int[] theCropWorkspace = new int[4];
    }
