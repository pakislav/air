package hr.foi.air.kriptocavrljanje.adapters;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * adapter klasa koja u ListView stavlja Custom Layoute ovisno o broju aktivnih korisnika
 * @author Tim_kmmnt
 *
 */
public class ActiveUsersAdapter extends ArrayAdapter<String> {
	
	private TextView activeUser;
	private Button startChat;
	private List<String> activeUsersList = new ArrayList<String>();
	//private String text = "";
	//private UserIdAdapter userIdAdapter;
	
	/**
	 * umetanje aktivnih korisnika u listu
	 */
	@Override
	public void add(String string) {
		activeUsersList.add(string);
		super.add(string);
	}
	
	public ActiveUsersAdapter(Context context, int resource) {
		super(context, resource);
	}
	
	/**
	 * metoda koja ume�e Custom Layouta u ListView
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.active_users_row, parent, false);
		}
		
		activeUser = (TextView) row.findViewById(R.id.txtView_activeUserId);
		
		startChat = (Button) row.findViewById(R.id.btn_chatWithuser);
		startChat.setTag(position);
		
		//pokretanje razgovora sa navedenim korisnikom
		startChat.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getContext(), ChatActivity.class);   // prelazak u aktivnost za razgovor
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            
	            Bundle b = new Bundle();
	            b.putString("key", getItem(position).toString());   //slanje id-a sugovornika
	            intent.putExtras(b); 
	            
	            getContext().startActivity(intent);
				
			}
		});
		
		// postavljanje aliasa poznatim korisnicima
		activeUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle("Postavljanje aliasa:");

				// postavljanje unosa
				final EditText input = new EditText(getContext());
				// specificiranje tipa unosa
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				builder.setView(input);

				// postavljanje gumba
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        text = input.getText().toString();
				        
				        userIdAdapter = new UserIdAdapter(getContext());
				        Alias alias = new Alias();
				        alias.setHashId(getItem(position).toString());
				        alias.setAlias(text);
				        userIdAdapter.insertUserAlias(alias);
				        
				        //TO DO refreshanje aktivnosti kako bi se prikazale promjene
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});

				builder.show();*/
			}
		});
		
		activeUser.setText(getItem(position));
		
		return row;
	}

}
