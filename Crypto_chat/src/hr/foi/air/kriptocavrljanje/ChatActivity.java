package hr.foi.air.kriptocavrljanje;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.adapters.ListAdapter;
import hr.foi.air.kriptocavrljanje.core.Comment;
import hr.foi.air.kriptocavrljanje.core.UserId;
import hr.foi.air.kriptocavrljanje.db.UserIdAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Aktivnost koja prikazuje razgovor između sugovornika
 * 
 * @author Tim_kmmnt
 * 
 */
public class ChatActivity extends Activity {

	private ListView chatList;
	private ListAdapter adapter;
	private EditText sendMessage;
	private Button sendButton;

	public static String hash = "";
	public static String rezultat = "";
	public static String sugovornik = "";
	public static String ip = "";
	UserIdAdapter userIdAdapter;
	public static Inet4Address sugovornikIp = null;
	public static int sugorovnikPort = 9001;

	/**
	 * onCreate metoda koja prikazuje komunikaciju
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);

		Bundle b = getIntent().getExtras();
		 sugovornik = b.getString("key"); // dohvacanje id-a sugovornika

		 UserId userId = new UserId();
		userIdAdapter = new UserIdAdapter(this);

		 userId = userIdAdapter.getUserIdInfo();
		hash = userId.getHashId();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				String ip = OnlineUserActivity.getIPAddress(true);

				String upit = String
						.format("http://78.47.115.155/air/get-service.php?c=getEndpoints&ip=%s&port=9001&f=%s&s=%s",
								ip, hash, sugovornik);
				
				// String upit = "http://78.47.115.155/air/get-service.php?c=getEndpoints&ip=192.168.5.101&port=9001&f=e755e82f689211663de577d3fed1f45fac79452&s=feea216822a2eaa72ca11867686572d977902045";

				// Log.d(hash, "fhffgfgfggfgfgfgfgfgfgfgfgfgf");
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(upit);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					rezultat = client.execute(request, handler);
					//Log.d(rezultat, "rezzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
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
		while (t.isAlive()) {
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
			String name = o.getString("getEndpoints");

			String[] oo = name.split(";");
			for (int i = 0; i < oo.length; i++) {
				//Log.d(oo[i], "vuco je zakon");
				
				if (oo[i] != "") {

					try {
						sugovornikIp = (Inet4Address) Inet4Address
								.getByName(oo[i].split(":")[0]);
						sugorovnikPort = Integer.parseInt(oo[i].split(":")[1]);
						Log.d("----------", String.format("%d %s",
								sugorovnikPort, sugovornikIp));
					} catch (UnknownHostException e) {

						e.printStackTrace();
					}

				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// server
		
		class ChatHelperThread extends Thread{
			
			/**
			 * Paket verzija 14.01.31.a
			 * @author student
			 *
			 */
			class Paket{ // predstavlja strukturu svakog paketa naseg protokola
				// fiksnih 20 + 20 + 4 okteta
				private String mSalje = null; // SHA1(javni kljuc) hex onoga tko salje poruku ili 20 nula
				private String mPrima = null; // SHA1(javni kljuc) hex onoga kome je poruka namjenjena ili 20 nula
				private Short mDuljinaPoruke = null; // do 32767 okteta moze biti dug sadrzaj poruke (4 hex) ili 4 nule
				// varijabilni dio poruke 4 + n okteta (gdje je n < 32763)
				private Short mBezPaddinga = null; // duljina poruke nakon dekripcije (4 hex) ili 4 nule (fiksno)
				private String mPoruka = null; // sama poruka
				
				// dakle ukupno paket uvijek ima do (20 + 20 + 4) + [32767 zaokruzeno na blok asimetricnog outputa]
				public final static int PAKET_LEN_MAX = 40000; //XXX cca
				public final static String NULA20 = "00000000000000000000";
				
				public Paket(){
					mSalje = NULA20;
					mPrima = NULA20;
					mDuljinaPoruke = 0;
					mBezPaddinga = 0;
					mPoruka = null;
				}
				/**
				 * 
				 * @param Salje SHA1 u hex (20)
				 * @param Prima SHA1 u hex (20)
				 * @param Poruka
				 */
				public Paket(String Salje, String Prima, String Poruka){
					//TODO validacija Salje, Prima, Poruka (duzine, sadrzaj, itd.)
					mSalje = Salje;
					mPrima = Prima;
					//XXX kriptiranje...
					mDuljinaPoruke = (short)(Poruka.length() + 4);
					mBezPaddinga = (short)(Poruka.length());
					mPoruka = Poruka;
				}
				
				public void deserijaliziraj(byte[] niz){
					/* XXX
					 * Za one koji zele znati vise:
					 * "mSalje = new String(niz, 0, 20);"
					 * , zasto ovaj nacin rusi JVM?
					 * # JRE version: 6.0_38-b05
					 * # Java VM: Java HotSpot(TM) 64-Bit Server VM (20.13-b02 mixed mode linux-amd64 compressed oops)
					 * # Problematic frame:
					 * # V  [libjvm.so+0x4e31c7]  java_lang_String::utf8_length(oopDesc*)+0x67
					 */
					StringBuffer tmp = new StringBuffer();
					for(int i = 0; i < 20; ++i){
						tmp.append((char)niz[i]);
					}
					mSalje = tmp.toString(); //System.out.println(String.format("%s", mSalje));
					tmp.delete(0, tmp.length());
					for(int i = 20; i < 40; ++i){
						tmp.append((char)niz[i]);
					}
					mPrima = tmp.toString(); //System.out.println(String.format("%s", mPrima));
					tmp.delete(0, tmp.length());
					for(int i = 40; i < 44; ++i){
						tmp.append((char)niz[i]);
					}
					mDuljinaPoruke = Short.parseShort(tmp.toString(), 16); //System.out.println(String.format("%d", mDuljinaPoruke));
					tmp.delete(0, tmp.length());
					//TODO validacija duljine nastavka prema mDuljinaPoruke
					//XXX krece kriptirani dio
					for(int i = 44; i < 48; ++i){
						tmp.append((char)niz[i]);
					}
					mBezPaddinga = Short.parseShort(tmp.toString(), 16); //System.out.println(String.format("%d", mBezPaddinga));
					tmp.delete(0, tmp.length());
					for(int i = 48; i < (int)mBezPaddinga + 48; ++i){
						tmp.append((char)niz[i]);
					}
					mPoruka = tmp.toString(); //System.out.println(String.format("%s", mPoruka));			
				}
				
				public boolean jeZahtjevZaRegistracijom(){ // registriraj me
					if(mPrima.compareTo(NULA20) == 0){
						return true;
					}
					return false;
				}
				
				public boolean jeZahtjevZaPopisom(){ // tko je online?
					if(mPrima.compareTo(mSalje) == 0){
						return true;
					}
					return false;
				}
				
				public String getSalje(){
					return mSalje;
				}
				
				public String getPoruka(){
					return mPoruka;
				}
				
				/*
				public String stringUHex(String s){
					char[] z = s.toCharArray();
					StringBuffer h = new StringBuffer();
					for(int i = 0; i < z.length; ++i){
						if(z[i] < 0x10){
							h.append('0');
						}
						h.append(Integer.toString(((int)z[i]), 16));
					}
					return h.toString();
				}
				
				public String hexUString(String h){
					StringBuilder r = new StringBuilder();
					for(int i = 0; i < h.length() - 1; i += 2){
						String b = h.substring(i, i + 2);
						r.append((char)(Integer.parseInt(b, 16)));
					}
					return r.toString();
				}
				*/
				public byte[] serijaliziraj(){
					byte[] niz = new byte[PAKET_LEN_MAX];
					for(int i = 0; i < 20; ++i){
						niz[i] = (byte)mSalje.charAt(i);
					}
					for(int i = 0; i < 20; ++i){
						niz[i + 20] = (byte)mPrima.charAt(i);
					}
					String tmp = String.format("%04x", mDuljinaPoruke);
					for(int i = 0; i < 4; ++i){
						niz[i + 40] = (byte)tmp.charAt(i);
					}
					//XXX kriptirano...
					tmp = String.format("%04x", mBezPaddinga);
					for(int i = 0; i < 4; ++i){
						niz[i + 44] = (byte)tmp.charAt(i);
					}
					for(int i = 0; i < mBezPaddinga; ++i){ //XXX mBezPaddinga mora biti mPoruka.length !
						niz[i + 48] = (byte)mPoruka.charAt(i);
					}
					return niz;
				}
			}
			
			public final static int IN_BUF_LEN = Paket.PAKET_LEN_MAX;
			public final static int PORT = 9001;
			

			DatagramSocket s = null; // listener
			
			public ChatHelperThread(){
				try {
					InetAddress host = Inet4Address.getByName(ip);
					s = new DatagramSocket(PORT, host);

				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			@Override
			public void run() {
				
				while(true){ // glavni radnik
					try{
						byte[] in = new byte[IN_BUF_LEN];
						
						DatagramPacket p = new DatagramPacket(in, in.length);
						s.receive(p); // blokira thread dok se klijent ne spoji
						Paket pp = new Paket();
						pp.deserijaliziraj(in);
						Log.d(pp.getPoruka(), "primili poruku");
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
		new ChatHelperThread().start();
		// zavr�etak servera
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		chatList = (ListView) findViewById(R.id.listView_chat);

		// referenciranje Custom adaptera i njegovo postavljanje na ListView-u
		adapter = new ListAdapter(getApplicationContext(),
				R.layout.listrow_layout);
		chatList.setAdapter(adapter);

		sendButton = (Button) findViewById(R.id.btn_sendMessage);
		sendButton.setText("send");

		// dodavanje novog komentara u razgovor
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!sendMessage.getText().toString().isEmpty()) {
					adapter.add(new Comment(sendMessage.getText().toString(),
							false));
					//////////////////////////////////////////////////////////////////////////////
					
						
						/**
						 * Paket verzija 14.01.31.a
						 * @author student
						 *
						 */
						class Paket{ // predstavlja strukturu svakog paketa naseg protokola
							// fiksnih 20 + 20 + 4 okteta
							private String mSalje = null; // SHA1(javni kljuc) hex onoga tko salje poruku ili 20 nula
							private String mPrima = null; // SHA1(javni kljuc) hex onoga kome je poruka namjenjena ili 20 nula
							private Short mDuljinaPoruke = null; // do 32767 okteta moze biti dug sadrzaj poruke (4 hex) ili 4 nule
							// varijabilni dio poruke 4 + n okteta (gdje je n < 32763)
							private Short mBezPaddinga = null; // duljina poruke nakon dekripcije (4 hex) ili 4 nule (fiksno)
							private String mPoruka = null; // sama poruka
							
							// dakle ukupno paket uvijek ima do (20 + 20 + 4) + [32767 zaokruzeno na blok asimetricnog outputa]
							public final static int PAKET_LEN_MAX = 40000; //XXX cca
							public final static String NULA20 = "00000000000000000000";
							
							public Paket(){
								mSalje = NULA20;
								mPrima = NULA20;
								mDuljinaPoruke = 0;
								mBezPaddinga = 0;
								mPoruka = null;
							}
							/**
							 * 
							 * @param Salje SHA1 u hex (20)
							 * @param Prima SHA1 u hex (20)
							 * @param Poruka
							 */
							public Paket(String Salje, String Prima, String Poruka){
								//TODO validacija Salje, Prima, Poruka (duzine, sadrzaj, itd.)
								mSalje = Salje;
								mPrima = Prima;
								//XXX kriptiranje...
								mDuljinaPoruke = (short)(Poruka.length() + 4);
								mBezPaddinga = (short)(Poruka.length());
								mPoruka = Poruka;
							}
							
							public void deserijaliziraj(byte[] niz){
								/* XXX
								 * Za one koji zele znati vise:
								 * "mSalje = new String(niz, 0, 20);"
								 * , zasto ovaj nacin rusi JVM?
								 * # JRE version: 6.0_38-b05
								 * # Java VM: Java HotSpot(TM) 64-Bit Server VM (20.13-b02 mixed mode linux-amd64 compressed oops)
								 * # Problematic frame:
								 * # V  [libjvm.so+0x4e31c7]  java_lang_String::utf8_length(oopDesc*)+0x67
								 */
								StringBuffer tmp = new StringBuffer();
								for(int i = 0; i < 20; ++i){
									tmp.append((char)niz[i]);
								}
								mSalje = tmp.toString(); //System.out.println(String.format("%s", mSalje));
								tmp.delete(0, tmp.length());
								for(int i = 20; i < 40; ++i){
									tmp.append((char)niz[i]);
								}
								mPrima = tmp.toString(); //System.out.println(String.format("%s", mPrima));
								tmp.delete(0, tmp.length());
								for(int i = 40; i < 44; ++i){
									tmp.append((char)niz[i]);
								}
								mDuljinaPoruke = Short.parseShort(tmp.toString(), 16); //System.out.println(String.format("%d", mDuljinaPoruke));
								tmp.delete(0, tmp.length());
								//TODO validacija duljine nastavka prema mDuljinaPoruke
								//XXX krece kriptirani dio
								for(int i = 44; i < 48; ++i){
									tmp.append((char)niz[i]);
								}
								mBezPaddinga = Short.parseShort(tmp.toString(), 16); //System.out.println(String.format("%d", mBezPaddinga));
								tmp.delete(0, tmp.length());
								for(int i = 48; i < (int)mBezPaddinga + 48; ++i){
									tmp.append((char)niz[i]);
								}
								mPoruka = tmp.toString(); //System.out.println(String.format("%s", mPoruka));			
							}
							
							public boolean jeZahtjevZaRegistracijom(){ // registriraj me
								if(mPrima.compareTo(NULA20) == 0){
									return true;
								}
								return false;
							}
							
							public boolean jeZahtjevZaPopisom(){ // tko je online?
								if(mPrima.compareTo(mSalje) == 0){
									return true;
								}
								return false;
							}
							
							public String getSalje(){
								return mSalje;
							}
							
							public String getPoruka(){
								return mPoruka;
							}
							

							public byte[] serijaliziraj(){
								byte[] niz = new byte[PAKET_LEN_MAX];
								for(int i = 0; i < 20; ++i){
									niz[i] = (byte)mSalje.charAt(i);
								}
								for(int i = 0; i < 20; ++i){
									niz[i + 20] = (byte)mPrima.charAt(i);
								}
								String tmp = String.format("%04x", mDuljinaPoruke);
								for(int i = 0; i < 4; ++i){
									niz[i + 40] = (byte)tmp.charAt(i);
								}
								//XXX kriptirano...
								tmp = String.format("%04x", mBezPaddinga);
								for(int i = 0; i < 4; ++i){
									niz[i + 44] = (byte)tmp.charAt(i);
								}
								for(int i = 0; i < mBezPaddinga; ++i){ //XXX mBezPaddinga mora biti mPoruka.length !
									niz[i + 48] = (byte)mPoruka.charAt(i);
								}
								return niz;
							}
						}
					
						final int IN_BUF_LEN = Paket.PAKET_LEN_MAX;

						
						try {
							DatagramSocket s = new DatagramSocket(9002, Inet4Address.getByName(ip));
							Paket p = new Paket(hash, sugovornik, "lidija bacic");
							byte[] pporuka = p.serijaliziraj();
							Log.d("-aaa-------->", ip.toLowerCase());
							DatagramPacket poruka = new DatagramPacket(pporuka, pporuka.length, sugovornikIp, sugorovnikPort);
							
							Log.d("--salji-------->", ip);
							s.send(poruka);
							Log.d("------------------->", "saljem");
							/*
							DatagramPacket odgovor = new DatagramPacket(in, in.length);
							s.receive(odgovor);
							Paket podgovor = new Paket();
							podgovor.deserijaliziraj(in);
							System.out.println(podgovor.mPoruka);
							*/

						} catch (Exception e) {
							e.printStackTrace();
							Log.d("-------11111111111------------>", "saljem");
						}
						///////////////////////////////////////////////////
				
				
				sendMessage.setText("");
				}
			}
			});
		
		sendMessage = (EditText) findViewById(R.id.editTxt_message);
		sendMessage.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// Izvršavanje koda u slučaju klika na Enter
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					adapter.add(new Comment(sendMessage.getText().toString(),
							false));
					sendMessage.setText("");
					return true;
				}
				return false;
			}
		});

		addItems();
	}

	/**
	 * metoda koja dodaje komentare na adekvatnu stranu lisview liste ovisno od
	 * koje osobe je poslana
	 */
	private void addItems() {
		adapter.add(new Comment("Hey kaj ima?", true));
		adapter.add(new Comment("Ej! Ništa previše.", false));
	}

}
