<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: DELETE");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == "DELETE"){
    $bdd= new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');
    $id = htmlspecialchars($_GET['id']);

    $bdd->exec('DELETE FROM liste WHERE id = '.$id);

    include_once '../cellulars/clear.php';
    include_once '../finances/clear.php';
    include_once '../universities/clear.php';
    include_once '../healths/clear.php';

    


}
else{
    http_response_code(405);
    echo json_encode(["message" => "méthode invalide"]);
}

?>
