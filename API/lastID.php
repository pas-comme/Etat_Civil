<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == 'GET'){

    $bdd= new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

    $req = $bdd->query('SELECT * FROM ID WHERE id = 1');
    if($donnees = $req->fetch())
    {
        echo $donnees['lastID'];
    }
    else{
        echo "0";
    }
}
else{
    echo "méthode invalide";
    http_response_code(405);
}

?>
