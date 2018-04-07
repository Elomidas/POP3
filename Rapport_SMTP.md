# Rapport IPC - Partie SMTP <a name="" />

[RFC]: https://tools.ietf.org/html/rfc5321
[GitHub]: https://github.com/Elomidas/POP3

**Etudiants :**
*  JACOUD Bastien
*  REMOND Victor
*  TAGUEJOU Christian
*  TARDY Martial

**Code du Projet :** [lien GitHub][GitHub]

## Table des matières

  * I - [Introduction](#I)
  * II - [Client](#II)
    * 1 - [Automate](#II1)
    * 2 - [Backend](#II2)
      * A - [SMTP Basique](#II2A)
      * B - [Fonctionnement avec plusieurs noms de domaines](#II2B)
      * C - [Optimisation](#II2C)
    * 3 - [Frontend](#II3)
      * A - [Présentation interface graphique](#II3A)
      * B - [Gestion de plusieurs adresses mail valides ou non](#II3B)
  * III - [Serveur](#III)
  * IV - [Conclusion](#IV)

## I - Introduction<a name="I" />

Suite du TP de développement d'un couple client/serveur mail.
Cette étape fut la troisième de cette série de TP.
Durant la première étape, nous avons dû mettre en place le protocole POP3 pour que le client relève ses mails sur le serveur.
La seconde consistait à adapter le code pour utiliser le protocole POP3S afin de sécuriser la connexion du client.
Le but de cette dernière étape était de permettre au client d'envoyer des messages à d'autres utilisateurs en permettant au client comme au serveur d'utiliser le protocole SMTP.
Lien vers la [norme RFC utilisée][RFC].

## II - Client <a name="II" />

### 1 - Automate <a name="II1" />

### 2 - Backend <a name="II2" />

#### A - SMTP Basique <a name="II2A" />

Dans un premier temps, nous avions un unic domaine à gérer *email.com*.
Ainsi l'implémentation du protocole était relativement simple, il suffisait nous de connaitre l'adresse du serveur, or celle-ci était déjà renseignée pour le fonctionnement des protocoles POP3 et POP3S. 
Nous n'avions donc pas besoin d'autre nouvelle information que le port de connexion de SMTP sur le serveur.

Afin de permettre à l'utilisateur d'envoyer son message à plusieurs utilisateurs en même temps, nous lui donnons la possibilité de mettre plusieurs destainataires, séparés par des points-virgules.
Deux fonctions de la classe ```String``` de java ont rendu cette fonctionnalité facile à implémenter :
*  ```String::split(";")``` nous permet de découper une chaine de caractères à chaque occurence du caractère ```";"```.
*  ```String::trim()``` permet quant à elle de supprimer les espaces en début et fin de chaine de caractères, utile pour avoir une adresse correcte pour le destinnataire, peu importe que l'utilisateur ait décidé de séparer les différentes adresses avec ```";"```, ```"; "``` ou ```" ; "```.

#### B - Fonctionnement avec plusieurs noms de domaines <a name="II2B" />

Nous avons ensuite dû faire fonctionner le client pour qu'il puisse gérer plusieurs noms de domaine (**email.com** et **email.fr**), correspondant à deux serveurs différents.
Afin de savoir sur quelle adresse IP et sur quel port envoyer le message selon l'adresse du destinataire, nous avons créé une classe ```DNS``` permettant de simuler le fonctionnement d'un serveur DNS classique : récupérer l'adresse IP d'un serveur en fonction de son nom de domaine.

Notre classe ```DNS``` se compose d'une liste de ```ServerIntels```, une classe contenant toutes les informations utiles à propos d'un serveur.
Cette liste est déclarée comme ci-dessous, elle doit être mise à jour après le lancement des serveurs.
Les différentes fonctions *public* de cette classe permettent de récupérer les informations qui nous intéressent sur le serveur voulu grâce à son nom de domaine.
```java
public class DNS {
	private static List<ServerIntels> servers = Arrays.asList(
			new ServerIntels(
					"email.com",	//Nom de domaine
					"127.0.0.1",	//Adresse IP
					1210,			//Port POP3
					1211,			//Port POP3S
					1212),			//Port SMTP
			new ServerIntels(
					"email.fr",
					"127.0.0.1",
					1213,
					1214,
					1215)
		);
	
	public static String getAddress(String domain) throws DNSException {
		/* Code */
	}

	public static int getPOP3(String domain) throws DNSException {
		/* Code */
	}

	public static int getPOP3S(String domain) throws DNSException {
		/* Code */
	}

	public static int getSMTP(String domain) throws DNSException {
		/* Code */
	}

	public static int getServersNumber() {
		/* Code */
	}

	public static List<String> getDomains() {
		/* Code */
	}

}
```
Ces informations sont en dur dans le code, mais nous ne considérons pas cela comme vraiment génant car dans la vraie vie les coordonées d'un serveur ne sont pas amenées à changer aussi fréquemment.
Si on veut lancer le programme avec un troisième serveur, il suffirait d'ajouter une troisième objet ```ServerIntels``` à la liste ci-dessus.

#### C - Optimisation <a name="II2C" />

Avec notre premier développement, nous avions créé un unique objet SMTP, qui, pour chaque destinataire, ouvrait une connexion TCP pour envoyer le message et si le destinataire suivant nécessitait la connexion à un autre serveur, il fermait celle précedemment ouverte avant d'en ouvrir une autre sur le nouveau serveur.
Étant donné que ceci n'était pas du tout optimisé, nous avons choisi de créer un objet SMTP pour chaque serveur renseigné dans la classe ```DNS```.
Tous ces objets ```SMTP``` sont manipulés grâce à la classe ```SMTPDispatcher``` et stockés dans une ```HashMap<String, SMTP>``` utilisant le nom de domaine comme clé.
Ces objets ne gardent pas les connexions TCP toujours ouvertes étant donné que ceci serait inutile : à l'appel de la fonction ```SMTP::SendMail(String targets, MailConvertor mailConvertor)```, la connexion est ouverte, le message envoyé, et la connexion est fermée.
De cette façon la connexion ne reste pas toujours ouverte mais le fait d'avoir plusieurs instances nous permet de ne pas avoir à refaire toute la configuration de la connexion à chaque ouverture.

L'objet ```MailConvertor``` passé en paramètre à la fonction d'envoi de mail ci-dessus est un objet permettant, en lui donnant l'expéditeur d'un mail, son objet et son corps, de générer les lignes à envoyer avec les headers une seule fois.
Nous avions ainsi la possibilité de changer le(s) destinataire(s) du mail et de récupérer à nouveau toutes les lignes à envoyer au serveur sans pour autant regénérer la totalité des headers du mail.

Une seconde amélioration a été de regrouper les destinataires par nom de domaine, afin d'ouvrir une seule fois la connexion TCP vers un serveur, envoyer le mail pour tous les utilisateurs sur ce domaine et fermer la connexion.

### 3 - Frontend <a name="II3" />

#### A - Présentation interface graphique <a name="II3A" />

L'interface graphique utilisée dans le cadre du protocole STMP est toujours la même que celle utilisée pour le protocole POP3S. 
Il est tout de même important de spécifier que le code de cette dernière n'est plus le même que pour le protocole POP3S, car notre 
interface a été optimisée entre temps.

De plus, on utilise cette fois ci, non pas le premier onglet, listant tous les mails reçus par l'utilisateur,  mais le second onglet 
qui nous permettra d'envoyer un mails à un ou plusieurs destinataires. 

Vous trouverez ci-dessous un aperçu de notre fenêtre d'envoi de mails :

![Fenetre_envoi](https://raw.githubusercontent.com/Elomidas/POP3/master/images/Fenetre_envoi.png)

Afin d'améliorer considérablement l'expérience de l'utilisateur, le bouton "Répondre", présenté lors de la partie POP3S redirige automatiquement
l'émetteur sur l'onglet d'envoi de mails, et ce en remplissant automatiquement les champs nécessaires :

![Fenetre_repondre](https://raw.githubusercontent.com/Elomidas/POP3/master/images/Fenetre_repondre.png)

Ainsi, notre utilisateur n'a plus qu'a écrire sa réponse à l'endroit adéquat pour que son destinataire puisse savoir de quel mail est issue la réponse.

De plus, si le champ "Objet" n'est pas rempli par l'émetteur lors de l'écriture du mail, une confirmation lui sera demandée afin d'envoyer un mail ne comportant
pas de mention "Objet".

#### B - Gestion de plusieurs adresses mail valides ou non <a name="II3B" />

Bien évidemment, notre service de messagerie étant capable d'envoyer des mails à plusieurs destinataires, appartenant éventuellement à différents serveurs, 
nous avons du traiter l'existence ou non de chacune des adresses mails des destinataires. 

Dans un premier temps, nous réalisons, à l'aide d'une expression régulière, un test nous permettant de savoir si la syntaxe de chacune des adresses mails insérées est correcte ou non.
```java
    private static final String _MAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static boolean CheckMails(String mails){
        String tabmails[] = mails.split(";");
        for(String mail : tabmails){
            if(!CheckMail(mail.trim())){
                return false;
            }
        }
        return true;
    }
```

Deux possibilités s'offrent alors à nous : soit les adresses mails des destinataires possèdent une syntaxe correcte, auquel cas nous essayons d'envoyer 
le mail à chaque destinataire. Soit une ou plusieurs adresses mails possèdent une syntaxe incorrecte, auquel cas nous affichons un message d'erreur à l'utilisateur. 

Si la syntaxe de chaque adresse écrite par l'utilisateur est correcte, nous essayons d'envoyer le mail à chaque utilisateur ainsi mentionné. Si un utilisateur est mentionné
mais n'existe pas au niveau de notre serveur, un message est envoyé à l'utilisateur. Ce message indique les adresses mails de tous les utilisateurs inexistants :

![Fenetre_adresses_invalides](https://raw.githubusercontent.com/Elomidas/POP3/master/images/adresses_invalides_SMTP.png)

L'émetteur est alors informé de chaque destinataire n'ayant pas pu recevoir son mail. 

## III - Serveur <a name="III" />
Avant de commencer l'implémentation du Serveur, nous avons réalisé l'automate de celui-ci :
![Automate_Serveur](https://raw.githubusercontent.com/Elomidas/POP3/Serveur/images/Automate-serveur.png).
Pour le développement SMTP, nous nous sommes servi de ce que nous avions fait pour POP3, donc la structure du projet est similaire.
Dans le main, nous avons défini une boucle infini pour que le serveur puisse accepter toutes les connexions tant que celles-ci se font sur le bon port. Le serveur étant concurrent, lorsqu'un client se connectera sur le port, un thread sera créé dans la classe Tcp pour lui permettre de communiquer avec le serveur.
Le client ainsi connecté se verra attribuer une instanciation de la classe ObjetSmtpConnecte. Cette classe est chargé de faire respecté l'automate du serveur. Il recevra les commandes de l'utilisateur et retournera les résultats grâce aux méthode receive() et send() de la classe Tcp. 

Les messages d'erreurs et de confirmation que peuvent envoyer le serveur sont stockés dans la classe ReponseServeur.
```java
public class ReponseServeur {

	public final static String SMTP_SERVER_READY = "220 Simple Mail Transfer Service Ready";
	public final static String SMTP_500_UNKNOWN_COMMAND = "500 Erreur de syntaxe, commande non reconnue";
	public final static String SMTP_250_SERVERDOMAIN = "250 localhost";
	public final static String SMTP_221_CLOSING = "221 fermeture";
	public final static String SMTP_250_OK = "250 OK";
	public final static String SMTP_550_UNKNOWN_USER = "250 utilisateur inconnu";
	public final static String SMTP_354_START_READING = "354 debut de lecture";
	/* Etat */
	public final static String SERVER_READY = "Initialisation";
	public final static String SERVER_CONNEXION = "Connexion";
	public final static String SERVER_IDENTIFICATION = "Identification";
	public final static String SERVER_TRANSACTION = "Transaction de Messagerie";
	public final static String SERVER_ENVOIE = "Envoie de Message";
	public final static String SERVER_LECTURE = "Lecture des Lignes";

	public final static String SMTP_CRLF = "\\r\\n";
}
```
Comme dit plus haut, la classe ObjetSmtpConnecte va permettre la communication entre le client et le serveur. Afin de pouvoir traiter toutes les requêtes de l'utilisateur, l'ensemble des traitements possibles est contenue dans une boucle 'while(true)' qui ne s'arretera que lorsque l'utilisateur émettra une requête QUIT.
Dans un premier temps, on commence par initialiser l'état du serveur comme c'est marqué dans l'automate. Ensuite, on recupère la commande et les paramètres envoyés par le client. Selon l'état du serveur, la méthode correspondante sera appelé et prendra en paramètre les informations envoyées par l'utilisateur.
```java
			switch (etatServeur) {
				case SERVER_CONNEXION:
					reponseServeur = this.connexion(command, parameters);
					break;
				case SERVER_IDENTIFICATION:
					reponseServeur = this.identification(command, parameters);
					break;
				case SERVER_TRANSACTION:
					reponseServeur = this.transaction(command, parameters);
					break;
				case SERVER_ENVOIE:
					reponseServeur = this.envoie(command, parameters);
					break;
				case SERVER_LECTURE:
					reponseServeur = this.lecture(command, parameters);
					break;
				default:
					reponseServeur = SMTP_500_UNKNOWN_COMMAND;
			}
```
Chaque état a un ensemble de commande qui est possible d'éxécuter. Par exemple, dans l'état Identification, il sera possible pour l'utilisateur d'utiliser les commandes MAIL, RSET et QUIT.
```java
	private String identification(String command, String[] parameters) {
		switch (command){
			case "MAIL":
				return commandeMailFrom(parameters);
			case "QUIT":
				return commandeQuit();
			case "RSET":
				return commandeRset();
			default :
				return SMTP_500_UNKNOWN_COMMAND;
		}
	}
```
Si l'utilisateur envoie la commande QUIT, la méthode commandeQuit() va s'exécuter. Le booléen continuer devient false ce qui va entrainer l'arret de la boucle "while". Le serveur va se mettre dans l'état d'initialisation et va retourner à l'utilisateur un message de fermeture.
```java
	private String commandeQuit() {
		continuer = false;
		etatServeur = SERVER_READY;
		return SMTP_221_CLOSING;
	}
```
Lorsque le traitement est fini, on utilise la méthode send() de la connexion tcp du client pour lui renvoyer la réponses:
```java
	tcp.send(reponseServeur);
```
L'ensemble des commandes fonctionnent de la même manière.
## IV - Conclusion <a name="IV" />
