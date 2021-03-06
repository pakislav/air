package hr.foi.air.kriptocavrljanje.core;



	
	

	import java.net.DatagramPacket;
	import java.net.DatagramSocket;
	import java.net.Inet4Address;
	import java.net.InetAddress;

	/**
	 * @author student
	 *
	 */
	public class ChatClient {
		
		//public final static String HELPER_IP = "78.47.115.155";
		public final static String HELPER_IP = "127.0.0.1";
		public final static int HELPER_PORT = 9001;
		
		public final static String KLIJENT_IP = "127.0.0.1";
		public final static int KLIJENT_PORT = 9002;
		
		public static void main(String[] args) {
			System.out.println("ChatClient");
			
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
		
			final int IN_BUF_LEN = Paket.PAKET_LEN_MAX;
			byte[] in = new byte[IN_BUF_LEN];
			
			try {
				DatagramSocket s = new DatagramSocket(KLIJENT_PORT, Inet4Address.getByName(KLIJENT_IP));
				Paket p = new Paket("AAAAAAAAAAAAAAAAAAAA", Paket.NULA20, "moj javni kljuc");
				byte[] registriraj = p.serijaliziraj();
				DatagramPacket poruka = new DatagramPacket(registriraj, registriraj.length, Inet4Address.getByName(HELPER_IP), HELPER_PORT);
				s.send(poruka);
				
				DatagramPacket odgovor = new DatagramPacket(in, in.length);
				s.receive(odgovor);
				Paket podgovor = new Paket();
				podgovor.deserijaliziraj(in);
				System.out.println(podgovor.mPoruka);
				
				p = new Paket("AAAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAA", "0");
				registriraj = p.serijaliziraj();
				poruka = new DatagramPacket(registriraj, registriraj.length, Inet4Address.getByName(HELPER_IP), HELPER_PORT);
				s.send(poruka);
				
				odgovor = new DatagramPacket(in, in.length);
				s.receive(odgovor);
				podgovor = new Paket();
				podgovor.deserijaliziraj(in);
				System.out.println(podgovor.mPoruka);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	

