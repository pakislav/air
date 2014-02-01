package hr.foi.air.kriptocavrljanje.adapters;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.ChatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	private String text = "";
	
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
	 * metoda koja umeæe Custom Layouta u ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
				//Integer index = (Integer) v.getTag();
                //remove(getItem(index));    
                //notifyDataSetChanged();

				Intent intent = new Intent(getContext(), ChatActivity.class);   // prelazak u aktivnost za razgovor
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            getContext().startActivity(intent);
				
			}
		});
		
		// postavljanje aliasa poznatim korisnicima
		activeUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Integer index = (Integer) v.getTag();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle("Zamjeni s imenom:");

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
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});

				builder.show();
			}
		});
		
		activeUser.setText(getItem(position));
		
		return row;
	}

}
