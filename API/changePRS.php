<?php

$conn = new PDO('mysql:host=localhost;dbname=etatCivil', 'root', 'toor');

$anarana = htmlspecialchars($_POST['anarana']);
$fanampiny =  htmlspecialchars($_POST['fanampiny']);
$sexe = htmlspecialchars($_POST['sexe']);
$daty = htmlspecialchars($_POST['daty']);
$adiresy = htmlspecialchars($_POST['adiresy']);
$cin = htmlspecialchars($_POST['cin']);
$asa = htmlspecialchars($_POST['asa']);
$img = $_POST['img'];
$id = $_POST['id'];

$conn->exec('UPDATE liste SET anarana = \''.$anarana.'\',fanampiny = \''.$fanampiny.'\', sexe = \''.$sexe.'\', daty = \''.$daty.'\', adiresy = \''.$adiresy.'\',  asa = \''.$asa.'\', cin= \''.$cin.'\', img = \''.$img.'\' WHERE id = '.$id );
echo "modification éffectuée";
?>