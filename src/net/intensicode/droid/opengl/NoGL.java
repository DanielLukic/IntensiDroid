package net.intensicode.droid.opengl;

import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.opengles.GL11;
import java.nio.*;

public final class NoGL implements GL11
    {
    public static final NoGL INSTANCE = new NoGL();

    private NoGL()
        {
        }

    public void glGetPointerv( final int i, final Buffer[] aBuffers )
        {

        }

    public void glBindBuffer( final int i, final int i1 )
        {

        }

    public void glBufferData( final int i, final int i1, final Buffer aBuffer, final int i2 )
        {

        }

    public void glBufferSubData( final int i, final int i1, final int i2, final Buffer aBuffer )
        {

        }

    public void glClipPlanef( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glClipPlanef( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glClipPlanex( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glClipPlanex( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glColor4ub( final byte b, final byte b1, final byte b2, final byte b3 )
        {

        }

    public void glColorPointer( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glDeleteBuffers( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glDeleteBuffers( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glDrawElements( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glGenBuffers( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glGenBuffers( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glGetBooleanv( final int i, final boolean[] aBooleans, final int i1 )
        {

        }

    public void glGetBooleanv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glGetBufferParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetBufferParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetClipPlanef( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glGetClipPlanef( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glGetClipPlanex( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glGetClipPlanex( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glGetFixedv( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glGetFixedv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glGetFloatv( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glGetFloatv( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glGetLightfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glGetLightfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glGetLightxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetLightxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetMaterialfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glGetMaterialfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glGetMaterialxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetMaterialxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetTexEnviv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetTexEnviv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetTexEnvxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetTexEnvxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetTexParameterfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glGetTexParameterfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glGetTexParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetTexParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glGetTexParameterxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glGetTexParameterxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public boolean glIsBuffer( final int i )
        {
        return false;
        }

    public boolean glIsEnabled( final int i )
        {
        return false;
        }

    public boolean glIsTexture( final int i )
        {
        return false;
        }

    public void glNormalPointer( final int i, final int i1, final int i2 )
        {

        }

    public void glPointParameterf( final int i, final float v )
        {

        }

    public void glPointParameterfv( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glPointParameterfv( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glPointParameterx( final int i, final int i1 )
        {

        }

    public void glPointParameterxv( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glPointParameterxv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glPointSizePointerOES( final int i, final int i1, final Buffer aBuffer )
        {

        }

    public void glTexCoordPointer( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glTexEnvi( final int i, final int i1, final int i2 )
        {

        }

    public void glTexEnviv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glTexEnviv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glTexParameterfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glTexParameterfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glTexParameteri( final int i, final int i1, final int i2 )
        {

        }

    public void glTexParameteriv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glTexParameteriv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glTexParameterxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glTexParameterxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glVertexPointer( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glActiveTexture( final int i )
        {

        }

    public void glAlphaFunc( final int i, final float v )
        {

        }

    public void glAlphaFuncx( final int i, final int i1 )
        {

        }

    public void glBindTexture( final int i, final int i1 )
        {

        }

    public void glBlendFunc( final int i, final int i1 )
        {

        }

    public void glClear( final int i )
        {

        }

    public void glClearColor( final float v, final float v1, final float v2, final float v3 )
        {

        }

    public void glClearColorx( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glClearDepthf( final float v )
        {

        }

    public void glClearDepthx( final int i )
        {

        }

    public void glClearStencil( final int i )
        {

        }

    public void glClientActiveTexture( final int i )
        {

        }

    public void glColor4f( final float v, final float v1, final float v2, final float v3 )
        {

        }

    public void glColor4x( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glColorMask( final boolean b, final boolean b1, final boolean b2, final boolean b3 )
        {

        }

    public void glColorPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {

        }

    public void glCompressedTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final Buffer aBuffer )
        {

        }

    public void glCompressedTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {

        }

    public void glCopyTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7 )
        {

        }

    public void glCopyTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7 )
        {

        }

    public void glCullFace( final int i )
        {

        }

    public void glDeleteTextures( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glDeleteTextures( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glDepthFunc( final int i )
        {

        }

    public void glDepthMask( final boolean b )
        {

        }

    public void glDepthRangef( final float v, final float v1 )
        {

        }

    public void glDepthRangex( final int i, final int i1 )
        {

        }

    public void glDisable( final int i )
        {

        }

    public void glDisableClientState( final int i )
        {

        }

    public void glDrawArrays( final int i, final int i1, final int i2 )
        {

        }

    public void glDrawElements( final int i, final int i1, final int i2, final Buffer aBuffer )
        {

        }

    public void glEnable( final int i )
        {

        }

    public void glEnableClientState( final int i )
        {

        }

    public void glFinish()
        {

        }

    public void glFlush()
        {

        }

    public void glFogf( final int i, final float v )
        {

        }

    public void glFogfv( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glFogfv( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glFogx( final int i, final int i1 )
        {

        }

    public void glFogxv( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glFogxv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glFrontFace( final int i )
        {

        }

    public void glFrustumf( final float v, final float v1, final float v2, final float v3, final float v4, final float v5 )
        {

        }

    public void glFrustumx( final int i, final int i1, final int i2, final int i3, final int i4, final int i5 )
        {

        }

    public void glGenTextures( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glGenTextures( final int i, final IntBuffer aIntBuffer )
        {

        }

    public int glGetError()
        {
        return EGL11.EGL_CONTEXT_LOST;
        }

    public void glGetIntegerv( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glGetIntegerv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public String glGetString( final int i )
        {
        return EMPTY_STRING;
        }

    public void glHint( final int i, final int i1 )
        {

        }

    public void glLightModelf( final int i, final float v )
        {

        }

    public void glLightModelfv( final int i, final float[] aFloats, final int i1 )
        {

        }

    public void glLightModelfv( final int i, final FloatBuffer aFloatBuffer )
        {

        }

    public void glLightModelx( final int i, final int i1 )
        {

        }

    public void glLightModelxv( final int i, final int[] aInts, final int i1 )
        {

        }

    public void glLightModelxv( final int i, final IntBuffer aIntBuffer )
        {

        }

    public void glLightf( final int i, final int i1, final float v )
        {

        }

    public void glLightfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glLightfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glLightx( final int i, final int i1, final int i2 )
        {

        }

    public void glLightxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glLightxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glLineWidth( final float v )
        {

        }

    public void glLineWidthx( final int i )
        {

        }

    public void glLoadIdentity()
        {

        }

    public void glLoadMatrixf( final float[] aFloats, final int i )
        {

        }

    public void glLoadMatrixf( final FloatBuffer aFloatBuffer )
        {

        }

    public void glLoadMatrixx( final int[] aInts, final int i )
        {

        }

    public void glLoadMatrixx( final IntBuffer aIntBuffer )
        {

        }

    public void glLogicOp( final int i )
        {

        }

    public void glMaterialf( final int i, final int i1, final float v )
        {

        }

    public void glMaterialfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glMaterialfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glMaterialx( final int i, final int i1, final int i2 )
        {

        }

    public void glMaterialxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glMaterialxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glMatrixMode( final int i )
        {

        }

    public void glMultMatrixf( final float[] aFloats, final int i )
        {

        }

    public void glMultMatrixf( final FloatBuffer aFloatBuffer )
        {

        }

    public void glMultMatrixx( final int[] aInts, final int i )
        {

        }

    public void glMultMatrixx( final IntBuffer aIntBuffer )
        {

        }

    public void glMultiTexCoord4f( final int i, final float v, final float v1, final float v2, final float v3 )
        {

        }

    public void glMultiTexCoord4x( final int i, final int i1, final int i2, final int i3, final int i4 )
        {

        }

    public void glNormal3f( final float v, final float v1, final float v2 )
        {

        }

    public void glNormal3x( final int i, final int i1, final int i2 )
        {

        }

    public void glNormalPointer( final int i, final int i1, final Buffer aBuffer )
        {

        }

    public void glOrthof( final float v, final float v1, final float v2, final float v3, final float v4, final float v5 )
        {

        }

    public void glOrthox( final int i, final int i1, final int i2, final int i3, final int i4, final int i5 )
        {

        }

    public void glPixelStorei( final int i, final int i1 )
        {

        }

    public void glPointSize( final float v )
        {

        }

    public void glPointSizex( final int i )
        {

        }

    public void glPolygonOffset( final float v, final float v1 )
        {

        }

    public void glPolygonOffsetx( final int i, final int i1 )
        {

        }

    public void glPopMatrix()
        {

        }

    public void glPushMatrix()
        {

        }

    public void glReadPixels( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final Buffer aBuffer )
        {

        }

    public void glRotatef( final float v, final float v1, final float v2, final float v3 )
        {

        }

    public void glRotatex( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glSampleCoverage( final float v, final boolean b )
        {

        }

    public void glSampleCoveragex( final int i, final boolean b )
        {

        }

    public void glScalef( final float v, final float v1, final float v2 )
        {

        }

    public void glScalex( final int i, final int i1, final int i2 )
        {

        }

    public void glScissor( final int i, final int i1, final int i2, final int i3 )
        {

        }

    public void glShadeModel( final int i )
        {

        }

    public void glStencilFunc( final int i, final int i1, final int i2 )
        {

        }

    public void glStencilMask( final int i )
        {

        }

    public void glStencilOp( final int i, final int i1, final int i2 )
        {

        }

    public void glTexCoordPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {

        }

    public void glTexEnvf( final int i, final int i1, final float v )
        {

        }

    public void glTexEnvfv( final int i, final int i1, final float[] aFloats, final int i2 )
        {

        }

    public void glTexEnvfv( final int i, final int i1, final FloatBuffer aFloatBuffer )
        {

        }

    public void glTexEnvx( final int i, final int i1, final int i2 )
        {

        }

    public void glTexEnvxv( final int i, final int i1, final int[] aInts, final int i2 )
        {

        }

    public void glTexEnvxv( final int i, final int i1, final IntBuffer aIntBuffer )
        {

        }

    public void glTexImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {

        }

    public void glTexParameterf( final int i, final int i1, final float v )
        {

        }

    public void glTexParameterx( final int i, final int i1, final int i2 )
        {

        }

    public void glTexSubImage2D( final int i, final int i1, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final Buffer aBuffer )
        {

        }

    public void glTranslatef( final float v, final float v1, final float v2 )
        {

        }

    public void glTranslatex( final int i, final int i1, final int i2 )
        {

        }

    public void glVertexPointer( final int i, final int i1, final int i2, final Buffer aBuffer )
        {

        }

    public void glViewport( final int i, final int i1, final int i2, final int i3 )
        {

        }

    private static final String EMPTY_STRING = "";
    }
