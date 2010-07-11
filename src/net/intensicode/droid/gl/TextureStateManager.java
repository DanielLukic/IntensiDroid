package net.intensicode.droid.gl;

import net.intensicode.util.Rectangle;

public class TextureStateManager
    {
    public TextureStateManager( final TextureBindManager aBindManager, final TextureCropManager aCropManager )
        {
        myBindManager = aBindManager;
        myCropManager = aCropManager;
        }

    public final void reset()
        {
        myBindManager.reset();
        myCropManager.reset();
        }

    public final void bind( final int aTextureId )
        {
        myBindManager.bind( aTextureId );
        myCropManager.reset();
        }

    public final void setCrop( final Rectangle aCropRect )
        {
        myCropManager.setCrop( aCropRect );
        }

    private final TextureBindManager myBindManager;

    private final TextureCropManager myCropManager;
    }
