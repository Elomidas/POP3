# Rapport IPC

**Etudiants :**
*  JACOUD Bastien
*  REMOND Victor
*  TAGUEJOU Christian
*  TARDY Martial

**Projet GIT**
* [Lien GitHub](https://github.com/Elomidas/POP3)
* [Version POP3 stable](https://github.com/Elomidas/POP3/releases/tag/POP3-OK)

## I - Introduction
[utilities]: https://github.com/Elomidas/POP3/tree/master/Client/src/Utilities
[POP3]: https://github.com/Elomidas/POP3/blob/master/Client/src/Model/Protocols/POP3/POP3.java
[POP3S]: https://github.com/Elomidas/POP3/blob/master/Client/src/Model/Protocols/POP3/POP3S.java
[TCP]: https://github.com/Elomidas/POP3/blob/master/Client/src/Model/Protocols/TCP/TCP.java
[RFC1939]: https://www.ietf.org/rfc/rfc1939.txt

L'objectif de ce TP était dans un premier temps de réaliser un client et un serveur POP3 respectant la norme [RFC 1939][RFC1939].

Pour mener à bien ce projet, nous avons commencé par réfléchir aux schémas des automates découlant de cette norme, avant de nous lancer dans le développement des deux 
parties du projet en java.

Une fois notre couple Client - Serveur fonctionnel avec le protocole POP3, nous l'avons modifié afin qu'il utilise le protocole POP3S, en remplaçant les commandes *USER* 
et *PASS* par une unique commande *APOP* et l'utilisation d'un timbre à date.
## II - Notice d'utilisation

Afin de pouvoir utiliser la messagerie correctement, l'utilisateur doit tout d'abord passer par une phase d'authentification. Lors de cette authentification, il doit notamment spécifier l'adresse IP et le port du serveur, mais aussi son adresse mail et son mot de passe. 

![alt text](https://github.com/Elomidas/POP3/blob/master/images/fen%C3%AAtre_connexion_num.jpg "Fenêtre de connexion")

Sur la capture d'écran ci-dessus, nous observons bien que l'utilisateur doit mentionner l'adresse IP de la machine serveur(**3**) et le port sur lequel le programme est exécuté(**4**). 
Il doit également inscrire son adresse mail(**1**) ainsi que son mot de passe(**2**). Lors du renseignement du mot de passe, ce dernier n'apparaît pas en clair sur la fenêtre d'affichage, visible par l'utilisateur. 

Une fois la phase d'identification et d'authentification achevée, une nouvelle fenêtre s'affiche alors à l'écran. Il s'agit de la messagerie du client, dont voici une capture d'écran :

![alt text](https://github.com/Elomidas/POP3/blob/master/images/POP3_num.png "Client POP3")

L'adresse mail de l'utilisateur actuellement connecté(**1**), ainsi qu'un bouton lui permettant de se déconnecter(**2**) apparaissent directement sur la fenêtre du client. Il est important de noter qu'il existe deux moyens pour l'utilisateur de se déconnecter : il peut cliquer sur le bouton déconnexion ou alors directement fermer la fenêtre via un clic sur la croix rouge. Dans chacun de ces cas, le client envoie la commande "QUIT" au serveur, qui va se charger de la suppression des messages marqués. 

Une liste, avec pagination automatique, de tous les mails réceptionnés(**3**) est dsponible sur la partie gauche de la fenêtre. Chaque numéro de mail(**8**) correspond à un lien et permet d'afficher directement via un clic sur ce dernier son contenu dans la partie droite de la fenetre(**7**).

Le bouton Actualiser(**4**) permet de réafficher tous les messages réceptionnés, y compris les messages venant tout juste d'être reçus. 
Le bouton Répondre(**5**) nous rediriger automatiquement vers l'onglet Envoi de mail et pré-rempli tous les champs nécessaires à la réponse. 
Le bouton Supprimer(**6**) nous permet de supprimer un mail. Ce dernier apparaît alors en rouge tant que l'utilisateur reste connecté et sera supprimé à la déconnexion du client. 

Il est important de noter que la partie envoi de message n'est pas présentée dans ce compte rendu car son implémentation fait l'objet du prochain tp. 

## II - La partie Client

Le client POP3 avait pour but de permettre à un utilisateur de se connecter au serveur POP3 (en renseignant l'adresse de ce dernier et le port sur lequel il voulait se connecter), de relever ses mails et de pouvoir les afficher.
Il n'était pas nécessaire de permettre à celui-ci d'envoyer des nouveaux messages ou de lui laisser la possibilité de supprimer ses messages.

### 1 - Algorithme

Ci-dessous le code Java basique d'un main de client POP3

```java
public static void main(String[] args) {
  //Création d'un objet client POP3
  POP3 client = new POP3();
  boolean connected = false;
  while(connected == false) {
    //Connexion du client au serveur, via son adresse IP et le numéro de port souhaité
    client.joinServer("192.168.43.18", 1210);
    //Authentification de l'utilisateur
    connected = client.authenticate("vremond@email.com", "Else");
  }
  //Passage à la boucle principale du client POP3
  client.transactions();
  //Fermeture du client
  client.close();
  //Fin de l'execution
}
```

Ainsi que le code Java basique de la gestion des transactions par le client

```java
class POP3 {
  public POP3() {
    /*  Initialisation du client
     */
  }
  
  protected String getFromUser() {
    /*  Récupère la commande à executer via l'interface utilisateur
     *  Varie selon l'interface homme-machine utilisée.
     */
     return command;
  }
  
  /* Autres fonctions */
  
  public transactions() {
    String command;
    boolean quit = false;
    while(quit == false) {
      command = this.getFromUser();
      //Récupération de chaque mot de la commande individuellement
      String[] splitCommand = command.split(" ");
      //Analyse du premier mot, forcé en minuscule pour ignorer la casse
      switch(splitCommand[0]) {
        case "quit":
          this.quit();
          quit = true;
          break;
        case "stat":
          this.stat();
          break;
        case "list":
          this.list();
          break;
        case "dele":
          //On n'execute la commande que si un argument a été passé en paramètre en entrée.
          //La validité de cet argument sera quant à elle vérifiée dans la fonction.
          if(splitCommand.length > 1) {
            this.dele(splitCommand[1]);
          }
          break;
        case "retr":
          //On n'execute la commande que si un argument a été passé en paramètre en entrée.
          //La validité de cet argument sera quant à elle vérifiée dans la fonction.
          if(splitCommand.length > 1) {
            this.retr(splitCommand[1]);
          }
          break;
        case "noop":
          this.noop();
          break;
        case "rset" :
          this.rset();
          break;
        default:
          break;
      }
    }
  }
}
```

On notera que dans la fonction ci-dessus, les codes d'actualisation des informations visibles par l'utilisateur ne sont pas détaillés étant données qu'ils sont totalement 
différents selon le style d'affichage utilisé (invité de commandes ou interface graphique). De plus ils n'apporteraient pas forcement d'informations utiles pour comprendre 
le fonctionnement du protocole.

### 2 - Développement
Dans la phase de développement, nous avons fait le choix de créer une classe gérant le protocole TCP afin d'effectuer la connexion avec le serveur. Cette [classe TCP][TCP] 
est elle même utilisée par notre [classe POP3][POP3], ainsi lorsque la fonction de connexion de POP3 est appelée, la classe fait elle même appel à TCP.

De même, pour l'envoi de commane, le client POP3 utilise la fonction *send* définie dans la [classe TCP][TCP] et la fonction *receive* pour récupérer les réponses envoyées par 
le serveur.

D'autres fonctions définies dans la [classe POP3][POP3] permettent d'envoyer une commande au serveur et d'attendre la réponse, voire même de tester si celle-ci est positive 
avant de la retourner.

Pour effectuer divers vérifications, nous avons créé un [package utilities][utilities] contenant une classe définissant diverses méthodes statiques permettant de faire des tests 
via des expressions régulères, telles que vérifier qu'une réponse du serveur commance bien par *+OK* ou encore découper la chaine de caractères qui représente le mail afin de ne 
récupérer que les informations qui nous intéressent (*expéditeur*, *objet*, *message*, etc...).

En ce qui concerne la transition du client de POP3 vers POP3S, elle a été extrêmement facile à opérer. En effet nous avons juste eu à créer une [classe POP3S][POP3S] héritant 
de la [classe POP3][POP3] et redifinissant la fonction d'authentification. Ainsi nous avons eu un client POP3S fonctionnel en récupérant la quasi totalité du code déjà mis en 
place précédemment, les seules modifications à apporter étant la récupération d'un timbre à date (extrait de la réponse du serveur grâce à une des fonctions du 
[package utilities][utilities] évoqué ci-dessus) et l'envoi d'une commande *APOP*, encryptée en MD5 grâce au timbre à date récupéré pécédemment, à la place des commandes *USER* et 
*PASS* utilisées par le protocole POP3 classique.

### 3 - Partie Graphique


## III - La partie Serveur

La partie Serveur a été faite en deux étapes. Dans un premier temps, nous avons géré la connexion
de l'utilisateur par le protocole TCP, puis la communication entre le client et le serveur grâce aux
commandes POP3.

### 1 - Connexion TCP
Avant qu'un utilisateur n'essaye de se connecter, on lance le serveur sur un port choisi. Pour 
cela, on instancie la classe SocketServer. Cet objet prendra en charge la transimission des données.
Nous avons défini une boucle sans fin pour que le serveur puisse accepter toutes les connexions
tant que celles-ci se font sur le bon port.

Lorsqu'un client se connecte sur le bon port, la méthode accept() de la classe SocketServer va
retourner un objet Socket représentant la connexion du client. Pour pouvoir traiter plusieurs
connexion à la fois, un thread va être créé à partir de cet objet. Le serveur et le client pourront par la suite s'échanger des données 
grâce aux méthodes receive() et send() qui utilisent respectivement des objets BufferedInputStream et PrintStream.

### 2 - POP3


