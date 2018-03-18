# Rapport IPC

**Etudiants :**
*  JACOUD Bastien
*  REMOND Victor
*  TAGUEJOU Christian
*  TARDY Martial

**Projet GIT**
* [Lien GitHub](https://github.com/Elomidas/POP3)

## I - Introduction

L'objectif de ce TP était dans un premier temps de réaliser un client et un serveur POP3 respectant la norme [RFC 1939](https://www.ietf.org/rfc/rfc1939.txt).

Pour mener à bien ce projet, nous avons commencé par réfléchir aux schémas des automates découlant de cette norme, avant de nous lancer dans le développement des deux parties du projet en java.

Une fois notre couple Client - Serveur fonctionnel avec le protocole POP3, nous l'avons modifié afin qu'il utilise le protocole POP3S, en remplaçant les commandes *USER* et *PASS* par une unique commande *APOP* et l'utilisation d'un timbre à date.

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

### 1 - Automate

### 2 - Développement

### 3 - Partie Graphique

L'interface graphique du client à été réalisé grâce à la bibliothèque graphique JavaFX. Les différents composants de chacune des fenêtres ont été créés grâce à SceneBuilder et sont stockées dans les fichiers FXML correspondants. 

Les vérifications sont effectuées à la fois côté client et côté serveur, dans la mesure du possible. Par exemple, l'utilisateur ne peut accéder au bouton de connexion que si les différents champs ont été correctement remplis. Pour cela, nous utilisons différentes Regex pour vérifier si une adresse mail ou une adresse IP est correcte. Il s'agit des vérifications effectuées côté client. De l'autre côté nous traitons les réponses renvoyés par le serveur pour faire remonter les erreurs sous forme d'exceptions. Lorsqu'une exception est levée, une alertbox est affichée à l'écran de l'utilisateur pour lui indiquer l'erreur qui a eu lieu. 
D'autres alertbox peuvent également suivenir pour demander confirmartion à l'utilisateur, par exemple lors de la suppression d'un message. 

Tout ceci va donc rendre plus agréable l'utilisation du logiciel par l'utilisateur. 

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


