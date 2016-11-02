Utvecklarinstruktioner

Dessa instrunktioner förutsätter att utvecklingsmiljön har access till Tak-API (coop) samt en instans av Elasticsearch som har konfigurering som Nationella Tjänsteplattfromens Elastcisearch instans.

Programvaror

Du behöver följande programvaror installerade för att kunna köra koden i "utvecklarläge", samt för att paketera releaser:

    Java Development Kit (version 1.7+) - behöver vara installerad och miljövariabeln JAVA_HOME satt till JDK-installationens rotkatalog (ett steg ovanför javas bin-katalog)
    Maven (version 3+) - behöver vara uppackas och dess bin-katalog finnas med i PATH

Konfigurera utvecklingsmiljön
Klona Git-repository

Klona detta git-repository via en Git-klient, alternativt ladda ned källkoden som en zip.
Bygg med Maven

mvn clean install

Om man har problem att bygga på grunda av PermGen sätta MAVEN_OPTS set MAVEN_OPTS=-Xmx512m -XX:MaxPermSize=128m

Permanentlösning: skapa fil mavenrc_pre.bat under %HOME% lägga MAVEN_OPTS

Skapa en katalog för statistik konfigurationsfiler någonstans, och peka ut sökvägen via miljövariabeln statistikapi_config_dir.

Skapa inställningsfil:
statistikapi-config-override.properties
filen skall innehålla följande information:
cluster.name=<elasticsearch-clustername>
elasticsearch-server=<elasticsearch-server>
elasticsearch-port=<elasticsearch-port>
tak-api-server=<tak-api-server>
platform=<platform-name-for-connectionpoint-used-in-tak-api>
environment=<environment-name-for-connectionpoint-used-in-tak-api>

Nu kan du bygga en war-fil med följande kommando:

mvn package

war-filen skapas i katalogen statistik-api/target/

Checka in ändringar

Om allt ser bra ut:

    committa dina ändringar till Git,
    skapa en tag med samma namn som versionsnumret
    pusha alltsammans till GitHub (dubbelkolla att taggen syns på GitHub).
