<?php
require_once 'db_connection.php';
header("Content-Type: application/json; charset=UTF-8");

$id = $_GET['id'];

$response = [];

$all = "SELECT * FROM news WHERE `id`='$id'";
$executeAll = mysqli_query($conn, $all);
$row = mysqli_fetch_assoc($executeAll);

if ($row > 0) {
    $query = "DELETE FROM news WHERE `id` = '$id'"; // query to delete news
    $execute = mysqli_query($conn, $query); // execute query
    $response['status'] = true;
    $response['result'] = "Berita berhasil dihapus";
} else {
    $response['status'] = false;
    $response['result'] = "Gagal dihapus, berita tidak ditemukan";
}

$json = json_encode($response, JSON_PRETTY_PRINT);
echo $json;
