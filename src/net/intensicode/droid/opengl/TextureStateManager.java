package net.intensicode.droid.opengl;

import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;


public final class TextureStateManager
    {
    public GL10 gl;


    //#if DEBUG && DEBUG_OPENGL
    public final void resetDebugCounters()
        {
        myTextureStateChanges = myTextureBindCalls = myTextureMatrixPops = myTextureMatrixPushes = myTextureCropChanges = 0;
        }

    public final void dumpDebugCounters()
        {
        if ( myTextureBindCalls > 10 ) Log.debug( "gl texture bind calls: {}", myTextureBindCalls );
        if ( myTextureMatrixPops > 10 ) Log.debug( "gl texture matrix pops: {}", myTextureMatrixPops );
        if ( myTextureMatrixPushes > 10 ) Log.debug( "gl texture matrix pushes: {}", myTextureMatrixPushes );
        if ( myTextureCropChanges > 10 ) Log.debug( "gl texture crop resets: {}", myTextureCropChanges );
        if ( myTextureStateChanges > 10 ) Log.debug( "gl texture state changes: {}", myTextureStateChanges );
        }
    //#endif

    public final void enableTexturingIfNecessary()
        {
        if ( myTextureEnabled ) return;

        gl.glEnable( GL10.GL_TEXTURE_2D );
        gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = true;

        myTextureStateChanges++;
        }

    public final void disableTexturingIfNecessary()
        {
        if ( !myTextureEnabled ) return;

        gl.glDisable( GL10.GL_TEXTURE_2D );
        gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = false;

        myTextureStateChanges++;
        }

    public final void enableAlpha( final int aAlpha256 )
        {
        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
        // TODO: Use special texture env 'source' instead of glColor? To avoid interference with OpenglGraphics color..
        gl.glColor4f( 1f, 1f, 1f, aAlpha256 / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE );
        }

    public final void disableAlpha()
        {
        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        }

    public final void bindTextureIfNecessary( final Texture aTexture )
        {
        if ( myActiveTexture == aTexture ) return;

        gl.glBindTexture( GL10.GL_TEXTURE_2D, aTexture.id );
        myActiveTexture = aTexture;

        myTextureBindCalls++;
        }

    public final void updateCropIfNecessary( final Rectangle aRectangle )
        {
        final boolean cropChanged = myActiveTexture.cropTextureIfNecessary( (GL11) gl, aRectangle );
        if ( cropChanged ) myTextureCropChanges++;
        }

    public final void updateMatrixIfNecessary( final Rectangle aRectangle )
        {
        final boolean textureMatrixUpToDate = isTextureMatrixUpToDate( myActiveTexture, aRectangle );
        //#if DEBUG_OPENGL
        if ( textureMatrixUpToDate && Random.INSTANCE.nextInt( 16 ) < 10 ) return;
        //#else
        //# if ( textureMatrixUpToDate ) return;
        //#endif

        if ( isTextureMatrixPushed() ) popTextureMatrix();
        pushTextureMatrix( myActiveTexture, aRectangle );
        }

    // Implementation

    private boolean isTextureMatrixUpToDate( final Texture aTexture, final Rectangle aRectangle )
        {
        if ( myActiveTexture != aTexture ) return false;
        if ( isIdentityMatrix() && aTexture.isFullRect( aRectangle ) ) return false;
        return myTextureMatrixRect.equals( aRectangle );
        }

    private boolean isIdentityMatrix()
        {
        if ( mMatrix4x4[ 0 ] != 1.0f ) return false;
        if ( mMatrix4x4[ 5 ] == -1.0f ) return false;
        if ( mMatrix4x4[ 12 ] == 0.0f ) return false;
        if ( mMatrix4x4[ 13 ] == 1.0f ) return false;
        return true;
        }

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

        myTextureMatrixPushes++;
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

        myTextureMatrixPops++;
        myTextureMatrixPushedFlag = false;
        }


    private int myTextureBindCalls;

    private int myTextureMatrixPops;

    private int myTextureMatrixPushes;

    private int myTextureStateChanges;

    private int myTextureCropChanges;


    private Texture myActiveTexture;

    private boolean myTextureEnabled;

    private boolean myTextureMatrixPushedFlag;

    private final Rectangle myTextureMatrixRect = new Rectangle();

    private final float[] mMatrix4x4 = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;
    }
