package net.intensicode.configuration;

import android.app.AlertDialog;
import android.content.*;
import android.view.ViewGroup;
import android.widget.*;
import net.intensicode.ConfigurableBooleanValue;

public final class ConfigurableToggleDialog implements ConfigurableDialog
    {
    public ConfigurableToggleDialog( final Context aContext, final ConfigurableBooleanValue aConfigurableValue )
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
        currentValue.setText( getValueAsText( getValueForCheckBox() ) );

        final CheckBox valueControl = new CheckBox( myContext );
        valueControl.setChecked( myConfigurableValue.getCurrentValue() );
        valueControl.setOnCheckedChangeListener( new MyOnCheckedChangeListener( currentValue ) );

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
            setNewValue( valueControl.isChecked() );
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

    private String getValueAsText( final boolean aConfiguredValue )
        {
        return myConfigurableValue.getValueAsText( aConfiguredValue );
        }

    private void setNewValue( final boolean aConfiguredValue )
        {
        myConfigurableValue.setNewValue( aConfiguredValue );
        }

    private boolean getValueForCheckBox()
        {
        return myConfigurableValue.getCurrentValue();
        }


    private final Context myContext;

    private final ConfigurableBooleanValue myConfigurableValue;


    private class MyOnCheckedChangeListener implements CheckBox.OnCheckedChangeListener
        {
        public MyOnCheckedChangeListener( final TextView aCurrentValue )
            {
            myCurrentValue = aCurrentValue;
            }

        public final void onCheckedChanged( final CompoundButton aCompoundButton, final boolean aCheckedState )
            {
            myCurrentValue.setText( getValueAsText( aCheckedState ) );
            }

        private final TextView myCurrentValue;
        }
    }
