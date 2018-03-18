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

## II - La partie Client

Le client POP3 avait pour but de permettre à un utilisateur de se connecter au serveur POP3 (en renseignant l'adresse de ce dernier et le port sur lequel il voulait se connecter), de relever ses mails et de pouvoir les afficher.
Il n'était pas nécessaire de permettre à celui-ci d'envoyer des nouveaux messages ou de lui laisser la possibilité de supprimer ses messages.

### 1 - Automate

### 2 - Développement

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


