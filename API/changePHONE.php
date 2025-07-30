<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $conn = new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

    $id = $_POST['id'];
    $phone = htmlspecialchars($_POST['phone']);

    if(isset($phone)){
        $req = $conn->query('SELECT phone FROM liste WHERE id = '.$id);
        $donnees = $req->fetch();
        if($donnees['phone'] == "aucun")
            $conn->exec('UPDATE liste SET phone = \''.$phone.'\' WHERE id = '.$id );  
        else
            $conn->exec('UPDATE liste SET phone = \''.$donnees['phone'].' / '.$phone.'\' WHERE id = '.$id );
    }
}
else{
    http_response_code(405);
    echo json_encode(["message" => "méthode invalide"]);
}


?>