<?php
require_once 'db_connection.php';
header("Content-Type: application/json; charset=UTF-8");

$query = "SELECT * FROM news ORDER BY created_at DESC"; // query to get news
$execute = mysqli_query($conn, $query); // execute query
$response = [];

if (mysqli_num_rows($execute) > 0) {
    while ($row = mysqli_fetch_object($execute)) {
        $response['status'] = true;
        $response['result'][] = $row;
    }
} else {
    $response['status'] = false;
    $response['result'][] = [
        'id' => null,
        'title' => null,
        'category' => null,
        'content' => null,
        'cover' => null,
        'created_at' => null,
        'updated_at' => null
    ];
}

$json = json_encode($response, JSON_PRETTY_PRINT);
echo $json;
