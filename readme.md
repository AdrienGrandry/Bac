# Pojet Bac

Programme de gestion de stock et location pour le bac.

# Action toujours à faire
- boisons : nom, fourni, repris et consomé
- pour reunion demander ce qu'on sort, demander qte avec facture
    => dans boissons afficher qte consomé seul et lors de la facture
    demander d'encodé les boissons à sortir du stock
-
- calculer facture boissons avec le nombre arrondi sans les chifre apres

# Organisation du projet - Avancement

- **Principale**
- **Agenda**
- locations
    - Enregistrer location
    - Gérer location
    - Facture
    - Tarif
- Boissons
    - **Mouvement**
    - **Stock**
    - **Produits**
    - Prévision
    - Commande
- **Options**

# Test du projet

- Principale
    - **Agenda**
    - **Gestion Locations**
    - **Boissons**
    - **Options**
    - **Quitter**
- agenda
    - **Affichage**
    - **Changement de mois <<**
    - **Changement de mois >>**
- location
    - **Enregistrer location**
    - Gérer location
    - Facture
    - Tarif
- boisson
    - **Mouvement**
        - **Affichage**
        - **Mouvement**
            - **Aucune Boisson**
            - **Boisson**
            - **MAJ Stock**
        - **Inventaire**
            - **Sans Boisson**
            - **Avec Boisson**
            - **MAJ Stock**
    - **Stock**
    - **Produits**
    - Prévision
    - Commande
- **Options**
    - **Lecture des couleurs**
    - **Lecture du chemin**
    - **Modification de la couleur**
    - **Modification du chemin**

# Optimisation et claireté du code

- agenda **Chat GPT Donc pas vérif**
    - app
        - DayPanel
        - EventLabel
        - GoogleAgendaStyleCalendar
    - google
        - EventMapper
        - GoogleAuthorizeUtil
        - GoogleCalendarService
    - model
        - CalendarModel
        - EventModel
    - util
        - DateUtilis
- **boisson**
    - **database**
        - **Database**
    - **mouvement**
        - **AddMouvement**
        - **Boisson**
        - **Inventaire**
        - **Mouvement**
    - **produit**
        - **AddProduit**
        - **Produit**
    - **start**
        - **Start**
    - **stock**
        - **Stock**
    - **Boisson**
    - **createMenu**
- location
    - facture
    - manageLocation
    - newLocation
        - **DateParser**
        - newLocation
    - **start**
        - **StartLocation**
    - tarifs
    - **CreateMenuLocation**
    - **Location**
- **options**
    - **ColorChooserWin11Style**
    - **ColorInfo**
    - **ColorXml**
    - **FolderChooserWin11Style**
    - **Options**
- **principal**
    - **MainFrame**
- **ressources**
    - **DataBase**
        - **QueryResult**
        - **Requete**
    - **LoadingDialog**
    - **Message**
    - **Style**
    - **XmlConfig**
-**MainApplication**