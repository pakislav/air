package hr.foi.air.kriptocavrljanje;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ActiveUsersAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Aktivnost koja prikazuje aktivne korisnike za komunikaciju
 * koji se dohvaæaju sa servera
 * @author Tim_kmmnt
 *
 */
public class OnlineUserActivity extends Activity {

	private ListView onlineUserslist;
	private ActiveUsersAdapter adapter;

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

		// s servera umetanje podataka
		adapter.add("Mibo");
		adapter.add("Paki");
		adapter.add("Neno");
		
		
	}
	
	/**
	 * Slanje podataka na server
	 */
	public void sendMessagetoServer() {
		
		// test git
		
	}
	
}
