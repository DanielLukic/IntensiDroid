package net.intensicode.configuration;

import android.app.AlertDialog;
import android.content.*;
import android.view.ViewGroup;
import android.widget.*;
import net.intensicode.ConfigurableActionValue;

public final class ConfigurableActionDialog implements ConfigurableDialog
    {
    public ConfigurableActionDialog( final Context aContext, final ConfigurableActionValue aConfigurableValue )
        {
        myContext = aContext;
        myConfigurableValue = aConfigurableValue;
        }

    // From ConfigurableDialog

    public final void createDialog()
        {
        final TextView infoText = new TextView( myContext );
        infoText.setText( getInfoText() );

        final LinearLayout view = new LinearLayout( myContext );
        view.setOrientation( LinearLayout.VERTICAL );
        view.setLayoutParams( new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        view.addView( infoText, new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );

        final AlertDialog.Builder builder = new AlertDialog.Builder( myContext );
        builder.setTitle( getTitle() );
        builder.setCancelable( true );
        builder.setView( view );
        builder.setNegativeButton( "Cancel", null );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener()
        {
        public void onClick( final DialogInterface aDialogInterface, final int i )
            {
            myConfigurableValue.trigger();
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


    private final Context myContext;

    private final ConfigurableActionValue myConfigurableValue;
    }
