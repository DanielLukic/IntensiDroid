package net.intensicode;

import android.app.AlertDialog;
import android.content.*;
import android.view.ViewGroup;
import android.widget.*;
import net.intensicode.ConfigurableIntegerValue;

public final class ConfigurableSeekBarDialog implements ConfigurableDialog
    {
    public ConfigurableSeekBarDialog( final Context aContext, final ConfigurableIntegerValue aConfigurableValue )
        {
        myContext = aContext;
        myConfigurableValue = aConfigurableValue;
        }

    // From ConfigurableDialog

    public final void createDialog()
        {
        final TextView infoText = new TextView( myContext );
        infoText.setText( getInfoText() );

        final TextView currentValue = new TextView( myContext );
        currentValue.setText( getValueAsText( getValueForSeekBar() ) );

        final SeekBar valueControl = new SeekBar( myContext );
        valueControl.setMax( getMaximumForSeekBar() );
        valueControl.setProgress( getValueForSeekBar() );
        valueControl.setKeyProgressIncrement( getStepSize() );
        valueControl.setOnSeekBarChangeListener( new MyOnSeekBarChangeListener( currentValue ) );

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

    // Implementation

    private String getTitle()
        {
        return myConfigurableValue.getTitle();
        }

    private String getInfoText()
        {
        return myConfigurableValue.getInfoText();
        }

    private String getValueAsText( final int aConfiguredValue )
        {
        return myConfigurableValue.getValueAsText( aConfiguredValue );
        }

    private void setNewValue( final int aConfiguredValue )
        {
        myConfigurableValue.setNewValue( aConfiguredValue );
        }

    private int getMaximumForSeekBar()
        {
        return myConfigurableValue.getMaxValue();
        }

    private int getValueForSeekBar()
        {
        return myConfigurableValue.getCurrentValue();
        }

    protected int getStepSize()
        {
        return myConfigurableValue.getStepSize();
        }


    private final Context myContext;

    private final ConfigurableIntegerValue myConfigurableValue;


    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
        {
        public MyOnSeekBarChangeListener( final TextView aCurrentValue )
            {
            myCurrentValue = aCurrentValue;
            }

        public final void onProgressChanged( final SeekBar aSeekBar, final int i, final boolean b )
            {
            myCurrentValue.setText( getValueAsText( i ) );
            }

        public final void onStartTrackingTouch( final SeekBar aSeekBar )
            {
            }

        public final void onStopTrackingTouch( final SeekBar aSeekBar )
            {
            }

        private final TextView myCurrentValue;
        }
    }
