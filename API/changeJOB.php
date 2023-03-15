<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $conn = new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

    $type = htmlspecialchars($_POST['type']);
    $img = $_POST['image'];
    $id = $_POST['id'];

    if($type == 'Etudiant'){
        $req = $conn->query('SELECT * FROM liste WHERE id = '.$id);
        $donnees = $req->fetch();
        if($donnees['sexe'] == "Féminin")
            $type = 'Etudiante';
    }
    $sql = 'UPDATE liste SET asa = \''.$type.'\'';
    if(isset($_POST['image'])){
        $sql = $sql.', img = \''.$img.'\'';
    }
    $sql = $sql.' WHERE id = '.$id;

    $conn->exec($sql); 
}
else{
    http_response_code(405);
    echo json_encode(["message" => "méthode invalide"]);
}

?>