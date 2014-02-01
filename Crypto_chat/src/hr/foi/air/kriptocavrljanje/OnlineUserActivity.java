package hr.foi.air.kriptocavrljanje;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ActiveUsersAdapter;
import hr.foi.air.kriptocavrljanje.core.Alias;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Aktivnost koja prikazuje aktivne korisnike za komunikaciju
 *  koji se dohvaæaju sa servera
 * @author Tim_kmmnt
 *
 */
public class OnlineUserActivity extends Activity {

	private TextView onlineusers;
	private ListView onlineUserslist;
	private ActiveUsersAdapter adapter;

	/**
	 * metoda koja prikazuje aktivne korisnike i sve potrebne komponente
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.active_users_list);

		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ActiveUsersAdapter(OnlineUserActivity.this,
				R.layout.active_users_row);

		onlineusers = (TextView) findViewById(R.id.txtView_onlineUsers);
		onlineUserslist = (ListView) findViewById(R.id.listView_onlineUsersList);

		onlineUserslist.setAdapter(adapter);   // postavnjanje adaptera

		getUsersfromServer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setContentView(R.layout.active_users_list);

		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ActiveUsersAdapter(OnlineUserActivity.this,
				R.layout.active_users_row);

		onlineusers = (TextView) findViewById(R.id.txtView_onlineUsers);
		onlineUserslist = (ListView) findViewById(R.id.listView_onlineUsersList);

		onlineUserslist.setAdapter(adapter);   // postavnjanje adaptera
		
		onlineUserslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				
				
			}
		});

		getUsersfromServer();
	}

	/**
	 * Metoda koja dohvaæa aktivne korisnike sa servera i prosljeðuje ih adpteru
	 */
	public void getUsersfromServer() {

		// testiranje unosa aliasa
		Alias alias = new Alias();
		alias.setHashId("Mibo");
		alias.setAlias("1337");

		UserIdAdapter userIdAdapter = new UserIdAdapter(this);
		userIdAdapter.insertUserAlias(alias);

		// s servera umetanje podataka
		adapter.add("Mibo");
		adapter.add("Paki");
		adapter.add("Neno");
		if (userIdAdapter.getUserIdInfo() != null) {
			adapter.add(userIdAdapter.getUserIdInfo().getHashId());
		}
		
	}
	
	
}
