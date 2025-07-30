<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers:, Authorization, X-Requested-Width");

if($_SERVER['REQUEST_METHOD'] == 'GET'){
    $bdd= new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

    $req = $bdd->query('SELECT * FROM liste');
        $tableau = [];
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
            $tableau [] = $prod;
        }
        
        echo json_encode($tableau);

}
else{
    http_response_code(405);
    echo json_encode(["message" => "méthode invalide"]);
}

?>


create table liste ( id int  PRIMARY KEY, anarana varchar(255), fanampiny varchar(255), sexe varchar(255), daty varchar(255), adiresy varchar(255), phone varchar(255), asa varchar(255), cin varchar(255), img blob);
CREATE TABLE liste (
    id INT AUTO_INCREMENT PRIMARY KEY,
    anarana VARCHAR(255),
    fanampiny VARCHAR(255),
    sexe VARCHAR(255),
    daty VARCHAR(255),
    adiresy VARCHAR(255),
    phone VARCHAR(255),
    asa VARCHAR(255),
    cin VARCHAR(255),
    img LONGBLOB
);

GRANT ALL PRIVILEGES ON etatCivil TO 'root'@'localhost' IDENTIFIED BY 'PASSWORD'

GRANT ALL PRIVILEGES ON etatCivil.* TO 'root'@'localhost' IDENTIFIED BY 'PASSWORD'

ALTER USER 'root'@'localhost' IDENTIFIED BY 'PASSWORD';




create table ID (id SERIAL PRIMARY KEY, lastID SERIAL);


pip install Flask-SQLAlchemy Flask-Migrate Flask-WTF Flask-Login

