package hr.foi.air.kriptocavrljanje.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * klasa u kojoj se stvara baza podataka
 * @author Tim_kmmnt
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	/**
	 * nazivi tablica, atributa i baze
	 */
	public static final String TABLE_NAME_IDS = "Identifiers";
	public static final String TABLE_NAME_ALIASES = "Aliases";
	public static final String COLUMN_IDS_ID = "Hash_Id";
	public static final String COLUMN_IDS_PUBLICKEY = "Public_Key";
	public static final String COLUMN_IDS_PRIVATEKEY = "Private_Key";
	public static final String COLUMN_ALIASES_ID = "Hash_Id";
	public static final String COLUMN_ALIASES_ALIAS = "Nick";

	private static final String DATABASE_NAME = "crypto.db";
	private static final int DATABASE_VERSION = 1;

	// upit za stvaranje tablice Identifiers
	private static final String CREATE_TABLE_IDS = "create table "
			+ TABLE_NAME_IDS + "(" + COLUMN_IDS_ID + " varchar(50) unique primary key, "
			+ COLUMN_IDS_PUBLICKEY + " varchar(50) not null, " + COLUMN_IDS_PRIVATEKEY
			+ " varchar(50) not null);";

	// upit za stvaranje tablice Aliases
	private static final String CREATE_TABLE_ALIASES = "create table "
			+ TABLE_NAME_ALIASES + "(" + COLUMN_ALIASES_ID
			+ " varchar(50) unique primary key, " + COLUMN_ALIASES_ALIAS + " varchar(50) not null);";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 *  metoda koja stvara tablice i bazu
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		
		/*String sqlQuery1 = "CREATE TABLE Identifiers (Hash_Id VARCHAR(100) PRIMARY KEY, PublicKey VARCHAR(100), PrivateKey VARCHAR(100));";
		database.execSQL(sqlQuery1);
		String sqlQuery2 = "CREATE TABLE Aliases (Hash_Id VARCHAR(100) PRIMARY KEY, Nick VARCHAR(100));";
		database.execSQL(sqlQuery2);*/
		
		database.execSQL(CREATE_TABLE_IDS);
		database.execSQL(CREATE_TABLE_ALIASES);
	}

	/**
	 * metoda za ažuriranje i brisanej baze
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_IDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ALIASES);
		onCreate(db);
	}

}
