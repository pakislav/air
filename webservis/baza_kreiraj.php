<?php
/*
Inicijaliziraj bazu za servis
*/

// TODO integritet
$db = new SQLite3('ws.db', SQLITE3_OPEN_CREATE | SQLITE3_OPEN_READWRITE);
$db->exec('CREATE TABLE soba(id_soba INTEGER PRIMARY KEY AUTOINCREMENT, naziv TEXT)');
$db->exec('CREATE TABLE sugovornik(id_sugovornik INTEGER PRIMARY KEY AUTOINCREMENT, fingerprint TEXT)');
$db->exec('CREATE TABLE ulazna_tocka(id_ulazna_tocka INTEGER PRIMARY KEY AUTOINCREMENT, id_korisnik INT, ip_adresa TEXT, port INT)');
$db->exec('CREATE TABLE aktivni_korisnik(id_aktivni_korisnik INTEGER PRIMARY KEY AUTOINCREMENT, id_soba INT, id_sugovornik INT, vrijeme TEXT)');

echo 'Baza kreirana.';
?>
