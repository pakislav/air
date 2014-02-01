package hr.foi.air.kriptocavrljanje.db;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.kriptocavrljanje.core.Alias;
import hr.foi.air.kriptocavrljanje.core.UserId;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserIdAdapter {

	private SQLiteDatabase db;
	private DBHelper dbHelper;

	public UserIdAdapter(Context context) {
		dbHelper = new DBHelper(context);
	}

	private void openToRead() {
		db = dbHelper.getReadableDatabase();
	}

	private void openToWrite() {
		db = dbHelper.getWritableDatabase();
	}

	private void closeDB() {
		dbHelper.close();
	}

	public boolean insertUserID(UserId userId) {

		boolean flag = true;

		ContentValues values = new ContentValues();

		values.put("Hash_Id", userId.getHashId());
		values.put("Public_Key", userId.getPublicKey());
		values.put("Private_Key", userId.getPrivateKey());

		openToWrite();

		try {
			db.insert(DBHelper.TABLE_NAME_IDS, null, values);
			
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

	public UserId getUserIdInfo() {

		UserId userId = null;
		String[] columns = new String[] { "Hash_Id", "Public_Key",
				"Private_Key" };
		openToRead();
		Cursor c = db.query(DBHelper.TABLE_NAME_IDS, columns, null, null, null,
				null, null);
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

	public boolean deleteUserId() {

		boolean flag = true;
		openToWrite();

		try {
			db.delete(DBHelper.TABLE_NAME_IDS, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			closeDB();
		}
		return flag;
	}

	public boolean insertUserAlias(Alias alias) {

		boolean flag = true;

		ContentValues values = new ContentValues();
		values.put("Hash_Id", alias.getHashId());
		values.put("Nick", alias.getAlias());

		openToWrite();

		try {
			db.insert(DBHelper.TABLE_NAME_ALIASES, null, values);
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			closeDB();
		}

		return flag;
	}

	public List<Alias> getUserAlias() {

		List<Alias> aliasList = new ArrayList<Alias>();
		String[] columns = new String[] { "Hash_Id", "Nick" };
		openToRead();
		Cursor c = db.query(DBHelper.TABLE_NAME_ALIASES, columns, null, null,
				null, null, null);
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

	public boolean deleteUserAlias(Alias alias) {

		boolean flag = true;
		openToWrite();

		try {
			db.delete(DBHelper.TABLE_NAME_ALIASES, DBHelper.COLUMN_ALIASES_ID
					+ " = " + alias.getHashId(), null);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			closeDB();
		}
		return flag;
	}

}
