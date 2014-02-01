package hr.foi.air.kriptocavrljanje.db;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.kriptocavrljanje.core.Alias;
import hr.foi.air.kriptocavrljanje.core.UserId;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * klasa za upravljanje upitima za bazu podataka
 * @author Tim_kmmnt
 *
 */
public class UserIdAdapter {

	private SQLiteDatabase db;
	private DBHelper dbHelper;

	/**
	 * konstruktor za inicijalizaciju baze podataka
	 * @param context prosljeðuje trtenutni contex
	 */
	public UserIdAdapter(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * otvara bazu za èitanje
	 */
	private void openToRead() {
		db = dbHelper.getReadableDatabase();
	}

	/**
	 * otvara bazu za pisanje
	 */
	private void openToWrite() {
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * zatvaranje baze
	 */
	private void closeDB() {
		dbHelper.close();
	}

	/**
	 * ubacivanje identifikatora korisnika u bazu
	 * @param userId objekt aktivnog korisnika
	 * @return vraæa true ukoliko sve prolazi bez greške inaèe false
	 */
	public boolean insertUserID(UserId userId) {

		boolean flag = true;

		ContentValues values = new ContentValues();

		values.put("Hash_Id", userId.getHashId());
		values.put("Public_Key", userId.getPublicKey());
		values.put("Private_Key", userId.getPrivateKey());

		openToWrite();

		try {
			db.insert(DBHelper.TABLE_NAME_IDS, null, values);   //upit za umetanje parametara u bazu
			
			/*db.execSQL("INSERT INTO " + DBHelper.TABLE_NAME_IDS
					+ " VALUES(?,?,?)", new String[] { userId.getHashId(),
					userId.getPublicKey(), userId.getPrivateKey()});*/
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			closeDB();
		}

		return flag;
	}

	/**
	 * dohvaæanje identifikatora korisnika iz baze
	 * @return objekt dohvaæenog korisnika
	 */
	public UserId getUserIdInfo() {

		UserId userId = null;
		String[] columns = new String[] { "Hash_Id", "Public_Key",
				"Private_Key" };
		openToRead();
		Cursor c = db.query(DBHelper.TABLE_NAME_IDS, columns, null, null, null,
				null, null);
		
		// èitanje redova iz baze podataka
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			String hashID = c.getString(c.getColumnIndex("Hash_Id"));
			String publicKey = c.getString(c.getColumnIndex("Public_Key"));
			String privateKey = c.getString(c.getColumnIndex("Private_Key"));

			userId = new UserId();
			userId.setHashId(hashID);
			userId.setPublicKey(publicKey);
			userId.setPrivateKey(privateKey);

		}

		closeDB();

		return userId;
	}

	/**
	 * brisanje identifikatora korisnika iz baze
	 * @return
	 */
	public boolean deleteUserId() {

		boolean flag = true;
		openToWrite();

		try {
			db.delete(DBHelper.TABLE_NAME_IDS, null, null);   // upit za brisanje
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			closeDB();
		}
		return flag;
	}

	/**
	 * ubacivanje aliasa korisnika u bazu
	 * @param alias objekt alisa koji æe se ubacivati u bazu
	 * @return  vraèa true ukoliko sve prolazi bez greške inaèe false
	 */
	public boolean insertUserAlias(Alias alias) {

		boolean flag = true;

		ContentValues values = new ContentValues();
		values.put("Hash_Id", alias.getHashId());
		values.put("Nick", alias.getAlias());

		openToWrite();

		try {
			db.insert(DBHelper.TABLE_NAME_ALIASES, null, values);   // upit za ubacivaanje aliasa u bazu
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			closeDB();
		}

		return flag;
	}

	/**
	 * dohvaæa listu svih aliasa iz baze
	 * @return lista svih aliasa
	 */
	public List<Alias> getUserAlias() {

		List<Alias> aliasList = new ArrayList<Alias>();
		String[] columns = new String[] { "Hash_Id", "Nick" };
		openToRead();
		Cursor c = db.query(DBHelper.TABLE_NAME_ALIASES, columns, null, null,
				null, null, null);
		
		// èitanje redova iz baze podataka
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			String hashID = c.getString(c.getColumnIndex("Hash_Id"));
			String nick = c.getString(c.getColumnIndex("Nick"));

			Alias alias = new Alias();
			alias.setHashId(hashID);
			alias.setAlias(nick);

			aliasList.add(alias);

		}

		closeDB();

		return aliasList;
	}

	/**
	 * brisanje aliasa iz baze
	 * @param alias objekt koji se briše iz baze
	 * @return true ako nije došlo do greške inaèe false
	 */
	public boolean deleteUserAlias(Alias alias) {

		boolean flag = true;
		openToWrite();

		try {
			db.delete(DBHelper.TABLE_NAME_ALIASES, DBHelper.COLUMN_ALIASES_ID 
					+ " = " + alias.getHashId(), null);   // upit za brisanje aliasa
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			closeDB();
		}
		return flag;
	}

}
