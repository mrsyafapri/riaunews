<?php
require_once 'db_connection.php';
header("Content-Type: application/json; charset=UTF-8");

$title = $_POST['title'];
$category = $_POST['category'];
$content = $_POST['content'];

$data = json_decode(file_get_contents("php://input"), true); // decode json data from client

$fileName  =  $_FILES['sendimage']['name']; // get file name from client
$tempPath  =  $_FILES['sendimage']['tmp_name']; // get temp path from client
$fileSize  =  $_FILES['sendimage']['size']; // get file size from client

if (empty($fileName)) {
    $errorMSG = json_encode(array(
        "status" => false,
        "message" => "Harap pilih gambar untuk cover berita"
    ), JSON_PRETTY_PRINT);
    echo $errorMSG;
} else {
    $image_path = 'images/news/'; // image directory path
    $fileExt = strtolower(pathinfo($fileName, PATHINFO_EXTENSION)); // get image extension
    $valid_extensions = array('jpeg', 'jpg', 'png'); // valid extensions
    if (in_array($fileExt, $valid_extensions)) {
        if (!file_exists($image_path . $fileName)) {
            if ($fileSize < 5000000) {
                move_uploaded_file($tempPath, $image_path . $fileName); // move uploaded image to images folder
            } else {
                $errorMSG = json_encode(array(
                    "status" => false,
                    "message" => "Maaf, ukuran file terlalu besar. Maksimal 5MB"
                ), JSON_PRETTY_PRINT);
                echo $errorMSG;
            }
        } else {
            $errorMSG = json_encode(array(
                "status" => false,
                "message" => "Maaf, file sudah ada"
            ), JSON_PRETTY_PRINT);
            echo $errorMSG;
        }
    } else {
        $errorMSG = json_encode(array(
            "status" => false,
            "message" => "Maaf, hanya file JPG, JPEG, dan PNG  yang diperbolehkan"
        ), JSON_PRETTY_PRINT);
        echo $errorMSG;
    }
}

if (!isset($errorMSG)) {
    $query = "INSERT INTO news (`title`,`category`,`content`,`cover`) VALUES ('$title','$category','$content','$fileName')"; // query to insert news
    $execute = mysqli_query($conn, $query); // execute query
    $json = json_encode(array(
        "status" => true,
        "message" => "Berita berhasil dibuat"
    ), JSON_PRETTY_PRINT);
    echo $json;
}
