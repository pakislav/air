package hr.foi.air.kriptocavrljanje;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.core.UserId;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	private long brojac = 0;
	Intent i;
	UserIdAdapter userIdAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userIdAdapter = new UserIdAdapter(this);
		
		setContentView(R.layout.main_layout);

		Button genId = (Button) findViewById(R.id.btn_generirajIdentitet);
		Button chat = (Button) findViewById(R.id.btn_chat);
		Button delId = (Button) findViewById(R.id.btn_deleteId);

		View.OnClickListener handler = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (v.getId()) {

				case R.id.btn_generirajIdentitet:
					runProgressBar(v);
					break;
				case R.id.btn_chat:
					i = new Intent(getApplicationContext(),
							OnlineUserActivity.class);
					startActivity(i);
					break;
				case R.id.btn_deleteId:
					deleteId();
					break;
				}
			}
		};

		genId.setOnClickListener(handler);
		chat.setOnClickListener(handler);
		delId.setOnClickListener(handler);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	public void runProgressBar(View v) {
		progressBar = new ProgressDialog(v.getContext());
		progressBar.setCancelable(true);
		progressBar.setMessage("Generiranje kljuca...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		progressBarStatus = 0;

		brojac = 0;

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (progressBarStatus < 100) {

					progressBarStatus = generateKeys();

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Update progress bar
					progressBarHandler.post(new Runnable() {
						@Override
						public void run() {
							progressBar.setProgress(progressBarStatus);
						}
					});
				}

				// generiranje zavrsilo
				if (progressBarStatus >= 100) {

					// sleep 2 sekunde, kako bi se vidio 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					progressBar.dismiss();

					progressBarHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Uspješno je generiran Vaš ključ!",
									Toast.LENGTH_SHORT).show();
							i = new Intent(getApplicationContext(),
									OnlineUserActivity.class);
							startActivity(i);
						}
					});
				}

			}
		}).start();
	}

	// simulator generatora kljuceva
	public int generateKeys() {

		UserId userId = new UserId();
		userId.setHashId("sfsfsfs");
		userId.setPublicKey("publickey");
		userId.setPrivateKey("privatekey");
		
		if(userIdAdapter.insertUserID(userId)) {
			
		}
		
		while (brojac <= 1000000) {

			brojac++;

			if (brojac == 100000) {
				return 10;
			} else if (brojac == 200000) {
				return 35;
			} else if (brojac == 300000) {
				return 70;
			}

		}

		return 100;
	}

	// generiranje hash identiteta
	public String getHash() {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		String text = "This is some text";

		try {
			md.update(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] digest = md.digest();

		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(Integer.toHexString(b & 0xff));
		}

		return sb.toString();
	}

	public void deleteId() {
		
		if(userIdAdapter.deleteUserId()) {
			Toast.makeText(getApplicationContext(),
					"Uspješno je izbrisan Vaš ključ!",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(),
					"Došlo je do pogreške, pokušajte ponovo!",
					Toast.LENGTH_SHORT).show();
		}
	}
}
