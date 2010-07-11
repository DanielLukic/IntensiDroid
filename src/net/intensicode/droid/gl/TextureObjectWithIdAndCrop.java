package net.intensicode.droid.gl;

import net.intensicode.util.Rectangle;

public class TextureObjectWithIdAndCrop
    {
    public TextureObjectWithIdAndCrop( final int aTextureId, final Rectangle aCropRect )
        {
        myTextureId = aTextureId;
        myCropRect = aCropRect;
        }

    public final void activate( final TextureStateManager aStateManager )
        {
        aStateManager.bind( myTextureId );
        aStateManager.setCrop( myCropRect );
        }

    private final int myTextureId;

    private final Rectangle myCropRect;
    }
