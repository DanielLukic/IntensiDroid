package net.intensicode.droid.opengl;

import net.intensicode.util.Assert;
import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.GL10;


public final class TextureStateManager
    {
    public TextureStateManager( final TextureUtilities aUtilities )
        {
        myUtilities = aUtilities;
        }

    public final void attach( final GL10 aGL )
        {
        if ( aGL == null ) throw new NullPointerException( "real GL must not be null" );
        gl = aGL;
        }

    public final void reset()
        {
        disableTexturingIfNecessary();
        disableAlpha();
        bindTexture( null );
        if ( isTextureMatrixPushed() ) popTextureMatrix();
        myTextureMatrixPushedFlag = false;
        myTextureEnabled = false;
        myActiveTexture = null;
        }

    public final void enableTexturingIfNecessary()
        {
        if ( myTextureEnabled ) return;

        gl.glEnable( GL10.GL_TEXTURE_2D );
        gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = true;
        }

    public final void disableTexturingIfNecessary()
        {
        if ( !myTextureEnabled ) return;

        gl.glDisable( GL10.GL_TEXTURE_2D );
        gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = false;
        }

    public final void enableAlpha( final int aAlpha256 )
        {
        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
        gl.glColor4f( 1f, 1f, 1f, aAlpha256 / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE );
        myAlphaEnabled = true;
        }

    public final void disableAlpha()
        {
        if ( !myAlphaEnabled ) return;
        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        }

    public final void bindTexture( final Texture aTexture )
        {
        if ( aTexture != null ) aTexture.bind();
        else myUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        myActiveTexture = aTexture;
        }

    public final void updateCrop( final Rectangle aRectangle )
        {
        if ( myCurrentlyCroppedTexture == myActiveTexture && myCurrentlyCroppedRectangle.equals( aRectangle ) ) return;
        myActiveTexture.setTextureCrop( aRectangle );
        myCurrentlyCroppedTexture = myActiveTexture;
        myCurrentlyCroppedRectangle.setTo( aRectangle );
        }

    public final void updateMatrix( final Rectangle aRectangle )
        {
        if ( isTextureMatrixPushed() ) popTextureMatrix();
        pushTextureMatrix( myActiveTexture, aRectangle );
        }

    // Implementation

    private boolean isTextureMatrixPushed()
        {
        return myTextureMatrixPushedFlag;
        }

    private void pushTextureMatrix( final Texture aTexture, final Rectangle aRectangle )
        {
        //#if DEBUG
        Assert.isFalse( "already pushed", myTextureMatrixPushedFlag );
        //#endif

        aTexture.setMatrix( mMatrix4x4, aRectangle );

        gl.glMatrixMode( GL10.GL_TEXTURE );
        gl.glPushMatrix();
        gl.glLoadMatrixf( mMatrix4x4, 0 );

        gl.glMatrixMode( GL10.GL_MODELVIEW );

        myTextureMatrixPushedFlag = true;
        }

    private void popTextureMatrix()
        {
        //#if DEBUG
        Assert.isTrue( "nothing pushed", myTextureMatrixPushedFlag );
        //#endif

        gl.glMatrixMode( GL10.GL_TEXTURE );
        gl.glPopMatrix();

        gl.glMatrixMode( GL10.GL_MODELVIEW );

        myTextureMatrixPushedFlag = false;
        }


    private GL10 gl = NoGL.INSTANCE;

    private Texture myActiveTexture;

    private boolean myAlphaEnabled;

    private boolean myTextureEnabled;

    private boolean myTextureMatrixPushedFlag;

    private Texture myCurrentlyCroppedTexture;

    private final TextureUtilities myUtilities;

    private final Rectangle myCurrentlyCroppedRectangle = new Rectangle();

    private final float[] mMatrix4x4 = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;
    }
