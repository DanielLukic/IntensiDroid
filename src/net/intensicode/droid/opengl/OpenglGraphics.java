package net.intensicode.droid.opengl;

import android.graphics.*;
import android.opengl.GLUtils;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;
import java.util.ArrayList;


public final class OpenglGraphics extends DirectGraphics implements TexturePurger
    {
    public OpenglGraphics()
        {
        myFillRectSquare.set( 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f );
        myFillRectSquare.set( 1, 0, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f );
        myFillRectSquare.set( 0, 1, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f );
        myFillRectSquare.set( 1, 1, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f );
        }

    final void onSurfaceCreated( final GL10 aGL10 )
        {
        myGL = aGL10;

        final String version = aGL10.glGetString( GL10.GL_VERSION );

        final boolean isVersion1_0 = version.indexOf( "1.0" ) >= 0;

        final String extensions = aGL10.glGetString( GL10.GL_EXTENSIONS );

        myHasDrawTextureExtension = extensions.indexOf( "GL_OES_draw_texture" ) >= 0;
        myHasHardwareBuffersFlag = !isVersion1_0;

        if ( myHasHardwareBuffersFlag )
            {
            myFillRectSquare.freeHardwareBuffers( aGL10 );
            myFillRectSquare.generateHardwareBuffers( aGL10 );
            }

        //#if DEBUG
        Log.debug( "GL vendor: {}", aGL10.glGetString( GL10.GL_VENDOR ) );
        Log.debug( "GL rendered: {}", aGL10.glGetString( GL10.GL_RENDERER ) );
        Log.debug( "GL version: {}", version );
        Log.debug( "GL extensions: {}", extensions );
        Log.debug( "has draw texture extension? " + myHasDrawTextureExtension );
        Log.debug( "has hardware buffers? " + myHasHardwareBuffersFlag );
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif

        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.get( myTexturizedImageResources.size() - 1 ) );
            }
        }

    public void onSurfaceChanged( final GL10 aGL10, final int aWidth, final int aHeight )
        {
        myWidth = aWidth;
        myHeight = aHeight;
        }

    final void onBeginFrame()
        {
        showGlError();

        myGL.glEnableClientState( GL10.GL_VERTEX_ARRAY );

        mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ 8 ] = mMatrix4x4[ 9 ] = 0.0f;
        mMatrix4x4[ 0 ] = 1.0f;
        mMatrix4x4[ 5 ] = -1.0f;
        mMatrix4x4[ 12 ] = 0.0f;
        mMatrix4x4[ 13 ] = 1.0f;

        myGL.glMatrixMode( GL10.GL_TEXTURE );
        myGL.glLoadMatrixf( mMatrix4x4, 0 );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        enableTexturing();

        showGlError();

        myTextureStateChanges = myTextureBindCalls = myTextureCropResets = 0;
        }

    private int myTextureStateChanges;

    private int myTextureBindCalls;

    private int myTextureCropResets;

    private boolean myTextureActive;

    private int myTextureId;

    private void showGlError()
        {
        final int error = myGL.glGetError();
        //#if DEBUG
        if ( error != 0 ) Log.debug( "gl error: {}", error );
        //#endif
        }

    private void enableTexturing()
        {
        myGL.glEnable( GL10.GL_TEXTURE_2D );
        myGL.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureActive = true;

        myTextureStateChanges++;
        }

    private void disableTexturing()
        {
        myGL.glDisable( GL10.GL_TEXTURE_2D );
        myGL.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureActive = false;

        myTextureStateChanges++;
        }

    final void onEndFrame()
        {
        showGlError();

        disableTexturing();

        myGL.glDisableClientState( GL10.GL_VERTEX_ARRAY );

        showGlError();

        //#if DEBUG
        if ( myTextureStateChanges > 10 ) Log.debug( "gl texture state changes: {}", myTextureStateChanges );
        if ( myTextureBindCalls > 20 ) Log.debug( "gl texture bind calls: {}", myTextureBindCalls );
        if ( myTextureCropResets > 5 ) Log.debug( "gl texture crop resets: {}", myTextureCropResets );
        //#endif
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "purging texture {} ({})", aImageResource.textureId, aImageResource.resourcePath );
        //#endif

        mTextureNameWorkspace[ 0 ] = aImageResource.textureId;
        myGL.glDeleteTextures( 1, mTextureNameWorkspace, 0 );

        aImageResource.textureId = aImageResource.textureWidth = aImageResource.textureHeight = 0;
        aImageResource.texturePurger = null;

        final boolean removed = myTexturizedImageResources.remove( aImageResource );
        //#if DEBUG
        if ( !removed ) Log.debug( "failed removing texturized image from internal list" );
        //#endif
        }

    // From DirectGraphics

    public void clearRGB24( final int aRGB24 )
        {
        setColorRGB24( aRGB24 );
        fillRect( 0, 0, myWidth, myHeight );
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        setColorARGB32( 0xFF000000 | aRGB24 );
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        final float alpha = ( ( aARGB32 >> 24 ) & 255 ) / 255.0f;
        final float red = ( ( aARGB32 >> 16 ) & 255 ) / 255.0f;
        final float green = ( ( aARGB32 >> 8 ) & 255 ) / 255.0f;
        final float blue = ( aARGB32 & 255 ) / 255.0f;
        myGL.glColor4f( red, green, blue, alpha );
        }

    public final void setFont( final FontResource aFont )
        {
        myFont = (AndroidFontResource) aFont;
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        if ( aX1 == aX2 && aY1 == aY2 ) fillColoredRect( aX1, aY1, 1, 1 );
        //#if DEBUG
        else Log.debug( "drawLine not implemented, yet" );
        //#endif
        }

    private void fillColoredRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( myTextureActive ) disableTexturing();
        myFillRectSquare.draw( myGL, aX, aY, aWidth, aHeight, false );
        }

    private final Grid myFillRectSquare = new Grid( 2, 2 );

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        //#if DEBUG
        Log.debug( "fillTriangle not implemented, yet" );
        //#endif
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        fillTexturedRect( aImage, aX, aY );
        }

    private void fillTexturedRect( final ImageResource aImage, final int aX, final int aY )
        {
        fillTexturedRect( aImage, aX, aY, aImage.getWidth(), aImage.getHeight() );
        }

    private int getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.textureId == 0 ) makeTexture( aImage );
        return aImage.textureId;
        }

    private void fillTexturedRect( final ImageResource aImage, final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( !myTextureActive ) enableTexturing();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int textureId = getOrLoadTexture( imageResource );
        if ( myTextureId != textureId ) bindTexture( textureId );

        if ( myHasDrawTextureExtension )
            {
            if ( myIsCroppedFlag ) resetTextureCropping( imageResource.textureWidth, imageResource.textureHeight );
            ( (GL11Ext) myGL ).glDrawTexfOES( aX, myHeight - aY - aHeight, 0, aWidth, aHeight );
            }
        else
            {
            myFillRectSquare.draw( myGL, aX, aY, aWidth, aHeight, true );
            }
        }

    private boolean myIsCroppedFlag;

    private void resetTextureCropping( final int aWidth, final int aHeight )
        {
        mCropWorkspace[ 0 ] = 0;
        mCropWorkspace[ 1 ] = aHeight;
        mCropWorkspace[ 2 ] = aWidth;
        mCropWorkspace[ 3 ] = -aHeight;

        ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );

        myIsCroppedFlag = false;

        myTextureCropResets++;
        }

    private void cropTexture( final Rectangle aRect, final AndroidImageResource aImageResource )
        {
        final float xFactor = aImageResource.textureWidth / (float) aImageResource.getWidth();
        final float yFactor = aImageResource.textureHeight / (float) aImageResource.getHeight();
        final float x = aRect.x * xFactor;
        final float y = aRect.y * yFactor;
        final float width = aRect.width * xFactor;
        final float height = aRect.height * yFactor;
        mCropWorkspace[ 0 ] = (int) x;
        mCropWorkspace[ 1 ] = (int) ( y + height );
        mCropWorkspace[ 2 ] = (int) width;
        mCropWorkspace[ 3 ] = (int) -height;

        ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );

        myIsCroppedFlag = true;
        }

    private void bindTexture( final int aTextureId )
        {
        if ( myTextureId == aTextureId ) return;

//        Log.debug( "gl texture bind change: {} => {}", myTextureId, aTextureId );

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, aTextureId );
        myTextureId = aTextureId;

        myTextureBindCalls++;
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        fillTexturedRect( aImage, aX, aY );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        final Position aligned = getAlignedPosition( aX, aY, aImage.getWidth(), aImage.getHeight(), aAlignment );
        drawImage( aImage, aligned.x, aligned.y );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        if ( !myTextureActive ) enableTexturing();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int textureId = getOrLoadTexture( imageResource );
        if ( myTextureId != textureId ) bindTexture( textureId );

        if ( myHasDrawTextureExtension )
            {
            cropTexture( aSourceRect, imageResource );
            ( (GL11Ext) myGL ).glDrawTexfOES( aTargetX, myHeight - aTargetY - aSourceRect.height, 0, aSourceRect.width, aSourceRect.height );
            }
        else
            {
            mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ 8 ] = mMatrix4x4[ 9 ] = 0.0f;
            mMatrix4x4[ 0 ] = aSourceRect.width / (float) aImage.getWidth();
            mMatrix4x4[ 5 ] = -aSourceRect.height / (float) aImage.getHeight();
            mMatrix4x4[ 12 ] = aSourceRect.x / (float) aImage.getWidth();
            mMatrix4x4[ 13 ] = aSourceRect.y / (float) aImage.getHeight() - mMatrix4x4[ 5 ];

            myGL.glMatrixMode( GL10.GL_TEXTURE );
            myGL.glPushMatrix();
            myGL.glLoadMatrixf( mMatrix4x4, 0 );

            myGL.glMatrixMode( GL10.GL_MODELVIEW );

            myFillRectSquare.draw( myGL, aTargetX, aTargetY, aSourceRect.width, aSourceRect.height, true );

            myGL.glMatrixMode( GL10.GL_TEXTURE );
            myGL.glPopMatrix();

            myGL.glMatrixMode( GL10.GL_MODELVIEW );
            }
        }

    private float[] mMatrix4x4 = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        }

    public void drawChar( final char aCharCode, final int aX, final int aY )
        {
        }

    // Implementation

    private void makeTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "making texture for {}", aImageResource.resourcePath );
        //#endif

        myTexturizedImageResources.add( aImageResource );

        mTextureNameWorkspace[ 0 ] = 0;
        myGL.glGenTextures( 1, mTextureNameWorkspace, 0 );

        final int textureName = aImageResource.textureId = mTextureNameWorkspace[ 0 ];
        //#if DEBUG
        Log.debug( "new texture id: {}", textureName );
        //#endif

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, textureName );

        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );

        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
//        myGL.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT );
//        myGL.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT );

        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );

        final Bitmap originalBitmap = aImageResource.bitmap;

        final int originalWidth = originalBitmap.getWidth();
        final int properWidth = Math.min( 512, findNextPowerOfTwo( originalWidth ) );
        final int originalHeight = originalBitmap.getHeight();
        final int properHeight = Math.min( 256, findNextPowerOfTwo( originalHeight ) );

        final Bitmap bitmap = makeProperBitmap( originalBitmap, properWidth, properHeight );

        aImageResource.textureWidth = bitmap.getWidth();
        aImageResource.textureHeight = bitmap.getHeight();

        aImageResource.texturePurger = this;

        if ( myUseGlutilsFlag )
            {
            GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bitmap, 0 );
            }
        else
            {
            int[] bitmap_data = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels( bitmap_data, 0, bitmap.getWidth(), 0, 0,
                              bitmap.getWidth(), bitmap.getHeight() );
            for ( int n = 0; n < bitmap.getWidth() * bitmap.getHeight(); ++n )
                {
                int pixel = bitmap_data[ n ];
                bitmap_data[ n ] = ( ( ( 0xFF000000 & pixel ) ) |        // Alpha.
                                     ( ( 0x00FF0000 & pixel ) >> 16 ) |  // Red.
                                     ( ( 0x0000FF00 & pixel ) ) |        // Green.
                                     ( ( 0x000000FF & pixel ) << 16 ) );  // Blue.
                }
            IntBuffer bitmap_data_buffer = IntBuffer.wrap( bitmap_data );
            myGL.glBindTexture( GL10.GL_TEXTURE_2D, textureName );
            myGL.glTexImage2D( GL10.GL_TEXTURE_2D,
                               0,                      // Mipmap level.
                               GL10.GL_RGBA,           // Internal format.
                               bitmap.getWidth(),
                               bitmap.getHeight(),
                               0,                      // Border.
                               GL10.GL_RGBA,           // Format.
                               GL10.GL_UNSIGNED_BYTE,
                               bitmap_data_buffer );
            }

        if ( myHasDrawTextureExtension )
            {
            mCropWorkspace[ 0 ] = 0;
            mCropWorkspace[ 1 ] = bitmap.getHeight();
            mCropWorkspace[ 2 ] = bitmap.getWidth();
            mCropWorkspace[ 3 ] = -bitmap.getHeight();

            ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );
            }

        if ( bitmap != originalBitmap ) bitmap.recycle(); // only the created ones..

        int error = myGL.glGetError();
        if ( error != GL10.GL_NO_ERROR )
            {
            Log.error( "failed loading texture - open myGL error {}", error, null );
            }
        }

    private int findNextPowerOfTwo( int aPositiveInteger )
        {
        if ( aPositiveInteger == 0 ) return 1;
        aPositiveInteger--;
        for ( int i = 1; i < 30; i <<= 1 )
            {
            aPositiveInteger = aPositiveInteger | aPositiveInteger >> i;
            }
        return aPositiveInteger + 1;
        }

    private Bitmap makeProperBitmap( final Bitmap aBitmap, final int aWidth, final int aHeight )
        {
        if ( aWidth == aBitmap.getWidth() && aHeight == aBitmap.getHeight() ) return aBitmap;

        final Bitmap bitmap32 = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        myTextureCloneCanvas.setBitmap( bitmap32 );
        myTextureCloneSourceRect.right = aBitmap.getWidth();
        myTextureCloneSourceRect.bottom = aBitmap.getHeight();
        myTextureCloneTargetRect.right = aWidth;
        myTextureCloneTargetRect.bottom = aHeight;
        myTextureCloneCanvas.drawBitmap( aBitmap, myTextureCloneSourceRect, myTextureCloneTargetRect, myTextureClonePaint );

        //#if DEBUG
        Log.debug( "created proper texture bitmap" );
        Log.debug( "bitmap size: {}x{}", aBitmap.getWidth(), aBitmap.getHeight() );
        Log.debug( "proper size: {}x{}", aWidth, aHeight );
        //#endif

        return bitmap32;
        }


    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private AndroidFontResource myFont;

    private boolean myUseGlutilsFlag;

    private boolean myHasHardwareBuffersFlag;

    private boolean myHasDrawTextureExtension;

    private final int[] mCropWorkspace = new int[4];

    private final int[] mTextureNameWorkspace = new int[1];

    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();

    private final Rect myTextureCloneSourceRect = new Rect();

    private final Rect myTextureCloneTargetRect = new Rect();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();
    }
