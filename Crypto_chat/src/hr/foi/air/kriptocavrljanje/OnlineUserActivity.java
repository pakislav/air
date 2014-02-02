package hr.foi.air.kriptocavrljanje;

import java.net.InetAddress;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ActiveUsersAdapter;
import hr.foi.air.kriptocavrljanje.core.UserId;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import java.io.*;
import java.net.*;
import java.util.*;   
import org.apache.http.conn.util.InetAddressUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Aktivnost koja prikazuje aktivne korisnike za komunikaciju
 * koji se dohvaæaju sa servera
 * @author Tim_kmmnt
 *
 */
public class OnlineUserActivity extends Activity {

	private ListView onlineUserslist;
	private ActiveUsersAdapter adapter;
	public static String hash = "";
	public static String  rezultat = "";
	UserIdAdapter userIdAdapter;

	/**
	 * metoda koja prikazuje aktivne korisnike i sve potrebne komponente
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.active_users_list);

		sendMessagetoServer();
		
		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ActiveUsersAdapter(OnlineUserActivity.this,
				R.layout.active_users_row);
		adapter.notifyDataSetChanged();

		onlineUserslist = (ListView) findViewById(R.id.listView_onlineUsersList);

		onlineUserslist.setAdapter(adapter);   // postavnjanje adaptera

		getUsersfromServer();
	}
	
	/*@Override
	protected void onResume() {
		super.onResume();
		
		setContentView(R.layout.active_users_list);

		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ActiveUsersAdapter(OnlineUserActivity.this,
				R.layout.active_users_row);
		adapter.notifyDataSetChanged();

		onlineusers = (TextView) findViewById(R.id.txtView_onlineUsers);
		onlineUserslist = (ListView) findViewById(R.id.listView_onlineUsersList);

		onlineUserslist.setAdapter(adapter);   // postavnjanje adaptera

		getUsersfromServer();
	}*/

	/**
	 * Metoda koja dohvaæa aktivne korisnike sa servera i prosljeðuje ih adpteru
	 */
	public void getUsersfromServer() {
		
		UserId userId = new UserId();
		userIdAdapter = new UserIdAdapter(this);
		 
		userId = userIdAdapter.getUserIdInfo();
		hash = userId.getHashId();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				
				String ip = getIPAddress(true);
				
				
				
				String upit = String.format("http://78.47.115.155/air/get-service.php?c=getUsers&ip=%s&port=9001&f=%s&r=default",ip, hash);
				
				//Log.d(hash, "hhhhhaashhhhhhh");
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(upit);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					rezultat = client.execute(request, handler);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.getConnectionManager().shutdown();
				
				
			}
		});
		
		t.start();
		while(t.isAlive())
		{
		try {
		Thread.sleep(100);
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}
		
		JSONObject o = null;
		try {
			o = new JSONObject(rezultat);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String name = o.getString("getUsers");
			Log.d(name, "cccccccccccc");
			
			String [] oo = name.split(";");
			for (int i = 0 ; i < oo.length; i++) {
				
				if (oo[i] != "") {
					adapter.add(oo[i]);
				}
				
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		// s servera umetanje podataka
	
		
		
	}
	
	/**
	 * Slanje podataka na server
	 */
	public void sendMessagetoServer() {
		
		
		
		UserId userId = new UserId();
		userIdAdapter = new UserIdAdapter(this);
		 
		userId = userIdAdapter.getUserIdInfo();
		hash = userId.getHashId();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				
				String ip = getIPAddress(true);
				
				
				
				String upit = String.format("http://78.47.115.155/air/get-service.php?c=joinRoom&ip=%s&port=9001&f=%s&r=default",ip, hash);
				
				//Log.d(hash, "hhhhhaashhhhhhh");
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(upit);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
				 client.execute(request, handler);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.getConnectionManager().shutdown();
				
				
			}
		});
		
		t.start();
		while(t.isAlive())
		{
		try {
		Thread.sleep(100);
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}
		
		/*JSONObject o = null;
		try {
			o = new JSONObject(rezultat);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String name = o.getString("getUsers");
			Log.d(name, "cccccccccccc");
			
			String [] oo = name.split(";");
			for (int i = 0 ; i < oo.length; i++) {
				
				if (oo[i] != "") {
					adapter.add(oo[i]);
				}
				
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
	}
	
	
	public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
	
}
