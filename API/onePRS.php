<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");


if($_SERVER['REQUEST_METHOD'] == 'GET'){
    $bdd= new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');
    $id = htmlspecialchars($_GET['id']);
    
    $req = $bdd->query('SELECT * FROM liste WHERE id = '.$id);
        
    while($row = $req->fetch(PDO::FETCH_ASSOC)){
        extract($row);
        $prod = [
            "id" => $id,
            "anarana" => $anarana,
            "fanampiny" => $fanampiny,
            "sexe" => $sexe,
            "daty" => $daty,
            "adiresy" => $adiresy,
            "phone" => $phone,
            "asa" => $asa,
            "cin" => $cin,
            "image" => $img
        ];
        echo json_encode($prod);
    }

}
else{
    http_response_code(405);
    echo json_encode(["message" => "méthode invalide"]);
}


?>