<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $conn = new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

    $anarana = htmlspecialchars($_POST['anarana']);
    $fanampiny =  htmlspecialchars($_POST['fanampiny']);
    $sexe = htmlspecialchars($_POST['sexe']);
    $daty = htmlspecialchars($_POST['daty']);
    $adiresy = htmlspecialchars($_POST['adiresy']);
    $img = $_POST['img'];
    $asa = htmlspecialchars($_POST['asa']);
    $cin = htmlspecialchars($_POST['cin']);
    
    
    $conn->exec('INSERT INTO liste (anarana, fanampiny, sexe, daty, adiresy, phone, asa, cin, img) VALUES (\''.$anarana.'\', \''.$fanampiny.'\', \''.$sexe.'\', \''.$daty.'\', \''.$adiresy.'\', \'aucun\', \''.$asa.'\', \''.$cin.'\', \''.$img.'\')');
    $req = $conn->query('SELECT * FROM ID WHERE id = 1');
    if($donnees = $req->fetch())    {
        $last = (int) ($donnees['lastID']) + 1;
        $conn->exec('UPDATE ID set lastID ='.$last.' WHERE id = 1');
    }
    else{
        $conn->exec('INSERT INTO ID (lastID) VALUES (1)');
    }
    echo "insertion éffectuée"; 
}
else{
    echo "méthode invalide";
    http_response_code(405);   
}
?>



