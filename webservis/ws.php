<?php
/*
Funkcionalnost servisa kao monolitna klasa
*/
class Ws{
	public function joinRoom($callerEndpoint, $callerFingerprint, $room){
		//TODO provjeri da $room ne sadrzi ';'
		$db = new SQLite3('ws.db', SQLITE3_OPEN_CREATE | SQLITE3_OPEN_READWRITE);
		$st = $db->prepare('SELECT id_sugovornik FROM sugovornik WHERE fingerprint=?');
		$st->bindValue(1, $callerFingerprint, SQLITE3_TEXT);
		$res = $st->execute();
		$re = $res->fetchArray();
		if(!isset($re['id_sugovornik'])){ /* sugovornik nije u bazi pa ga se dodaje */
			$st = $db->prepare('INSERT INTO sugovornik(fingerprint) VALUES (?)');
			$st->bindValue(1, $callerFingerprint, SQLITE3_TEXT);
			$st->execute();
			$st = $db->prepare('SELECT id_sugovornik FROM sugovornik WHERE fingerprint=?');
			$st->bindValue(1, $callerFingerprint, SQLITE3_TEXT);
			$res = $st->execute();
			$re = $res->fetchArray();
		}
		$id_sugovornik = $re['id_sugovornik'];
		$st = $db->prepare('SELECT id_soba FROM soba WHERE naziv=?');
		$st->bindValue(1, $room, SQLITE3_TEXT);
		$res = $st->execute();
		$re = $res->fetchArray();
		if(!isset($re['id_soba'])){ /* soba ne postoji u bazi pa je se dodaje */
			$st = $db->prepare('INSERT INTO soba(naziv) VALUES (?)');
			$st->bindValue(1, $room, SQLITE3_TEXT);
			$st->execute();
			$st = $db->prepare('SELECT id_soba FROM soba WHERE naziv=?');
			$st->bindValue(1, $room, SQLITE3_TEXT);
			$res = $st->execute();
			$re = $res->fetchArray();
		}
		$id_soba = $re['id_soba'];
		$st = $db->prepare("INSERT INTO aktivni_korisnik(id_soba, id_sugovornik, vrijeme) VALUES (?, ?, DateTime('now'))");
		$st->bindValue(1, $id_soba, SQLITE3_INTEGER);
		$st->bindValue(2, $id_sugovornik, SQLITE3_INTEGER);
		$st->execute();
		$st = $db->prepare("INSERT INTO ulazna_tocka(id_korisnik, ip_adresa, port) VALUES (?, ?, ?)");
		$st->bindValue(1, $id_sugovornik, SQLITE3_INTEGER);
		$ud = explode(';', $callerEndpoint, 2);
		$st->bindValue(2, $ud[0], SQLITE3_TEXT);
		$st->bindValue(3, $ud[1], SQLITE3_INTEGER);
		$st->execute();
		$db->close();
	}

	public function getUsers($callerEndpoint, $callerFingerprint, $roomlist){
		$db = new SQLite3('ws.db', SQLITE3_OPEN_CREATE | SQLITE3_OPEN_READWRITE);
		$st = $db->prepare('DROP TABLE IF EXISTS tmp_get_users');
		$st->execute();
		$st = $db->prepare('CREATE TEMPORARY TABLE tmp_get_users(soba TEXT)');
		$st->execute();
		foreach($roomlist as $i => $v){
			$st = $db->prepare('INSERT INTO tmp_get_users(soba) VALUES (?)');
			$st->bindValue(1, $v, SQLITE3_TEXT);
			$st->execute();
		}
		$st = $db->prepare('SELECT DISTINCT fingerprint FROM aktivni_korisnik '.
			'LEFT JOIN sugovornik ON aktivni_korisnik.id_sugovornik=sugovornik.id_sugovornik '.
			'LEFT JOIN soba ON aktivni_korisnik.id_soba=soba.id_soba '.
			'WHERE soba.naziv = (SELECT DISTINCT soba FROM tmp_get_users) '.
			'ORDER BY aktivni_korisnik.vrijeme DESC');
		$res = $st->execute();
		$r = '';
		while($re = $res->fetchArray(SQLITE3_ASSOC)){
			$r .= $re['fingerprint'].';';
		}
		$db->close();
		return $r;
	}

	public function getEndpoints($callerEndpoint, $callerFingerprint, $fingerprint){
		$db = new SQLite3('ws.db', SQLITE3_OPEN_CREATE | SQLITE3_OPEN_READWRITE);
		$st = $db->prepare("SELECT DISTINCT (ip_adresa || ':' || port) AS tocka FROM ulazna_tocka ".
			'LEFT JOIN aktivni_korisnik ON ulazna_tocka.id_korisnik=aktivni_korisnik.id_sugovornik '.
			'LEFT JOIN sugovornik ON sugovornik.id_sugovornik=aktivni_korisnik.id_sugovornik '.
			'WHERE sugovornik.fingerprint = ? '.
			'LIMIT 30'); // XXX limit
		//TODO u sklopu odrzavanja pobrisati stare endpointe
		$st->bindValue(1, $fingerprint, SQLITE3_TEXT);
		$res = $st->execute();
		$r = '';
		while($re = $res->fetchArray(SQLITE3_ASSOC)){
			$r .= $re['tocka'].';';
		}
		$db->close();
		return $r;
	}
}
?>
