<?php
/*
Otvori funkcionalnost servisa preko HTTP GET
*/
//header('Access-Control-Allow-Origin: *'); /* https://en.wikipedia.org/wiki/Cross-origin_resource_sharing */

// http://localhost/~student/air/get-service.php?c=joinRoom&ip=192.168.0.2&port=9001&f=AAAAAAAAAAAAAAAAAAAA&r=default
if(isset($_GET['c']) && $_GET['c'] == 'joinRoom'){
	include 'ws.php';
	$ws = new Ws();
	//$callerEndpoint = $_SERVER['REMOTE_ADDR'].';'.$_SERVER['REMOTE_PORT'];
	$callerEndpoint = $_GET['ip'].';'.$_GET['port']; // XXX
	$callerFingerprint = $_GET['f']; // XXX
	$room = $_GET['r']; // XXX
	$ws->joinRoom($callerEndpoint, $callerFingerprint, $room);
}

// http://localhost/~student/air/get-service.php?c=getUsers&ip=192.168.0.2&port=9001&f=AAAAAAAAAAAAAAAAAAAA&r=default
if(isset($_GET['c']) && $_GET['c'] == 'getUsers'){
	include 'ws.php';
	$ws = new Ws();
	//$callerEndpoint = $_SERVER['REMOTE_ADDR'].';'.$_SERVER['REMOTE_PORT'];
	$callerEndpoint = $_GET['ip'].';'.$_GET['port']; // XXX
	$callerFingerprint = $_GET['f']; //XXX
	$rooms = $_GET['r']; // XXX
	$roomlist = explode(';', $rooms, 10); //XXX
	$r = $ws->getUsers($callerEndpoint, $callerFingerprint, $roomlist);
	header('Content-type: application/json');
	echo json_encode(array('getUsers'=>$r));
	//echo $r;
}

// http://localhost/~student/air/get-service.php?c=getEndpoints&ip=192.168.0.3&port=9001&f=BBBBBBBBBBBBBBBBBBB&s=AAAAAAAAAAAAAAAAAAAA
if(isset($_GET['c']) && $_GET['c'] == 'getEndpoints'){
	include 'ws.php';
	$ws = new Ws();
	//$callerEndpoint = $_SERVER['REMOTE_ADDR'].';'.$_SERVER['REMOTE_PORT'];
	$callerEndpoint = $_GET['ip'].';'.$_GET['port']; // XXX
	$callerFingerprint = $_GET['f']; //XXX
	$fingerprint = $_GET['s']; //XXX
	$r = $ws->getEndpoints($callerEndpoint, $callerFingerprint, $fingerprint);
	header('Content-type: application/json');
	echo json_encode(array('getEndpoints'=>$r));
	//echo $r;
}
?>
