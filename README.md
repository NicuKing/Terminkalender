<h3>Beschreibung</h2>
Dies ist ein Projekt, welches ich über 2 Wochen mit Java gemacht habe. <br>
Es ist ein Client/Server Programm, mit dem man eine Liste von Terminen <br>
verwalten kann. Der Server ist mit einer Datenbank verbunden, welche alle Benutzer <br>
und Termine gespeichert hat. <br> <br>
Momentan  ist das Projekt soweit, dass man mit dem Server einen Port öffnen kann und <br>
dieser dann im Hintergrund läuft. Der Client kann dann eine Port-Nummer(Diese des Server) <br>
und eine IP-Adresse, oder auch localhost, eingeben.

<h3>Aufsetzen</h3>
Um das Projekt zu starten muss man zuerst XAMPP herunterladen: https://www.apachefriends.org/download.html <br>
Danach muss man das XAMPP-Control Panel starten und bei den Zeilen Apache, MySQL Start klicken. <br>
Sobald man die Dienste gestartet hat, kann man die Datenbank importieren. Dies macht man indem <br>
man auf den Browser geht und localhost/phpmyadmin in der URL eingibt. <br>
Nun ist man auf dem GUI des Datenbankservice. man muss nun oben auf "Import" klicken, wo man dann <br>
auf "Datei auswählen" klickt und die Datei calendar.sql im JavaProject Ordner auswählt und auf "Go" klickt.<br>
Nun ist die Datenbank aufgesetzt. <br>
Jetzt kann man in der Umgebung die man will(Ich brauche IntelliJ) im "src"-Ordner im Package "Server" die Klasse<br>
MainServer ausführen. Danach kann man im Package "Client" die Klasse MainClient ausführen.

<h3>Erweiterung</h3>
Das Projekt kann noch erweitert werden durch:<br>
- Der Server erkennt, welcher Client die Termine erstellt hat, und man kann dann eine Zugriffsliste <br>
für die Termine machen, sodass nicht jede Person alle Termine hat und man mehr übersicht hat.
- Man kann eine Farbe den Terminen hinzufügen um diese zu klassifizieren.
- Man kann das GUI noch besser designen, da ich bei diesem Projekt hauptsächlich auf das <br>
Backend geachtet habe.<br>