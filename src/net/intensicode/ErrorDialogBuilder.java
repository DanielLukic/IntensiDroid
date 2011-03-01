package net.intensicode;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.*;
import android.view.*;
import android.widget.*;
import net.intensicode.core.*;

public final class ErrorDialogBuilder implements DialogInterface.OnClickListener, View.OnClickListener
    {
    public ErrorDialogBuilder( final Activity aActivity, final GameSystem aGameSystem )
        {
        myActivity = aActivity;
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
        if ( myDialog != null && myDialog.isShowing() ) return;

        //#if FEEDBACK
        final Button feedbackButton = new Button( myActivity );
        feedbackButton.setText( "Send Feedback" );
        feedbackButton.setOnClickListener( this );
        //#endif

        final TextView message = new TextView( myActivity );
        if ( myMessageOrNull != null ) message.setText( myMessageOrNull );
        else message.setTag( I18n._( "no description" ) );

        final TextView exception = new TextView( myActivity );
        if ( myExceptionTextOrNull != null ) exception.setText( myExceptionTextOrNull );
        else exception.setTag( I18n._( "no exception data" ) );

        final ScrollView scrollView = new ScrollView( myActivity );
        scrollView.setLayoutParams( LAYOUT_PARAMS );
        scrollView.addView( exception );

        final LinearLayout view = new LinearLayout( myActivity );
        view.setOrientation( LinearLayout.VERTICAL );
        view.setLayoutParams( LAYOUT_PARAMS );
        //#if FEEDBACK
        view.addView( feedbackButton, LAYOUT_PARAMS );
        //#endif
        view.addView( message, LAYOUT_PARAMS );
        view.addView( scrollView, LAYOUT_PARAMS );

        final AlertDialog.Builder builder = new AlertDialog.Builder( myActivity );
        if ( myTitleOrNull != null ) builder.setTitle( myTitleOrNull );
        builder.setCancelable( true );
        builder.setView( view );
        builder.setNegativeButton( "EXIT", this );
        if ( !myCriticalErrorFlag ) builder.setPositiveButton( "CONTINUE", this );

        myDialog = builder.create();
        myDialog.show();
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

        //#if FEEDBACK
        final net.intensicode.configuration.SendFeedback feedback = new net.intensicode.configuration.SendFeedback( myGameSystem );
        feedback.optionalMessageAddon = buffer.toString();

        feedback.trigger();
        //#endif
        }


    private String myTitleOrNull;

    private String myMessageOrNull;

    private String myExceptionTextOrNull;

    private boolean myCriticalErrorFlag;

    private AlertDialog myDialog;

    private final Activity myActivity;

    private final GameSystem myGameSystem;

    private static final LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
    }
