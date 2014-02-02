<?php
$db = new SQLite3('ws.db', SQLITE3_OPEN_CREATE | SQLITE3_OPEN_READWRITE);

$db->exec('DROP TABLE IF EXISTS soba');
$db->exec('DROP TABLE IF EXISTS sugovornik');
$db->exec('DROP TABLE IF EXISTS ulazna_tocka');
$db->exec('DROP TABLE IF EXISTS aktivni_korisnik');

echo 'Baza unistena';

?>
