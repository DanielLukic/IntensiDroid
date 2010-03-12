package net.intensicode.dialogs;

import android.app.AlertDialog;
import android.content.*;
import android.view.ViewGroup;
import android.widget.*;
import net.intensicode.core.GameSystem;

public abstract class SeekBarDialogBase
    {
    public final void createDialog()
        {
        final TextView infoText = new TextView( myContext );
        infoText.setText( getInfoText() );

        final TextView currentValue = new TextView( myContext );
        currentValue.setText( getValueAsText( getValueForSeekBar() ) );

        final SeekBar valueControl = new SeekBar( myContext );
        valueControl.setMax( getMaximumForSeekBar() );
        valueControl.setProgress( getValueForSeekBar() );
        valueControl.setKeyProgressIncrement( getKeyIncrement() );
        valueControl.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
        {
        public final void onProgressChanged( final SeekBar aSeekBar, final int i, final boolean b )
            {
            currentValue.setText( getValueAsText( i ) );
            }

        public final void onStartTrackingTouch( final SeekBar aSeekBar )
            {
            }

        public final void onStopTrackingTouch( final SeekBar aSeekBar )
            {
            }
        } );

        final LinearLayout view = new LinearLayout( myContext );
        view.setOrientation( LinearLayout.VERTICAL );
        view.setLayoutParams( new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        view.addView( infoText, new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        view.addView( valueControl, new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        view.addView( currentValue, new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );

        final AlertDialog.Builder builder = new AlertDialog.Builder( myContext );
        builder.setTitle( getTitle() );
        builder.setCancelable( true );
        builder.setView( view );
        builder.setNegativeButton( "Cancel", null );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener()
        {
        public void onClick( final DialogInterface aDialogInterface, final int i )
            {
            setNewValue( valueControl.getProgress() );
            }
        } );

        final AlertDialog dialog = builder.create();
        dialog.show();
        }

    // Protected API

    protected SeekBarDialogBase( final Context aContext, final GameSystem aGameSystem )
        {
        myContext = aContext;
        myGameSystem = aGameSystem;
        }

    protected final GameSystem system()
        {
        return myGameSystem;
        }

    // Subclass API

    protected abstract String getTitle();

    protected abstract String getInfoText();

    protected abstract String getValueAsText( final int aSeekBarValue );

    protected abstract void setNewValue( final int aSeekBarValue );

    protected abstract int getMaximumForSeekBar();

    protected abstract int getValueForSeekBar();

    protected int getKeyIncrement()
        {
        return DEFAULT_KEY_INCREMENT;
        }

    protected static final int DEFAULT_KEY_INCREMENT = 1;


    private final Context myContext;

    private final GameSystem myGameSystem;
    }
