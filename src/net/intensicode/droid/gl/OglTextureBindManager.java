package net.intensicode.droid.gl;

import javax.microedition.khronos.opengles.*;

public class OglTextureBindManager implements TextureBindManager
    {
    public OglTextureBindManager( final GL11 aGL )
        {
        myGL = aGL;
        }

    // From TextureBindManager

    public final void reset()
        {
        myPreviousId = 0;
        }

    public final void bind( final int aTextureId )
        {
        if ( myPreviousId == aTextureId ) return;
        myPreviousId = aTextureId;

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, aTextureId );
        }

    private long myPreviousId;

    private final GL11 myGL;
    }
