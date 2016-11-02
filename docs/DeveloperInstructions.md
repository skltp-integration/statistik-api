Utvecklarinstruktioner

Dessa instrunktioner f�ruts�tter att utvecklingsmilj�n har access till Tak-API (coop) samt en instans av Elasticsearch som har konfigurering som Nationella Tj�nsteplattfromens Elastcisearch instans.

Programvaror

Du beh�ver f�ljande programvaror installerade f�r att kunna k�ra koden i "utvecklarl�ge", samt f�r att paketera releaser:

    Java Development Kit (version 1.7+) - beh�ver vara installerad och milj�variabeln JAVA_HOME satt till JDK-installationens rotkatalog (ett steg ovanf�r javas bin-katalog)
    Maven (version 3+) - beh�ver vara uppackas och dess bin-katalog finnas med i PATH

Konfigurera utvecklingsmilj�n
Klona Git-repository

Klona detta git-repository via en Git-klient, alternativt ladda ned k�llkoden som en zip.
Bygg med Maven

mvn clean install

Om man har problem att bygga p� grunda av PermGen s�tta MAVEN_OPTS set MAVEN_OPTS=-Xmx512m -XX:MaxPermSize=128m

Permanentl�sning: skapa fil mavenrc_pre.bat under %HOME% l�gga MAVEN_OPTS

Skapa en katalog f�r statistik konfigurationsfiler n�gonstans, och peka ut s�kv�gen via milj�variabeln statistikapi_config_dir.

Skapa inst�llningsfil:
statistikapi-config-override.properties
filen skall inneh�lla f�ljande information:
cluster.name=<elasticsearch-clustername>
elasticsearch-server=<elasticsearch-server>
elasticsearch-port=<elasticsearch-port>
tak-api-server=<tak-api-server>
platform=<platform-name-for-connectionpoint-used-in-tak-api>
environment=<environment-name-for-connectionpoint-used-in-tak-api>

Nu kan du bygga en war-fil med f�ljande kommando:

mvn package

war-filen skapas i katalogen statistik-api/target/

Checka in �ndringar

Om allt ser bra ut:

    committa dina �ndringar till Git,
    skapa en tag med samma namn som versionsnumret
    pusha alltsammans till GitHub (dubbelkolla att taggen syns p� GitHub).
