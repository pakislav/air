package hr.foi.air.kriptocavrljanje;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ListAdapter;
import hr.foi.air.kriptocavrljanje.core.Comment;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	
	private ListView chatList;
	private ListAdapter adapter;
	private EditText sendMessage;
	private Button sendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		
		chatList = (ListView) findViewById(R.id.listView_chat);
		
		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ListAdapter(getApplicationContext(), R.layout.listrow_layout);
		chatList.setAdapter(adapter);
		
		sendButton = (Button) findViewById(R.id.btn_sendMessage);
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!sendMessage.getText().toString().isEmpty()) {
					adapter.add(new Comment(sendMessage.getText().toString(), false));
					sendMessage.setText("");
				}	
			}
		});
		
		sendMessage = (EditText) findViewById(R.id.editTxt_message);
		sendMessage.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// Izvršavanje koda u slu�?aju klika na Enter
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					adapter.add(new Comment(sendMessage.getText().toString(), false));
					sendMessage.setText("");
					return true;
				}
				return false;
			}
		});
		
		addItems();
	}
	
	// dodavanje proizvoljnih vrijednosti radi efekta
	private void addItems() {
		adapter.add(new Comment("Hey kaj ima?", true));
		adapter.add(new Comment("Ej! Ništa previše.", false));
	}

}
