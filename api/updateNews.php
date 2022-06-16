<?php
require_once 'db_connection.php';
header("Content-Type: application/json; charset=UTF-8");

$id = $_GET['id'];
$title = $_POST['title'];
$category = $_POST['category'];
$content = $_POST['content'];

$query = "UPDATE news SET `title`='$title', `category`='$category', `content`='$content' WHERE `id`='$id'"; // query to update news
$execute = mysqli_query($conn, $query); // execute query
$row = mysqli_affected_rows($conn);

if ($row > 0) {
    $json = json_encode(array(
        "status" => true,
        "message" => "Berita berhasil diedit"
    ), JSON_PRETTY_PRINT);
} else {
    $json = json_encode(array(
        "status" => false,
        "message" => "Berita gagal diedit"
    ), JSON_PRETTY_PRINT);
}
echo $json;
