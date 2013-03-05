package com.snapwork.xmppchatapp;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class SettingsDialog extends Dialog implements android.view.View.OnClickListener {
    private XMPPClient xmppClient;
    private ProgressDialog mPdialog=null;
    private Context mContext;
    
    public SettingsDialog(XMPPClient xmppClient) {
        super(xmppClient);
        this.xmppClient = xmppClient;
       
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.settings);
        getWindow().setFlags(4, 4);
        setTitle("XMPP Settings");        
        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);	
       
    }

    public void onClick(View v) {
    	if(v == findViewById(R.id.ok))
    	{
    		 mContext = v.getContext();
    	     mPdialog=new ProgressDialog(mContext);
    		Log.d("onclick ","onclick onclick ");
    		mPdialog.setMessage("Connecting...");
			mPdialog.setIndeterminate(true);
			mPdialog.setCancelable(true);
			mPdialog.show();
					
    		Handler mHandler=new Handler();
    	    	mHandler.postDelayed(new Runnable() {
    				@Override
    				public void run() {    			
    	    	mPdialog.dismiss();
    				}
    			},3000);
    	    	
    	   mContext.getApplicationContext().startActivity(new Intent(mContext,BuddyList.class));
    	}
        String host = getText(R.id.host);
        String port = getText(R.id.port);
        String service = getText(R.id.service);
        String username = getText(R.id.userid);
        String password = getText(R.id.password);

        // Create a connection
//        ConnectionConfiguration connConfig =
//                new ConnectionConfiguration(host, Integer.parseInt(port), service);
//        ConnectionConfiguration connConfig =
//              new ConnectionConfiguration("talk.google.com",5222,"gmail.com");
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration("50.16.66.101",5222,"openfire");
        XMPPConnection connection = new XMPPConnection(connConfig);

        
        
        try {
            connection.connect();   
           
            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("XMPPClient", ex.toString());
            xmppClient.setConnection(null);
        }
        try {
            connection.login(username, password);
            Log.i("XMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            xmppClient.setConnection(connection);
            
            Roster roster = connection.getRoster();
            Collection<RosterEntry> entries = roster.getEntries();
         
            System.out.println("\n\n" + entries.size() + " buddy(ies):");
            for(RosterEntry r:entries)
            {
            System.out.println(r.getUser());
            }
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + username);
            Log.e("XMPPClient", ex.toString());
                xmppClient.setConnection(null);
        }
        dismiss();

    }

    private String getText(int id) {
        EditText widget = (EditText) this.findViewById(id);
        return widget.getText().toString();
    }
}
