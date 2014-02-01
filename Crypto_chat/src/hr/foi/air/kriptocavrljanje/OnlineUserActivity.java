package hr.foi.air.kriptocavrljanje;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ActiveUsersAdapter;
import hr.foi.air.kriptocavrljanje.core.Alias;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineUserActivity extends Activity {

	private TextView onlineusers;
	private ListView onlineUserslist;
	private ActiveUsersAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.active_users_list);

		adapter = new ActiveUsersAdapter(OnlineUserActivity.this,
				R.layout.active_users_row);

		onlineusers = (TextView) findViewById(R.id.txtView_onlineUsers);
		onlineUserslist = (ListView) findViewById(R.id.listView_onlineUsersList);

		onlineUserslist.setAdapter(adapter);

		getUsersfromServer();
	}

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
