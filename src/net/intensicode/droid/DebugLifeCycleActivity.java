package net.intensicode.droid;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.*;
import net.intensicode.util.Log;

public class DebugLifeCycleActivity extends Activity
    {
    //#if DEBUG

    public void onCreate( Bundle savedInstanceState )
        {
        Log.debug( "onCreate" );
        super.onCreate( savedInstanceState );
        }

    public void onConfigurationChanged( Configuration aConfiguration )
        {
        Log.debug( "onConfigurationChanged" );
        super.onConfigurationChanged( aConfiguration );
        }

    public Object onRetainNonConfigurationInstance()
        {
        Log.debug( "onRetainNonConfigurationInstance" );
        return super.onRetainNonConfigurationInstance();
        }

    public void onLowMemory()
        {
        Log.debug( "onLowMemory" );
        super.onLowMemory();
        }

    public boolean onKeyDown( int aKeyCode, KeyEvent aKeyEvent )
        {
        Log.debug( "onKeyDown {} {}", new Integer( aKeyCode ), aKeyEvent );
        return super.onKeyDown( aKeyCode, aKeyEvent );
        }

    public boolean onKeyUp( int aKeyCode, KeyEvent aKeyEvent )
        {
        Log.debug( "onKeyUp {} {}", new Integer( aKeyCode ), aKeyEvent );
        return super.onKeyUp( aKeyCode, aKeyEvent );
        }

    public boolean onKeyMultiple( int aKeyCode, int aRepeatCount, KeyEvent aKeyEvent )
        {
        Log.debug( "onKeyMultiple {} {}", new Integer( aKeyCode ), aKeyEvent );
        Log.debug( "onKeyMultiple count={}", aRepeatCount );
        return super.onKeyMultiple( aKeyCode, aRepeatCount, aKeyEvent );
        }

    public boolean onTouchEvent( MotionEvent aMotionEvent )
        {
        Log.debug( "onTouchEvent" );
        return super.onTouchEvent( aMotionEvent );
        }

    public boolean onTrackballEvent( MotionEvent aMotionEvent )
        {
        Log.debug( "onTrackballEvent" );
        return super.onTrackballEvent( aMotionEvent );
        }

//    public void onUserInteraction()
//        {
//        Log.debug( "onUserInteraction" );
//        super.onUserInteraction();
//        }

    public void onWindowAttributesChanged( WindowManager.LayoutParams aLayoutParams )
        {
        Log.debug( "onWindowAttributesChanged" );
        super.onWindowAttributesChanged( aLayoutParams );
        }

    public void onContentChanged()
        {
        Log.debug( "onContentChanged" );
        super.onContentChanged();
        }

    public void onWindowFocusChanged( boolean b )
        {
        Log.debug( "onWindowFocusChanged" );
        super.onWindowFocusChanged( b );
        }

    public boolean onCreateThumbnail( Bitmap aBitmap, Canvas aCanvas )
        {
        Log.debug( "onCreateThumbnail" );
        return super.onCreateThumbnail( aBitmap, aCanvas );
        }

    public View onCreatePanelView( int i )
        {
        Log.debug( "onCreatePanelView" );
        return super.onCreatePanelView( i );
        }

    public boolean onCreatePanelMenu( int i, Menu aMenu )
        {
        Log.debug( "onCreatePanelMenu" );
        return super.onCreatePanelMenu( i, aMenu );
        }

    public boolean onPreparePanel( int i, View aView, Menu aMenu )
        {
        Log.debug( "onPreparePanel" );
        return super.onPreparePanel( i, aView, aMenu );
        }

    public boolean onMenuOpened( int i, Menu aMenu )
        {
        Log.debug( "onMenuOpened" );
        return super.onMenuOpened( i, aMenu );
        }

    public boolean onMenuItemSelected( int i, MenuItem aMenuItem )
        {
        Log.debug( "onMenuItemSelected" );
        return super.onMenuItemSelected( i, aMenuItem );
        }

    public void onPanelClosed( int i, Menu aMenu )
        {
        Log.debug( "onPanelClosed" );
        super.onPanelClosed( i, aMenu );
        }

    public boolean onCreateOptionsMenu( Menu aMenu )
        {
        Log.debug( "onCreateOptionsMenu" );
        return super.onCreateOptionsMenu( aMenu );
        }

    public boolean onOptionsItemSelected( MenuItem aMenuItem )
        {
        Log.debug( "onOptionsItemSelected" );
        return super.onOptionsItemSelected( aMenuItem );
        }

    public boolean onPrepareOptionsMenu( Menu aMenu )
        {
        Log.debug( "onPrepareOptionsMenu" );
        return super.onPrepareOptionsMenu( aMenu );
        }

    public void onOptionsMenuClosed( Menu aMenu )
        {
        Log.debug( "onOptionsMenuClosed" );
        super.onOptionsMenuClosed( aMenu );
        }

    public void onCreateContextMenu( ContextMenu aContextMenu, View aView, ContextMenu.ContextMenuInfo aContextMenuInfo )
        {
        Log.debug( "onCreateContextMenu" );
        super.onCreateContextMenu( aContextMenu, aView, aContextMenuInfo );
        }

    public boolean onContextItemSelected( MenuItem aMenuItem )
        {
        Log.debug( "onContextItemSelected" );
        return super.onContextItemSelected( aMenuItem );
        }

    public void onContextMenuClosed( Menu aMenu )
        {
        Log.debug( "onContextMenuClosed" );
        super.onContextMenuClosed( aMenu );
        }

    public CharSequence onCreateDescription()
        {
        Log.debug( "onCreateDescription" );
        return super.onCreateDescription();
        }

    public View onCreateView( String s, Context aContext, AttributeSet aAttributeSet )
        {
        Log.debug( "onCreateView" );
        return super.onCreateView( s, aContext, aAttributeSet );
        }

    protected void onRestoreInstanceState( Bundle aBundle )
        {
        Log.debug( "onRestoreInstanceState" );
        super.onRestoreInstanceState( aBundle );
        }

    protected void onPostCreate( Bundle aBundle )
        {
        Log.debug( "onPostCreate" );
        super.onPostCreate( aBundle );
        }

    protected void onStart()
        {
        Log.debug( "onStart" );
        super.onStart();
        }

    protected void onRestart()
        {
        Log.debug( "onRestart" );
        super.onRestart();
        }

    protected void onResume()
        {
        Log.debug( "onResume" );
        super.onResume();
        }

    protected void onPostResume()
        {
        Log.debug( "onPostResume" );
        super.onPostResume();
        }

    protected void onNewIntent( Intent aIntent )
        {
        Log.debug( "onNewIntent" );
        super.onNewIntent( aIntent );
        }

    protected void onSaveInstanceState( Bundle aBundle )
        {
        Log.debug( "onSaveInstanceState" );
        super.onSaveInstanceState( aBundle );
        }

    protected void onPause()
        {
        Log.debug( "onPause" );
        super.onPause();
        }

    protected void onUserLeaveHint()
        {
        Log.debug( "onUserLeaveHint" );
        super.onUserLeaveHint();
        }

    protected void onStop()
        {
        Log.debug( "onStop" );
        super.onStop();
        }

    protected void onDestroy()
        {
        Log.debug( "onDestroy" );
        super.onDestroy();
        }

    protected Dialog onCreateDialog( int i )
        {
        Log.debug( "onCreateDialog" );
        return super.onCreateDialog( i );
        }

    protected void onPrepareDialog( int i, Dialog aDialog )
        {
        Log.debug( "onPrepareDialog" );
        super.onPrepareDialog( i, aDialog );
        }

    protected void onActivityResult( int i, int i1, Intent aIntent )
        {
        Log.debug( "onActivityResult" );
        super.onActivityResult( i, i1, aIntent );
        }

    //#endif
    }
