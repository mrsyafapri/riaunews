<?php
require_once 'db_connection.php';
header("Content-Type: application/json; charset=UTF-8");

$username = $_POST['username'];
$password = $_POST['password'];

$query = "SELECT * FROM users WHERE `username`='$username' AND `password`='$password'"; // query to get user
$execute = mysqli_query($conn, $query); // execute query
$response = [];
$row = mysqli_fetch_assoc($execute); // get row

if ($row > 0) {
    $response['status'] = true;
    $response['message'] = 'Login Berhasil';
    $response['data'] = $row;
} else {
    $response['status'] = false;
    $response['message'] = 'Username atau Password Salah';
    $response['data'] = $row;
}

$json = json_encode($response, JSON_PRETTY_PRINT);
echo $json;
