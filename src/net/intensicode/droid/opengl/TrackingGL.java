package net.intensicode.droid.opengl;

import net.intensicode.util.Log;

import javax.microedition.khronos.opengles.*;
import java.nio.*;

public class TrackingGL implements GL11, GL11Ext
    {
    public TrackingGL( final GL aGL )
        {
        myGL10 = (GL10) aGL;
        myGL11 = (GL11) aGL;
        myGL11ext = (GL11Ext) aGL;
        }

    private int glActiveTexture;

    private int glDrawTexfOES;

    private int glBindBuffer;

    private int glTexParameteriv;

    private int glBindTexture;

    private int glBlendFunc;

    private int glClear;

    private int glClearColor;

    private int glClientActiveTexture;

    private int glColor4f;

    private int glColor4x;

    private int glColorMask;

    private int glColorPointer;

    private int glCullFace;

    private int glDeleteTextures;

    private int glDisable;

    private int glDisableClientState;

    private int glDrawArrays;

    private int glDrawElements;

    private int glEnable;

    private int glEnableClientState;

    private int glFrontFace;

    private int glGenTextures;

    private int glGetError;

    private int glGetIntegerv;

    private int glGetString;

    private int glHint;

    private int glLoadIdentity;

    private int glMatrixMode;

    private int glOrthof;

    private int glPopMatrix;

    private int glPushMatrix;

    private int glScalef;

    private int glShadeModel;

    private int glTexCoordPointer;

    private int glTexEnvf;

    private int glTexImage2D;

    private int glTexParameterf;

    private int glTexSubImage2D;

    private int glTranslatef;

    private int glVertexPointer;

    private int glViewport;

    public void beginFrame()
        {
        glActiveTexture = 0;
        glDrawTexfOES = 0;
        glBindBuffer = 0;
        glTexParameteriv = 0;
        glBindTexture = 0;
        glBlendFunc = 0;
        glClear = 0;
        glClearColor = 0;
        glClientActiveTexture = 0;
        glColor4f = 0;
        glColor4x = 0;
        glColorMask = 0;
        glColorPointer = 0;
        glCullFace = 0;
        glDeleteTextures = 0;
        glDisable = 0;
        glDisableClientState = 0;
        glDrawArrays = 0;
        glDrawElements = 0;
        glEnable = 0;
        glEnableClientState = 0;
        glFrontFace = 0;
        glGenTextures = 0;
        glGetError = 0;
        glGetIntegerv = 0;
        glGetString = 0;
        glHint = 0;
        glLoadIdentity = 0;
        glMatrixMode = 0;
        glOrthof = 0;
        glPopMatrix = 0;
        glPushMatrix = 0;
        glScalef = 0;
        glShadeModel = 0;
        glTexCoordPointer = 0;
        glTexEnvf = 0;
        glTexImage2D = 0;
        glTexParameterf = 0;
        glTexSubImage2D = 0;
        glTranslatef = 0;
        glVertexPointer = 0;
        glViewport = 0;
        }

    private static final int DUMP_THRESHOLD = 5;

    public void endFrame()
        {
        if ( glActiveTexture > DUMP_THRESHOLD ) Log.info( "glActiveTexture: {}", glActiveTexture );
        if ( glDrawTexfOES > DUMP_THRESHOLD ) Log.info( "glDrawTexfOES: {}", glDrawTexfOES );
        if ( glBindBuffer > DUMP_THRESHOLD ) Log.info( "glBindBuffer: {}", glBindBuffer );
        if ( glTexParameteriv > DUMP_THRESHOLD ) Log.info( "glTexParameteriv: {}", glTexParameteriv );
        if ( glBindTexture > DUMP_THRESHOLD ) Log.info( "glBindTexture: {}", glBindTexture );
        if ( glBlendFunc > DUMP_THRESHOLD ) Log.info( "glBlendFunc: {}", glBlendFunc );
        if ( glClear > DUMP_THRESHOLD ) Log.info( "glClear: {}", glClear );
        if ( glClearColor > DUMP_THRESHOLD ) Log.info( "glClearColor: {}", glClearColor );
        if ( glClientActiveTexture > DUMP_THRESHOLD ) Log.info( "glClientActiveTexture: {}", glClientActiveTexture );
        if ( glColor4f > DUMP_THRESHOLD ) Log.info( "glColor4f: {}", glColor4f );
        if ( glColor4x > DUMP_THRESHOLD ) Log.info( "glColor4x: {}", glColor4x );
        if ( glColorMask > DUMP_THRESHOLD ) Log.info( "glColorMask: {}", glColorMask );
        if ( glColorPointer > DUMP_THRESHOLD ) Log.info( "glColorPointer: {}", glColorPointer );
        if ( glCullFace > DUMP_THRESHOLD ) Log.info( "glCullFace: {}", glCullFace );
        if ( glDeleteTextures > DUMP_THRESHOLD ) Log.info( "glDeleteTextures: {}", glDeleteTextures );
        if ( glDisable > DUMP_THRESHOLD ) Log.info( "glDisable: {}", glDisable );
        if ( glDisableClientState > DUMP_THRESHOLD ) Log.info( "glDisableClientState: {}", glDisableClientState );
        if ( glDrawArrays > DUMP_THRESHOLD ) Log.info( "glDrawArrays: {}", glDrawArrays );
        if ( glDrawElements > DUMP_THRESHOLD ) Log.info( "glDrawElements: {}", glDrawElements );
        if ( glEnable > DUMP_THRESHOLD ) Log.info( "glEnable: {}", glEnable );
        if ( glEnableClientState > DUMP_THRESHOLD ) Log.info( "glEnableClientState: {}", glEnableClientState );
        if ( glFrontFace > DUMP_THRESHOLD ) Log.info( "glFrontFace: {}", glFrontFace );
        if ( glGenTextures > DUMP_THRESHOLD ) Log.info( "glGenTextures: {}", glGenTextures );
        if ( glGetError > DUMP_THRESHOLD ) Log.info( "glGetError: {}", glGetError );
        if ( glGetIntegerv > DUMP_THRESHOLD ) Log.info( "glGetIntegerv: {}", glGetIntegerv );
        if ( glGetString > DUMP_THRESHOLD ) Log.info( "glGetString: {}", glGetString );
        if ( glHint > DUMP_THRESHOLD ) Log.info( "glHint: {}", glHint );
        if ( glLoadIdentity > DUMP_THRESHOLD ) Log.info( "glLoadIdentity: {}", glLoadIdentity );
        if ( glMatrixMode > DUMP_THRESHOLD ) Log.info( "glMatrixMode: {}", glMatrixMode );
        if ( glOrthof > DUMP_THRESHOLD ) Log.info( "glOrthof: {}", glOrthof );
        if ( glPopMatrix > DUMP_THRESHOLD ) Log.info( "glPopMatrix: {}", glPopMatrix );
        if ( glPushMatrix > DUMP_THRESHOLD ) Log.info( "glPushMatrix: {}", glPushMatrix );
        if ( glScalef > DUMP_THRESHOLD ) Log.info( "glScalef: {}", glScalef );
        if ( glShadeModel > DUMP_THRESHOLD ) Log.info( "glShadeModel: {}", glShadeModel );
        if ( glTexCoordPointer > DUMP_THRESHOLD ) Log.info( "glTexCoordPointer: {}", glTexCoordPointer );
        if ( glTexEnvf > DUMP_THRESHOLD ) Log.info( "glTexEnvf: {}", glTexEnvf );
        if ( glTexImage2D > DUMP_THRESHOLD ) Log.info( "glTexImage2D: {}", glTexImage2D );
        if ( glTexParameterf > DUMP_THRESHOLD ) Log.info( "glTexParameterf: {}", glTexParameterf );
        if ( glTexSubImage2D > DUMP_THRESHOLD ) Log.info( "glTexSubImage2D: {}", glTexSubImage2D );
        if ( glTranslatef > DUMP_THRESHOLD ) Log.info( "glTranslatef: {}", glTranslatef );
        if ( glVertexPointer > DUMP_THRESHOLD ) Log.info( "glVertexPointer: {}", glVertexPointer );
        if ( glViewport > DUMP_THRESHOLD ) Log.info( "glViewport: {}", glViewport );
        }

    // From GL11Ext

    public void glCurrentPaletteMatrixOES( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexfOES( final float v, final float v1, final float v2, final float v3, final float v4 )
        {
        glDrawTexfOES++;
        myGL11ext.glDrawTexfOES( v, v1, v2, v3, v4 );
        }

    public void glDrawTexfvOES( final float[] aFloats, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexfvOES( final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexiOES( final int i, final int i1, final int i2, final int i3, final int i4 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexivOES( final int[] aInts, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexivOES( final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexsOES( final short i, final short i1, final short i2, final short i3, final short i4 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexsvOES( final short[] aShorts, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexsvOES( final ShortBuffer aShortBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexxOES( final int i, final int i1, final int i2, final int i3, final int i4 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexxvOES( final int[] aInts, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawTexxvOES( final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLoadPaletteFromModelViewMatrixOES()
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMatrixIndexPointerOES( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMatrixIndexPointerOES( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glWeightPointerOES( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glWeightPointerOES( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    // From GL11

    public void glGetPointerv( final int i, final Buffer[] aBuffers )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glBindBuffer( final int i, final int i1 )
        {
        glBindBuffer++;
        myGL11.glBindBuffer( i, i1 );
        }

    public void glBufferData( final int i, final int i1, final Buffer aBuffer, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glBufferSubData( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClipPlanef( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClipPlanef( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClipPlanex( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClipPlanex( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glColor4ub( final byte b, final byte b1, final byte b2, final byte b3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glColorPointer( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDeleteBuffers( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDeleteBuffers( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDrawElements( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGenBuffers( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGenBuffers( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetBooleanv( final int i, final boolean[] aBooleans, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetBooleanv( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetBufferParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetBufferParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetClipPlanef( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetClipPlanef( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetClipPlanex( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetClipPlanex( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetFixedv( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetFixedv( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetFloatv( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetFloatv( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetLightfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetLightfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetLightxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetLightxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetMaterialfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetMaterialfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetMaterialxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetMaterialxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexEnviv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexEnviv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexEnvxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexEnvxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameterfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameterfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameterxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGetTexParameterxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public boolean glIsBuffer( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public boolean glIsEnabled( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public boolean glIsTexture( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glNormalPointer( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterf( final int i, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterfv( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterfv( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterx( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterxv( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointParameterxv( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointSizePointerOES( final int i, final int i1, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexCoordPointer( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnvi( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnviv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnviv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexParameterfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexParameterfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexParameteri( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        glTexParameteriv++;
        myGL11.glTexParameteriv( i, i1, aInts, i2 );
        }

    public void glTexParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        glTexParameteriv++;
        myGL11.glTexParameteriv( i, i1, aIntBuffer );
        }

    public void glTexParameterxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexParameterxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glVertexPointer( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    // From GL10

    public void glActiveTexture( final int i )
        {
        glActiveTexture++;
        myGL10.glActiveTexture( i );
        }

    public void glAlphaFunc( final int i, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glAlphaFuncx( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glBindTexture( final int i, final int i1 )
        {
        glBindTexture++;
        myGL10.glBindTexture( i, i1 );
        }

    public void glBlendFunc( final int i, final int i1 )
        {
        glBlendFunc++;
        myGL10.glBlendFunc( i, i1 );
        }

    public void glClear( final int i )
        {
        glClear++;
        myGL10.glClear( i );
        }

    public void glClearColor( final float v, final float v1, final float v2, final float v3 )
        {
        glClearColor++;
        myGL10.glClearColor( v, v1, v2, v3 );
        }

    public void glClearColorx( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClearDepthf( final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClearDepthx( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClearStencil( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glClientActiveTexture( final int i )
        {
        glClientActiveTexture++;
        myGL10.glClientActiveTexture( i );
        }

    public void glColor4f( final float v, final float v1, final float v2, final float v3 )
        {
        glColor4f++;
        myGL10.glColor4f( v, v1, v2, v3 );
        }

    public void glColor4x( final int i, final int i1, final int i2, final int i3 )
        {
        glColor4x++;
        myGL10.glColor4x( i, i1, i2, i3 );
        }

    public void glColorMask( final boolean b, final boolean b1, final boolean b2, final boolean b3 )
        {
        glColorMask++;
        myGL10.glColorMask( b, b1, b2, b3 );
        }

    public void glColorPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        glColorPointer++;
        myGL10.glColorPointer( i, i1, i2, aBuffer );
        }

    public void glCompressedTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glCompressedTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glCopyTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glCopyTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glCullFace( final int i )
        {
        glCullFace++;
        myGL10.glCullFace( i );
        }

    public void glDeleteTextures( final int i, final int[] aInts, final int i1 )
        {
        glDeleteTextures++;
        myGL10.glDeleteTextures( i, aInts, i1 );
        }

    public void glDeleteTextures( final int i, final IntBuffer aIntBuffer )
        {
        glDeleteTextures++;
        myGL10.glDeleteTextures( i, aIntBuffer );
        }

    public void glDepthFunc( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDepthMask( final boolean b )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDepthRangef( final float v, final float v1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDepthRangex( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glDisable( final int i )
        {
        glDisable++;
        myGL10.glDisable( i );
        }

    public void glDisableClientState( final int i )
        {
        glDisableClientState++;
        myGL10.glDisableClientState( i );
        }

    public void glDrawArrays( final int i, final int i1, final int i2 )
        {
        glDrawArrays++;
        myGL10.glDrawArrays( i, i1, i2 );
        }

    public void glDrawElements( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        glDrawElements++;
        myGL10.glDrawElements( i, i1, i2, aBuffer );
        }

    public void glEnable( final int i )
        {
        glEnable++;
        myGL10.glEnable( i );
        }

    public void glEnableClientState( final int i )
        {
        glEnableClientState++;
        myGL10.glEnableClientState( i );
        }

    public void glFinish()
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFlush()
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogf( final int i, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogfv( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogfv( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogx( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogxv( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFogxv( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFrontFace( final int i )
        {
        glFrontFace++;
        myGL10.glFrontFace( i );
        }

    public void glFrustumf( final float v, final float v1, final float v2, final float v3, final float v4, final float v5 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glFrustumx( final int i, final int i1, final int i2, final int i3, final int i4, final int i5 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glGenTextures( final int i, final int[] aInts, final int i1 )
        {
        glGenTextures++;
        myGL10.glGenTextures( i, aInts, i1 );
        }

    public void glGenTextures( final int i, final IntBuffer aIntBuffer )
        {
        glGenTextures++;
        myGL10.glGenTextures( i, aIntBuffer );
        }

    public int glGetError()
        {
        glGetError++;
        return myGL10.glGetError();
        }

    public void glGetIntegerv( final int i, final int[] aInts, final int i1 )
        {
        glGetIntegerv++;
        myGL10.glGetIntegerv( i, aInts, i1 );
        }

    public void glGetIntegerv( final int i, final IntBuffer aIntBuffer )
        {
        glGetIntegerv++;
        myGL10.glGetIntegerv( i, aIntBuffer );
        }

    public String glGetString( final int i )
        {
        glGetString++;
        return myGL10.glGetString( i );
        }

    public void glHint( final int i, final int i1 )
        {
        glHint++;
        myGL10.glHint( i, i1 );
        }

    public void glLightModelf( final int i, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightModelfv( final int i, final float[] aFloats, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightModelfv( final int i, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightModelx( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightModelxv( final int i, final int[] aInts, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightModelxv( final int i, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightf( final int i, final int i1, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightx( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLightxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLineWidth( final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLineWidthx( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLoadIdentity()
        {
        glLoadIdentity++;
        myGL10.glLoadIdentity();
        }

    public void glLoadMatrixf( final float[] aFloats, final int i )
        {
        myGL10.glLoadMatrixf(aFloats, i );
        }

    public void glLoadMatrixf( final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLoadMatrixx( final int[] aInts, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLoadMatrixx( final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glLogicOp( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialf( final int i, final int i1, final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialx( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMaterialxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMatrixMode( final int i )
        {
        glMatrixMode++;
        myGL10.glMatrixMode( i );
        }

    public void glMultMatrixf( final float[] aFloats, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMultMatrixf( final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMultMatrixx( final int[] aInts, final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMultMatrixx( final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMultiTexCoord4f( final int i, final float v, final float v1, final float v2, final float v3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glMultiTexCoord4x( final int i, final int i1, final int i2, final int i3, final int i4 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glNormal3f( final float v, final float v1, final float v2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glNormal3x( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glNormalPointer( final int i, final int i1, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glOrthof( final float v, final float v1, final float v2, final float v3, final float v4, final float v5 )
        {
        glOrthof++;
        myGL10.glOrthof( v, v1, v2, v3, v4, v5 );
        }

    public void glOrthox( final int i, final int i1, final int i2, final int i3, final int i4, final int i5 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPixelStorei( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointSize( final float v )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPointSizex( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPolygonOffset( final float v, final float v1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPolygonOffsetx( final int i, final int i1 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glPopMatrix()
        {
        glPopMatrix++;
        myGL10.glPopMatrix();
        }

    public void glPushMatrix()
        {
        glPushMatrix++;
        myGL10.glPushMatrix();
        }

    public void glReadPixels( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final Buffer aBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glRotatef( final float v, final float v1, final float v2, final float v3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glRotatex( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glSampleCoverage( final float v, final boolean b )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glSampleCoveragex( final int i, final boolean b )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glScalef( final float v, final float v1, final float v2 )
        {
        glScalef++;
        myGL10.glScalef( v, v1, v2 );
        }

    public void glScalex( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glScissor( final int i, final int i1, final int i2, final int i3 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glShadeModel( final int i )
        {
        glShadeModel++;
        myGL10.glShadeModel( i );
        }

    public void glStencilFunc( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glStencilMask( final int i )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glStencilOp( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexCoordPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        glTexCoordPointer++;
        myGL10.glTexCoordPointer( i, i1, i2, aBuffer );
        }

    public void glTexEnvf( final int i, final int i1, final float v )
        {
        glTexEnvf++;
        myGL10.glTexEnvf( i, i1, v );
        }

    public void glTexEnvfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnvfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnvx( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnvxv( final int i, final int i1, final int[] aInts, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexEnvxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {
        glTexImage2D++;
        myGL10.glTexImage2D( i, i1, i2, i3, i4, i5, i6, i7, aBuffer );
        }

    public void glTexParameterf( final int i, final int i1, final float v )
        {
        glTexParameterf++;
        myGL10.glTexParameterf( i, i1, v );
        }

    public void glTexParameterx( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {
        glTexSubImage2D++;
        myGL10.glTexSubImage2D( i, i1, i2, i3, i4, i5, i6, i7, aBuffer );
        }

    public void glTranslatef( final float v, final float v1, final float v2 )
        {
        glTranslatef++;
        myGL10.glTranslatef( v, v1, v2 );
        }

    public void glTranslatex( final int i, final int i1, final int i2 )
        {
        throw new RuntimeException( "nyi" );
        }

    public void glVertexPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {
        glVertexPointer++;
        myGL10.glVertexPointer( i, i1, i2, aBuffer );
        }

    public void glViewport( final int i, final int i1, final int i2, final int i3 )
        {
        glViewport++;
        myGL10.glViewport( i, i1, i2, i3 );
        }

    private final GL10 myGL10;

    private final GL11 myGL11;

    private final GL11Ext myGL11ext;
    }
