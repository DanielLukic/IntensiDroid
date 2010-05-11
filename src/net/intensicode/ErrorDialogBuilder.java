package net.intensicode;

import android.app.AlertDialog;
import android.content.*;
import android.view.*;
import android.widget.*;
import net.intensicode.configuration.SendFeedback;
import net.intensicode.core.*;

public final class ErrorDialogBuilder implements DialogInterface.OnClickListener, View.OnClickListener
    {
    public ErrorDialogBuilder( final Context aContext, final GameSystem aGameSystem )
        {
        myContext = aContext;
        myGameSystem = aGameSystem;
        }

    public final void setTitle( final String aTitleOrNull )
        {
        myTitleOrNull = aTitleOrNull;
        }

    public final void setMessage( final String aMessageOrNull )
        {
        myMessageOrNull = aMessageOrNull;
        }

    public final void setCause( final String aExceptionTextOrNull )
        {
        myExceptionTextOrNull = aExceptionTextOrNull;
        }

    public void setCritical( final boolean aCritical )
        {
        myCriticalErrorFlag = aCritical;
        }

    public final void createDialog()
        {
        final TextView message = new TextView( myContext );
        if ( myMessageOrNull != null ) message.setText( myMessageOrNull );
        else message.setTag( I18n._( "no description" ) );

        final TextView exception = new TextView( myContext );
        if ( myExceptionTextOrNull != null ) exception.setText( myExceptionTextOrNull );
        else exception.setTag( I18n._( "no exception data" ) );

        final Button feedbackButton = new Button( myContext );
        feedbackButton.setText( "Send Feedback" );
        feedbackButton.setOnClickListener( this );

        final LinearLayout view = new LinearLayout( myContext );
        view.setOrientation( LinearLayout.VERTICAL );
        view.setLayoutParams( LAYOUT_PARAMS );
        view.addView( message, LAYOUT_PARAMS );
        view.addView( exception, LAYOUT_PARAMS );
        view.addView( feedbackButton, LAYOUT_PARAMS );

        final AlertDialog.Builder builder = new AlertDialog.Builder( myContext );
        if ( myTitleOrNull != null ) builder.setTitle( myTitleOrNull );
        builder.setCancelable( true );
        builder.setView( view );
        builder.setNegativeButton( "EXIT", this );
        if ( !myCriticalErrorFlag ) builder.setPositiveButton( "CONTINUE", this );

        final AlertDialog dialog = builder.create();
        dialog.show();
        }

    // From DialogInterface.OnClickListener

    public void onClick( final DialogInterface aDialogInterface, final int aInteger )
        {
        if ( DialogInterface.BUTTON_POSITIVE == aInteger ) return;
        if ( DialogInterface.BUTTON_NEGATIVE == aInteger ) myGameSystem.shutdownAndExit();
        }

    // From View.OnClickListener

    public void onClick( final View aView )
        {
        final StringBuffer buffer = new StringBuffer();
        if ( myMessageOrNull != null )
            {
            buffer.append( myMessageOrNull );
            buffer.append( "\n" );
            }
        if ( myExceptionTextOrNull != null )
            {
            buffer.append( myExceptionTextOrNull );
            buffer.append( "\n" );
            }

        final SendFeedback feedback = new SendFeedback( myGameSystem );
        feedback.optionalMessageAddon = buffer.toString();

        feedback.trigger();
        }


    private String myTitleOrNull;

    private String myMessageOrNull;

    private String myExceptionTextOrNull;

    private boolean myCriticalErrorFlag;

    private final Context myContext;

    private final GameSystem myGameSystem;

    private static final LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
    }