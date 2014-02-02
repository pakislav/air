package hr.foi.air.kriptocavrljanje;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.core.UserId;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Početna aktivnost za generiranje identiteta korisnika
 * ako je identitet već generiran mogućnost odlaska u drugi aktovnost te
 * brisanje generiranog identiteta
 * @author Tim_kmmnt
 * 
 */
public class MainActivity extends Activity {

	private ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	private long brojac = 0;
	private Button genId;
	private Button chat;
	private Button delId;
	private String id;
	private String privateKey;
	private String publicKey;
	Intent i;
	UserIdAdapter userIdAdapter;
	
	/**
	 * onCreate metoda unutar koje se generiraju potrebne fukcionalnosti
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userIdAdapter = new UserIdAdapter(this);
		
		setContentView(R.layout.main_layout);

		genId = (Button) findViewById(R.id.btn_generirajIdentitet);
		chat = (Button) findViewById(R.id.btn_chat);
		delId = (Button) findViewById(R.id.btn_deleteId);
		
		if(userIdAdapter.getUserIdInfo() == null) {
			chat.setEnabled(false);
		}

		//izvođenje aktivnosti klikom na gumbove		 		 
		View.OnClickListener handler = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (v.getId()) {

				case R.id.btn_generirajIdentitet:
					runProgressBar(v);
					break;
				case R.id.btn_chat:
					//prelazak u drugu aktivnost
					i = new Intent(getApplicationContext(),
							OnlineUserActivity.class);
					startActivity(i);
					break;
				case R.id.btn_deleteId:
					deleteId();   //brisanje generiranog identiteta  
					break;
				}
			}
		};

		genId.setOnClickListener(handler);
		chat.setOnClickListener(handler);
		delId.setOnClickListener(handler);

	}
	
	/*@Override
	protected void onResume() {
		super.onResume();
		
		userIdAdapter = new UserIdAdapter(this);
		
		setContentView(R.layout.main_layout);

		genId = (Button) findViewById(R.id.btn_generirajIdentitet);
		chat = (Button) findViewById(R.id.btn_chat);
		delId = (Button) findViewById(R.id.btn_deleteId);
		
		if(userIdAdapter.getUserIdInfo() == null) {
			chat.setEnabled(false);
		}

		//izvođenje aktivnosti klikom na gumbove		 		 
		View.OnClickListener handler = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (v.getId()) {

				case R.id.btn_generirajIdentitet:
					runProgressBar(v);
					break;
				case R.id.btn_chat:
					//prelazak u drugu aktivnost
					i = new Intent(getApplicationContext(),
							OnlineUserActivity.class);
					startActivity(i);
					break;
				case R.id.btn_deleteId:
					deleteId();   //brisanje generiranog identiteta  
					break;
				}
			}
		};

		genId.setOnClickListener(handler);
		chat.setOnClickListener(handler);
		delId.setOnClickListener(handler);
	}*/
	
	/**
	 * metoda instanciranja menija
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	/**
	 * metoda unutar koje se generira identitet, upisuje u bazu podataka i prikazuje napredak u izvršavanju
	 * @param v id komponente koja je kliknuta tj. gumba generira identitet
	 */
	public void runProgressBar(View v) {
		/*
		progressBar = new ProgressDialog(v.getContext());
		progressBar.setCancelable(true);
		progressBar.setMessage("Generiranje kljuca...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		progressBarStatus = 0;

		brojac = 0;

		//pokretanje dretve
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
		*/
		progressBarHandler.post(new Runnable() {
			@Override
			public void run() {
				generateKeys();
				Toast.makeText(getApplicationContext(),
						"Uspješno je generiran Vaš ključ!",
						Toast.LENGTH_SHORT).show();
				i = new Intent(getApplicationContext(),
						OnlineUserActivity.class);
				startActivity(i);
			}
		});
		
	}

	/**
	 *  metoda generiranja ključeva 
	 * @return vraća broj 100 nakone generiranja ključeva
	 */
	public int generateKeys() {
		
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		
		try {
			generator.init(new RSAKeyGenerationParameters(
					new BigInteger("10001", 16),			/* Javni eksponent: 65537 */
					SecureRandom.getInstance("SHA1PRNG"),
					1024,									    /* Enkripcija 1024-bitnim asimetricnim kljucem */
					80)										/* Sigurnost da su brojevi prosti */
			);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		
		AsymmetricCipherKeyPair parKljuceva = generator.generateKeyPair();
		
		RSAPrivateCrtKeyParameters privatniKljuc = (RSAPrivateCrtKeyParameters)parKljuceva.getPrivate();
		RSAKeyParameters javniKljuc = (RSAKeyParameters)parKljuceva.getPublic();

		privateKey = serijalizirajKljuc(privatniKljuc);
		publicKey = serijalizirajKljuc(javniKljuc);
		
		id = getHash(publicKey);
		
		UserId userId = new UserId();
		userId.setHashId(id);
		userId.setPublicKey(publicKey);
		userId.setPrivateKey(privateKey);
		
		if(!userIdAdapter.insertUserID(userId)) {
			Toast.makeText(getApplicationContext(),
					"Došlo je do greške!",
					Toast.LENGTH_SHORT).show();
		}
		
		/*
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
		*/

		return 100;
	}

	/**
	 *  generiranje hash identiteta
	 * @return hash identitet
	 */
	public String getHash(String kljuc) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		try {
			md.update(kljuc.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] digest = md.digest();

		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(Integer.toHexString(b & 0xff));
		}

		//Log.d(sb.toString(), "aaaaaaaaaaaaaaa");
		return sb.toString();
	}

	/**
	 * metoda brisanja identiteta iz baze
	 */
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
		
		chat.setEnabled(false);
	}
	
	/**
	 * Metoda pretvara instancu RSAPrivateCrtKeyParameters klase u niz okteta
	 * @param kljuc
	 * @return
	 */
	private static String serijalizirajKljuc(RSAPrivateCrtKeyParameters kljuc){
		BigInteger modulus = kljuc.getModulus();
		BigInteger exponent = kljuc.getExponent();
		BigInteger p = kljuc.getP();
		BigInteger q = kljuc.getQ();
		BigInteger dp = kljuc.getDP();
		BigInteger dq = kljuc.getDQ();
		BigInteger publicExponent = kljuc.getPublicExponent();
		BigInteger qInv = kljuc.getQInv();
		String format = String.format("%s %s %s %s %s %s %s %s", modulus.toString(), exponent.toString(), p.toString(), q.toString(), dp.toString(), dq.toString(), publicExponent.toString(), qInv.toString());
		return format;
	}
	
	/**
	 * Metoda pretvara instancu RSAKeyParameters klase u niz okteta
	 * @param kljuc
	 * @return
	 */
	private static String serijalizirajKljuc(RSAKeyParameters kljuc){
		BigInteger modulus = kljuc.getModulus();
		BigInteger exponent = kljuc.getExponent();
		String format = String.format("%s %s", modulus.toString(), exponent.toString());
		return format;
	}
	
	/**
	 * Metoda pretvara niz okteta u instancu klase RSAPrivateCrtKeyParameters
	 * @param kljuc
	 * @return
	 */
	private static RSAPrivateCrtKeyParameters deserijalizirajPrivatniKljuc(byte[] kljuc){
		String format = new String(kljuc);
		String[] elementi = format.split(" ");
		BigInteger modulus = new BigInteger(elementi[0]);
		BigInteger exponent = new BigInteger(elementi[1]);
		BigInteger p = new BigInteger(elementi[2]);
		BigInteger q = new BigInteger(elementi[3]);
		BigInteger dp = new BigInteger(elementi[4]);
		BigInteger dq = new BigInteger(elementi[5]);
		BigInteger publicExponent = new BigInteger(elementi[6]);
		BigInteger qInv = new BigInteger(elementi[7]);
		return new RSAPrivateCrtKeyParameters(modulus, publicExponent, exponent, p, q, dp, dq, qInv);
	}

	/**
	 * Funkcija pretvara niz okteta u instancu klase RSAKeyParameters
	 * @param kljuc
	 * @return
	 */
	private static RSAKeyParameters deserijalizirajJavniKljuc(byte[] kljuc){
		String format = new String(kljuc);
		String[] elementi = format.split(" ");
		BigInteger modulus = new BigInteger(elementi[0]);
		BigInteger exponent = new BigInteger(elementi[1]);
		return new RSAKeyParameters(false, modulus, exponent);
	}
	
	/**
	 * Metoda kriptira algoritmom RSA zadanu datoteku
	 * @param datotekaIzvorna Datoteka koja se zeli kriptirati
	 * @param datotekaKljuca Datoteka s javnim kljucem
	 * @param datotekaOdredisna Datoteka u koju ce se pohraniti rezultat enkripcije
	 */
	private static void kriptirajAsimetricno(String datotekaIzvorna, String datotekaKljuca, String datotekaOdredisna){
		AsymmetricBlockCipher sifra = new RSAEngine();
		sifra = new OAEPEncoding(sifra);
		
		byte[] kljuc = null;
		try {
			
		} catch (Exception ex) {
			
		}
		RSAKeyParameters javniKljuc = deserijalizirajJavniKljuc(kljuc);
		sifra.init(true, javniKljuc);

		byte[] cistopis = null;
		
		try {
			
		} catch (Exception ex) {
			
		}
		
		byte[] sifratBlok;
		int indeks = 0, duljinaUlaznogBloka;
		boolean zadnjiBlok;
		try {
			
			while(true){
				int preostalo = cistopis.length - indeks;
				if(preostalo < sifra.getInputBlockSize()){
					duljinaUlaznogBloka = preostalo;
					zadnjiBlok = true;
				}
				else{
					duljinaUlaznogBloka = sifra.getInputBlockSize();
					zadnjiBlok = false;
				}
				sifratBlok = sifra.processBlock(cistopis, indeks, duljinaUlaznogBloka);
				
				if(zadnjiBlok == true)
					break;
				indeks += duljinaUlaznogBloka;
			}
			
		} catch (Exception ex) {
			
		}
	}
	
	/**
	 * Metoda dekriptira algoritmom RSA zadanu datoteku
	 * @param datotekaIzvorna Datoteka koja se zeli dekriptirati
	 * @param datotekaKljuca Datoteka s privatnim kljucem
	 * @param datotekaOdredisna Datoteka u koju ce se pohraniti dekriptirani sadrzaj
	 */
	private static void dekriptirajAsimetricno(String datotekaIzvorna, String datotekaKljuca, String datotekaOdredisna){
		AsymmetricBlockCipher sifra = new RSAEngine();
		sifra = new OAEPEncoding(sifra);
		
		byte[] kljuc = null;
		try {
			
		} catch (Exception ex) {
			
		}
		RSAPrivateCrtKeyParameters privatniKljuc = deserijalizirajPrivatniKljuc(kljuc);
		sifra.init(false, privatniKljuc);

		byte[] sifrat = null;
		
		try {
			
		} catch (Exception ex) {
			
		}
		
		byte[] cistopisBlok;
		int indeks = 0, duljinaUlaznogBloka;
		boolean zadnjiBlok;
		try {
			
			while(true){
				int preostalo = sifrat.length - indeks;
				if(preostalo < sifra.getInputBlockSize()){
					duljinaUlaznogBloka = preostalo;
					zadnjiBlok = true;
				}
				else{
					duljinaUlaznogBloka = sifra.getInputBlockSize();
					zadnjiBlok = false;
				}
				if(duljinaUlaznogBloka != 0){
					cistopisBlok = sifra.processBlock(sifrat, indeks, duljinaUlaznogBloka);
					
				}
				if(zadnjiBlok == true)
					break;
				indeks += duljinaUlaznogBloka;
			}
			
		} catch (Exception ex) {
			
		}
	}
	
}
