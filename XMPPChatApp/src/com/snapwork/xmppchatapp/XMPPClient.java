package com.snapwork.xmppchatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.muc.UserStatusListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class XMPPClient extends Activity {

    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();
    private SettingsDialog mDialog;
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;
    private XMPPConnection connection;
    private ProgressDialog mPdialog=null;
    private static boolean isOnline;
    private Context mContext;
    String shost,sport,mservice,suserId,spass;
    TextView host,port,service,userId,pass;
    ArrayList<String> buddyList=new ArrayList<String>();
    public void onResume()
    {
    	super.onResume();
    	Log.d("onResume","onResume");
    }
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i("XMPPClient", "onCreate called"+isOnline);
        setContentView(R.layout.settings);       
        
        /*mRecipient = (EditText) this.findViewById(R.id.recipient);
        Log.i("XMPPClient", "mRecipient = " + mRecipient);
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i("XMPPClient", "mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i("XMPPClient", "mList = " + mList);*/
        host=(TextView)findViewById(R.id.host); 
        port=(TextView)findViewById(R.id.port); 
        service=(TextView)findViewById(R.id.service); 
        userId=(TextView)findViewById(R.id.userid); 
        pass=(TextView)findViewById(R.id.password); 
        
       // setListAdapter();
        mContext=getApplicationContext();
        // Dialog for getting the xmpp settings
       // mDialog = new SettingsDialog(this);      
        PostCreate();
        // Set a listener to show the settings dialog
//        Button setup = (Button) this.findViewById(R.id.setup);
//        Button send = (Button) this.findViewById(R.id.send);
        Button login = (Button) this.findViewById(R.id.ok);  
        
        login.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
			       
		         shost= host.getText().toString();
		         sport = port.getText().toString();
		         mservice = service.getText().toString();		         
		         suserId = userId.getText().toString();		         
		         spass = pass.getText().toString();
		         
				 ConnectionConfiguration connConfig =
			                new ConnectionConfiguration("50.16.66.101",5222,"openfire");
			        XMPPConnection connection = new XMPPConnection(connConfig);
				
			        try {
			            connection.connect();   
			           
			            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
			        } catch (XMPPException ex) {
			            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
			            Log.e("XMPPClient", ex.toString());
			            //xmppClient.setConnection(null);
			        }
			            
			        try {
			            connection.login(suserId, spass);
			            
			            try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            
			            
			            Log.i("XMPPClient", "Logged in as " + connection.getUser());

			            // Set the status to available
			            Presence presence = new Presence(Presence.Type.available);
			            connection.sendPacket(presence);
			         //   setConnection(connection);
			            
			            Roster roster = connection.getRoster();
			            Collection<RosterEntry> entries = roster.getEntries();
			            
			            
			           
			            
			         
			            System.out.println("\n\n" + entries.size() + " buddy(ies):");
			            
			            for(RosterEntry r:entries)
			            {
			            	
			            	
			            	Presence availability = roster.getPresence(r.getUser());
			            	
			            	
			            	
			            
			            	
			            	System.out.println(r.getUser());
			            	
			            	
			            	System.out.println("Presence : "+availability);
			            	
			            	
			            buddyList.add(r.getUser());
			            }
			        } catch (XMPPException ex) {
			            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + suserId);
			            Log.e("XMPPClient", ex.toString());
			               setConnection(null);
			        } 
			        
				Intent i=new Intent(XMPPClient.this,BuddyList.class);				
				i.putStringArrayListExtra("buddyList", buddyList);
				startActivity(i);
				
			}
		});
       // Log.d("isonline flag=",""+isOnline);
        /*if(isOnline)
        {
        	setup.setEnabled(true);
        	setup.setClickable(true);
        	send.setEnabled(true);
        	send.setClickable(true);
        }
        else{
        	isOnline=false;
        	setup.setEnabled(false);
        	setup.setClickable(false);
        	send.setEnabled(false);
        	send.setClickable(false);
        }*/
        /*setup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {            	
                mHandler.post(new Runnable() {
                    public void run() {
                        mDialog.show();                       
                    }
                });
            }
        });

        // Set a listener to send a chat text message
       
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = mRecipient.getText().toString();
                String text = mSendText.getText().toString();

                Log.i("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);
                connection.sendPacket(msg);
                messages.add(connection.getUser() + ":");
                messages.add(text);
                setListAdapter();
                mList.requestFocus();
                mList.setSelection(mList.getCount()-1);
                mSendText.setText("");
            }
        });*/
    }

    private void ShowToast()
	{
		Toast.makeText(XMPPClient.this, "No Network Connection!", Toast.LENGTH_LONG).show();
	}


	public static boolean isNetworkAvailable(Context context) {
		boolean value = false;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			value = true;	
			isOnline = true;
		}
		return value;
	} 
    
	public boolean isOnline() {
		try {
			if (isNetworkAvailable(getApplicationContext())) {
				URL url = new URL("http://www.rediff.com");
				ProxyUrlUtil pU = new ProxyUrlUtil();
				String retStr =pU.getProxyXML(url, getApplicationContext());	
			//	Log.d("retsry=",""+retStr);
				pU=null;
				
				if(retStr==null)
					return false;

				if(retStr.length()<10)
					return false;

				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void PostCreate()
	{
		Log.d("postcreate","postcreate");		
				if(!isOnline())
				{
					Log.d("postcreate in","postcreate in");
					mHandler.post(new Runnable() { public void run() {				
						ShowToast();
					} });
					return;
				} 
	}
    /**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param connection
     */
    public void setConnection
            (XMPPConnection
                    connection) {
        this.connection = connection;
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                        // Add the incoming message to the list view
                       /* mHandler.post(new Runnable() {
                            public void run() {
                              //  setListAdapter();
                                mList.requestFocus();
                                mList.setSelection(mList.getCount()-1);
                            }
                        });*/
                    }
                }
            }, filter);
        }
    }

    /*private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multi_line_list_item,
                messages);
        mList.setAdapter(adapter);           
    }*/
}
