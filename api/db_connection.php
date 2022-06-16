<?php
$host = "localhost"; // Host name
$username = "root"; // Mysql username
$password = ""; // Mysql password
$db = "riaunews"; // Database name

$conn = mysqli_connect($host, $username, $password, $db); // create connection

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}
