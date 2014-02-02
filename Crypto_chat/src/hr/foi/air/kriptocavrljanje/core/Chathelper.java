package hr.foi.air.kriptocavrljanje.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

/**
 * @author student
 *
 */
public class Chathelper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ChatHelper");
		
		class ChatHelperThread extends Thread{
			class Zapis{
				private Date mTrenutak = null; // trenutak kreiranja
				private String mId = null; // SHA1(javni kljuc) hex
				private String mJavni = null; // javni kljuc
				
				private Inet4Address mIP = null;
				private int mPort = -1;
				
				public Zapis(String Id, String Javni){
					mTrenutak = new Date();
					mId = Id;
					mJavni = Javni;
				}
				
				public Date getTrenutak(){
					return mTrenutak;
				}
				
				public String getId(){
					return mId;
				}
				
				public String getJavni(){
					return mJavni;
				}
				
				public void setIP(Inet4Address IP){
					mIP = IP;
				}
				
				public void setPort(int Port){
					mPort = Port;
				}
				
				public Inet4Address getIP(){
					return mIP;
				}
				
				public int getPort(){
					return mPort;
				}
			}
			
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
			
			Vector<Zapis> k; // sadrzi sve zapise o trenutnim korisnicima, namjerno samo u RAMu
			DatagramSocket s = null; // listener
			
			public ChatHelperThread(){
				try {
					InetAddress host = Inet4Address.getByName("localhost");
					s = new DatagramSocket(PORT, host);
					k = new Vector<Zapis>();
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
						if(pp.jeZahtjevZaRegistracijom()){
							Zapis z = new Zapis(pp.getSalje(), pp.getPoruka());
							z.setIP((Inet4Address)p.getAddress());
							z.setPort(p.getPort());
							k.add(z);
							//TODO: implementirati ciscenje memorije od viska registracija
							System.out.println(String.format("Registrirao: %s %s (%s:%d)", z.getId(), z.getJavni(), z.getIP().toString(), z.getPort()));
							Paket odgovor = new Paket(Paket.NULA20, pp.getSalje(), "OK");
							byte[] bodgovor = odgovor.serijaliziraj();
							DatagramPacket podgovor = new DatagramPacket(bodgovor, bodgovor.length, z.getIP(), z.getPort());
							s.send(podgovor);
						}
						if(pp.jeZahtjevZaPopisom()){
							StringBuffer poruka = new StringBuffer();
							for(int i = (k.size() > 10 ? 10 : k.size()); i > 0; --i){ // zadnjih 10 obrnuto sortirano po vremenu
								Zapis z = k.get(i - 1);
								System.out.println(String.format("Popis %d/10: %s %s %s (%s:%d)", i, z.getTrenutak().toString(), z.getId(), z.getJavni(), z.getIP().toString(), z.getPort()));
								poruka.append(z.getId());
								poruka.append("|");
								poruka.append(z.getJavni());
								poruka.append(";");
							}
							Paket odgovor = new Paket(Paket.NULA20, pp.getSalje(), poruka.toString());
							byte[] bodgovor = odgovor.serijaliziraj();
							DatagramPacket podgovor = new DatagramPacket(bodgovor, bodgovor.length, p.getAddress(), p.getPort());
							s.send(podgovor);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
		new ChatHelperThread().start();
	}

}