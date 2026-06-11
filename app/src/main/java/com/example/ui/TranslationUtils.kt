package com.example.ui

import java.util.Locale

object Trans {
    // Dynamic translations dictionary
    // We map the native Italian strings to their English, French, and Spanish counterpart.
    // If the locale is "it", it returns the original. Otherwise, it searches for translated text.
    // Default fallback is English ("en").
    private val translations = mapOf(
        "Today Budget" to mapOf(
            "en" to "Today Budget",
            "fr" to "Today Budget",
            "es" to "Today Budget",
            "it" to "Today Budget"
        ),
        "Ciao, %s! 👋" to mapOf(
            "en" to "Hello, %s! 👋",
            "fr" to "Bonjour, %s! 👋",
            "es" to "¡Hola, %s! 👋",
            "it" to "Ciao, %s! 👋"
        ),
        "Oggi" to mapOf(
            "en" to "Today",
            "fr" to "Aujourd'hui",
            "es" to "Hoy",
            "it" to "Oggi"
        ),
        "Cronologia" to mapOf(
            "en" to "History",
            "fr" to "Historique",
            "es" to "Historial",
            "it" to "Cronologia"
        ),
        "Cronologia Spese" to mapOf(
            "en" to "Expense History",
            "fr" to "Historique des Dépenses",
            "es" to "Historial de Gastos",
            "it" to "Cronologia Spese"
        ),
        "Modifica o elimina transazioni passate" to mapOf(
            "en" to "Edit or delete past transactions",
            "fr" to "Modifier ou supprimer les transactions passées",
            "es" to "Edita o elimina transacciones pasadas",
            "it" to "Modifica o elimina transazioni passate"
        ),
        "Analisi" to mapOf(
            "en" to "Analysis",
            "fr" to "Analyse",
            "es" to "Análisis",
            "it" to "Analisi"
        ),
        "Categorie" to mapOf(
            "en" to "Categories",
            "fr" to "Catégories",
            "es" to "Categorías",
            "it" to "Categorie"
        ),
        "Opzioni" to mapOf(
            "en" to "Options",
            "fr" to "Options",
            "es" to "Opciones",
            "it" to "Opzioni"
        ),
        "Impostazioni" to mapOf(
            "en" to "Settings",
            "fr" to "Paramètres",
            "es" to "Ajustes",
            "it" to "Impostazioni"
        ),
        "Statistiche" to mapOf(
            "en" to "Statistics",
            "fr" to "Statistiques",
            "es" to "Estadísticas",
            "it" to "Statistiche"
        ),
        "DISPONIBILE OGGI" to mapOf(
            "en" to "AVAILABLE TODAY",
            "fr" to "DISPONIBLE AUJOURD'HUI",
            "es" to "DISPONIBLE HOY",
            "it" to "DISPONIBILE OGGI"
        ),
        "PREVISTO DOMANI" to mapOf(
            "en" to "EXPECTED TOMORROW",
            "fr" to "PRÉVU DEMAIN",
            "es" to "PREVISTO MAÑANA",
            "it" to "PREVISTO DOMANI"
        ),
        "MENSALE RIMANENTE" to mapOf(
            "en" to "REMAINING MONTHLY",
            "fr" to "MENSUEL RESTANT",
            "es" to "MENSUAL RESTANTE",
            "it" to "MENSALE RIMANENTE"
        ),
        "Spesa Giornaliera Media" to mapOf(
            "en" to "Average Daily Spend",
            "fr" to "Dépense Quotidienne Moyenne",
            "es" to "Gasto Diario Promedio",
            "it" to "Spesa Giornaliera Media"
        ),
        "giorni rimasti" to mapOf(
            "en" to "days remaining",
            "fr" to "jours restants",
            "es" to "días restantes",
            "it" to "giorni rimasti"
        ),
        "Azioni Rapide" to mapOf(
            "en" to "Quick Actions",
            "fr" to "Actions Rapides",
            "es" to "Acciones Rápidas",
            "it" to "Azioni Rapide"
        ),
        "Nessuna categoria creata" to mapOf(
            "en" to "No categories created",
            "fr" to "Aucune catégorie créée",
            "es" to "No hay categorías creadas",
            "it" to "Nessuna categoria creata"
        ),
        "Tracciando categorie..." to mapOf(
            "en" to "Tracking categories...",
            "fr" to "Suivi des catégories...",
            "es" to "Seguimiento de categorías...",
            "it" to "Tracciando categorie..."
        ),
        "Correggi" to mapOf(
            "en" to "Adjust",
            "fr" to "Ajuster",
            "es" to "Corregir",
            "it" to "Correggi"
        ),
        "Nuova Spesa" to mapOf(
            "en" to "New Expense",
            "fr" to "Nouvelle Dépense",
            "es" to "Nuevo Gasto",
            "it" to "Nuova Spesa"
        ),
        "Spesa" to mapOf(
            "en" to "Expense",
            "fr" to "Dépense",
            "es" to "Gasto",
            "it" to "Spesa"
        ),
        "Regola" to mapOf(
            "en" to "Adjust",
            "fr" to "Ajuster",
            "es" to "Corregir",
            "it" to "Regola"
        ),
        "+ %s %.2f mese precedente" to mapOf(
            "en" to "+ %s %.2f previous month",
            "fr" to "+ %s %.2f mois précédent",
            "es" to "+ %s %.2f mes anterior",
            "it" to "+ %s %.2f mese precedente"
        ),
        "- %s %.2f mese precedente" to mapOf(
            "en" to "- %s %.2f previous month",
            "fr" to "- %s %.2f mois précédent",
            "es" to "- %s %.2f mes anterior",
            "it" to "- %s %.2f mese precedente"
        ),
        "Cerca per descrizione o categoria..." to mapOf(
            "en" to "Search by description or category...",
            "fr" to "Rechercher par description...",
            "es" to "Buscar por descripción...",
            "it" to "Cerca per descrizione o categoria..."
        ),
        "Nessuna spesa registrata" to mapOf(
            "en" to "No expenses recorded",
            "fr" to "Aucune dépense enregistrée",
            "es" to "No hay gastos registrados",
            "it" to "Nessuna spesa registrata"
        ),
        "Aggiungi la tua prima spesa per iniziare a tracciare il budget." to mapOf(
            "en" to "Add your first expense to start tracking your budget.",
            "fr" to "Ajoutez votre première dépense pour commencer à suivre votre budget.",
            "es" to "Agrega tu primer gasto para comenzar a rastrear tu presupuesto.",
            "it" to "Aggiungi la tua prima spesa per iniziare a tracciare il budget."
        ),
        "Riepilogo Ciclo di Budget" to mapOf(
            "en" to "Budget Cycle Summary",
            "fr" to "Résumé du Cycle Budgétaire",
            "es" to "Resumen del Ciclo de Presupuesto",
            "it" to "Riepilogo Ciclo di Budget"
        ),
        "Budget Iniziale:" to mapOf(
            "en" to "Initial Budget:",
            "fr" to "Budget Initial :",
            "es" to "Presupuesto Inicial:",
            "it" to "Budget Iniziale:"
        ),
        "Spese Totali:" to mapOf(
            "en" to "Total Expenses:",
            "fr" to "Dépenses Totales :",
            "es" to "Gastos Totales:",
            "it" to "Spese Totali:"
        ),
        "Correzioni apportate:" to mapOf(
            "en" to "Adjustments made:",
            "fr" to "Ajustements apportés :",
            "es" to "Ajustes realizados:",
            "it" to "Correzioni apportate:"
        ),
        "Saldo Finale Netto:" to mapOf(
            "en" to "Net Final Balance:",
            "fr" to "Solde Final Net :",
            "es" to "Saldo Final Neto:",
            "it" to "Saldo Finale Netto:"
        ),
        "Eliminare spesa?" to mapOf(
            "en" to "Delete expense?",
            "fr" to "Supprimer la dépense ?",
            "es" to "¿Eliminar gasto?",
            "it" to "Eliminare spesa?"
        ),
        "Sei sicuro di voler eliminare questa spesa?" to mapOf(
            "en" to "Are you sure you want to delete this expense?",
            "fr" to "Êtes-vous sûr de vouloir supprimer cette dépense ?",
            "es" to "¿Estás seguro de que deseas eliminar este gasto?",
            "it" to "Sei sicuro di voler eliminare questa spesa?"
        ),
        "Sicuro di voler eliminare questa spesa di %s %s? L'operazione ricalcolerà immediatamente il budget." to mapOf(
            "en" to "Are you sure you want to delete this expense of %s %s? This operation will immediately recalculate the budget.",
            "fr" to "Êtes-vous sûr de vouloir supprimer cette dépense de %s %s ? L'opération recalculera immédiatement le budget.",
            "es" to "¿Estás seguro de que deseas eliminar este gasto de %s %s? La operación recalculará inmediatamente el presupuesto.",
            "it" to "Sicuro di voler eliminare questa spesa di %s %s? L'operazione ricalcolerà immediatamente il budget."
        ),
        "Elimina" to mapOf(
            "en" to "Delete",
            "fr" to "Supprimer",
            "es" to "Eliminar",
            "it" to "Elimina"
        ),
        "Annulla" to mapOf(
            "en" to "Cancel",
            "fr" to "Annuler",
            "es" to "Cancelar",
            "it" to "Annulla"
        ),
        "Modifica" to mapOf(
            "en" to "Edit",
            "fr" to "Modifier",
            "es" to "Modificar",
            "it" to "Modifica"
        ),
        "Analisi Spese" to mapOf(
            "en" to "Expense Analysis",
            "fr" to "Analyse des Dépenses",
            "es" to "Análisis de Gastos",
            "it" to "Analisi Spese"
        ),
        "Statistiche Spese" to mapOf(
            "en" to "Expense Statistics",
            "fr" to "Statistiques des Dépenses",
            "es" to "Estadísticas de Gastos",
            "it" to "Statistiche Spese"
        ),
        "Grafici di andamento e categorie" to mapOf(
            "en" to "Trend graphs and categories",
            "fr" to "Graphiques de tendance et catégories",
            "es" to "Gráficos de tendance et catégories",
            "it" to "Grafici di andamento e categorie"
        ),
        "Dati Demo Attivi" to mapOf(
            "en" to "Demo Data Active",
            "fr" to "Données Démo Actives",
            "es" to "Datos Demo Activos",
            "it" to "Dati Demo Attivi"
        ),
        "Attiva Demo" to mapOf(
            "en" to "Enable Demo",
            "fr" to "Activer Démo",
            "es" to "Activar Demo",
            "it" to "Attiva Demo"
        ),
        "Risparmio Mese Precedente" to mapOf(
            "en" to "Previous Month Savings",
            "fr" to "Épargne du Mois Précédent",
            "es" to "Ahorro del Mes Anterior",
            "it" to "Risparmio Mese Precedente"
        ),
        "I grafici reali prenderanno forma non appena registrerai delle spese. Al momento stai visualizzando dati demo rappresentativi." to mapOf(
            "en" to "Real charts will take shape as soon as you record expenses. Currently displaying representative demo data.",
            "fr" to "Les graphiques réels prendront forme dès que vous enregistrerez des dépenses. Affichage actuel de données de démonstration.",
            "es" to "Los gráficos reales tomarán forma tan pronto como registres gastos. Actualmente se muestran datos de demostración.",
            "it" to "I grafici reali prenderanno forma non appena registrerai delle spese. Al momento stai visualizzando dati demo rappresentativi."
        ),
        "Totale Speso" to mapOf(
            "en" to "Total Spent",
            "fr" to "Total Dépensé",
            "es" to "Total Gastado",
            "it" to "Totale Speso"
        ),
        "Andamento Storico" to mapOf(
            "en" to "Historical Trend",
            "fr" to "Tendance Historique",
            "es" to "Tendencia Histórica",
            "it" to "Andamento Storico"
        ),
        "Complessivo" to mapOf(
            "en" to "Overall",
            "fr" to "Global",
            "es" to "Global",
            "it" to "Complessivo"
        ),
        "Totale Spese" to mapOf(
            "en" to "Total Expenses",
            "fr" to "Total des Dépenses",
            "es" to "Total de Gastos",
            "it" to "Totale Spese"
        ),
        "In questo ciclo" to mapOf(
            "en" to "In this cycle",
            "fr" to "Dans ce cycle",
            "es" to "En este ciclo",
            "it" to "In questo ciclo"
        ),
        "Spesa Totale" to mapOf(
            "en" to "Total Spend",
            "fr" to "Dépense Totale",
            "es" to "Gasto Total",
            "it" to "Spesa Totale"
        ),
        "Budget Rimanente" to mapOf(
            "en" to "Remaining Budget",
            "fr" to "Budget Restant",
            "es" to "Presupuesto Restante",
            "it" to "Budget Rimanente"
        ),
        "Distribuzione per Categoria" to mapOf(
            "en" to "Distribution by Category",
            "fr" to "Répartition par Catégorie",
            "es" to "Distribución por Categoría",
            "it" to "Distribuzione per Categoria"
        ),
        "Andamento Giornaliero" to mapOf(
            "en" to "Daily Trend",
            "fr" to "Tendance Quotidienne",
            "es" to "Tendencia Diaria",
            "it" to "Andamento Giornaliero"
        ),
        "Nessun dato disponibile" to mapOf(
            "en" to "No data available",
            "fr" to "Aucune donnée disponible",
            "es" to "No hay datos disponibles",
            "it" to "Nessun dato disponibile"
        ),
        "Nessuna spesa inserita in questo periodo." to mapOf(
            "en" to "No expenses entered in this period.",
            "fr" to "Aucune dépense saisie sur cette période.",
            "es" to "No se han ingresado gastos en este período.",
            "it" to "Nessuna spesa inserita in questo periodo."
        ),
        "Gestione Categorie" to mapOf(
            "en" to "Manage Categories",
            "fr" to "Gérer les Catégories",
            "es" to "Gestionar Categorías",
            "it" to "Gestione Categorie"
        ),
        "Personalizza nomi e simboli di riferimento" to mapOf(
            "en" to "Customize names and reference icons",
            "fr" to "Personnalisez les noms et icônes",
            "es" to "Personaliza nombres e iconos",
            "it" to "Personalizza nomi e simboli di riferimento"
        ),
        "Nuova Categoria" to mapOf(
            "en" to "New Category",
            "fr" to "Nouvelle Catégorie",
            "es" to "Nueva Categoría",
            "it" to "Nuova Categoria"
        ),
        "Tracciando categorie..." to mapOf(
            "en" to "Loading categories...",
            "fr" to "Chargement des catégories...",
            "es" to "Cargando categorías...",
            "it" to "Tracciando categorie..."
        ),
        "Sicuro di voler eliminare la categoria '%s'? Le spese associate a questa categoria verranno ri-numerate come 'Generiche' ma non saranno rimosse dai conteggi totali del budget." to mapOf(
            "en" to "Are you sure you want to delete the category '%s'? Associated expenses will be reset to 'Generic' but won't be removed from your budget totals.",
            "fr" to "Êtes-vous sûr de vouloir supprimer la catégorie '%s' ? Les dépenses associées seront définies comme 'Génériques' mais ne seront pas exclues de votre budget.",
            "es" to "¿Estás seguro de que deseas eliminar la categoría '%s'? Los gastos asociados se restablecerán como 'Genéricos' pero no se eliminarán de tus totales.",
            "it" to "Sicuro di voler eliminare la categoria '%s'? Le spese associate a questa categoria verranno ri-numerate come 'Generiche' ma non saranno rimosse dai conteggi totali del budget."
        ),
        "Puoi creare e personalizzare fino a 10 categorie per organizzare le tue spese giornaliere." to mapOf(
            "en" to "You can create and customize up to 10 categories to organize your daily expenses.",
            "fr" to "Vous pouvez créer et personnaliser jusqu'à 10 catégories pour organiser vos dépenses quotidiennes.",
            "es" to "Puedes crear y personalizar hasta 10 categorías para organizar tus gastos diarios.",
            "it" to "Puoi creare e personalizzare fino a 10 categorie per organizzare le tue spese giornaliere."
        ),
        "Limite Categorie" to mapOf(
            "en" to "Categories Limit",
            "fr" to "Limite des Catégories",
            "es" to "Límite de Categorías",
            "it" to "Limite Categorie"
        ),
        "Puoi impostare al massimo 10 categorie per mantenere l'interfaccia pulita e leggibile. Se hai bisogno di una nuova categoria, modifica o elimina una di quelle esistenti." to mapOf(
            "en" to "You can set up to 10 categories to keep the interface clean and readable. To create a new one, please edit or delete an existing category.",
            "fr" to "Vous pouvez configurer jusqu'à 10 catégories pour garder l'interface propre. Pour en créer une nouvelle, veuillez modifier ou supprimer une existante.",
            "es" to "Puedes configurar hasta 10 categorías para mantener la interfaz limpia. Para crear una nueva, edita o elimina una de las existentes.",
            "it" to "Puoi impostare al massimo 10 categorie per mantenere l'interfaccia pulita e leggibile. Se hai bisogno di una nuova categoria, modifica o elimina una di quelle esistenti."
        ),
        "Eliminare categoria?" to mapOf(
            "en" to "Delete category?",
            "fr" to "Supprimer la catégorie ?",
            "es" to "¿Eliminar categoría?",
            "it" to "Eliminare categoria?"
        ),
        "Sei sicuro di voler eliminare la categoria \"%s\"? Anche tutte le spese associate verranno eliminate." to mapOf(
            "en" to "Are you sure you want to delete the category \"%s\"? All associated expenses will also be deleted.",
            "fr" to "Êtes-vous sûr de vouloir supprimer la catégorie \"%s\" ? Toutes les dépenses associées seront également supprimées.",
            "es" to "¿Estás seguro de que deseas eliminar la categoría \"%s\"? Todos los gastos asociados también serán eliminados.",
            "it" to "Sei sicuro di voler eliminare la categoria \"%s\"? Anche tutte le spese associate verranno eliminate."
        ),
        "Personalizza regole, orari e calcolo del budget" to mapOf(
            "en" to "Customize rules, schedule, and budget calculation",
            "fr" to "Personnalisez les règles, les horaires et le calcul du budget",
            "es" to "Personaliza las reglas, los horarios y el cálculo del presupuesto",
            "it" to "Personalizza regole, orari e calcolo del budget"
        ),
        "Profilo Utente" to mapOf(
            "en" to "User Profile",
            "fr" to "Profil de l'Utilisateur",
            "es" to "Perfil de Usuario",
            "it" to "Profilo Utente"
        ),
        "Nome Utente" to mapOf(
            "en" to "Username",
            "fr" to "Nom d'Utilisateur",
            "es" to "Nombre de Usuario",
            "it" to "Nome Utente"
        ),
        "Regione / Lingua" to mapOf(
            "en" to "Region / Language",
            "fr" to "Région / Langue",
            "es" to "Región / Idioma",
            "it" to "Regione / Lingua"
        ),
        "Imposta Budget mensile" to mapOf(
            "en" to "Set Monthly Budget",
            "fr" to "Définir le Budget Mensuel",
            "es" to "Establecer Presupuesto Mensual",
            "it" to "Imposta Budget mensile"
        ),
        "Ad esempio, impostando il giorno '%d', il tuo periodo di budget attuale andrà dal %d di questo mese al giorno %d del mese successivo." to mapOf(
            "en" to "For example, setting day '%d', your current budget period will run from the %d of this month to the %d of the next month.",
            "fr" to "Par exemple, en réglant le jour '%d', votre période budgétaire actuelle s'étendra du %d de ce mois au %d du mois suivant.",
            "es" to "Por ejemplo, estableciendo el día '%d', tu período de presupuesto actual irá del %d de este mes al día %d del sguiente mes.",
            "it" to "Ad esempio, impostando il giorno '%d', il tuo periodo di budget attuale andrà dal %d di questo mese al giorno %d del mese successivo."
        ),
        "Orario standard a mezzanotte (00:00). Tutte le spese inserite dopo mezzanotte apparterranno alla nuova giornata." to mapOf(
            "en" to "Standard time at midnight (00:00). All expenses entered after midnight will belong to the new day.",
            "fr" to "Heure standard à minuit (00:00). Toutes les dépenses saisies après minuit appartiendront à la nouvelle journée.",
            "es" to "Hora estándar a la medianoche (00:00). Todos los gastos ingresados después de la medianoche pertenecerán al nuevo día.",
            "it" to "Orario standard a mezzanotte (00:00). Tutte le spese inserite dopo mezzanotte apparterranno alla nuova giornata."
        ),
        "Le spese effettuate fino alle ore %s del mattino saranno dedotte dal budget della giornata precedente." to mapOf(
            "en" to "Expenses made up to %s in the morning will be deducted from the previous day's budget.",
            "fr" to "Les dépenses effectuées jusqu'à %s du matin seront déduites du budget de la journée précédente.",
            "es" to "Los gastos del día anterior comprenderán los realizados hasta las %s de la mañana.",
            "it" to "Le spese effettuate fino alle ore %s del mattino saranno dedotte dal budget della giornata precedente."
        ),
        "Decrementa" to mapOf(
            "en" to "Decrease",
            "fr" to "Diminuer",
            "es" to "Disminuir",
            "it" to "Decrementa"
        ),
        "Incrementa" to mapOf(
            "en" to "Increase",
            "fr" to "Augmenter",
            "es" to "Incrementar",
            "it" to "Incrementa"
        ),
        "Indietro" to mapOf(
            "en" to "Back",
            "fr" to "Retour",
            "es" to "Atrás",
            "it" to "Indietro"
        ),
        "Avanti" to mapOf(
            "en" to "Next",
            "fr" to "Suivant",
            "es" to "Siguiente",
            "it" to "Avanti"
        ),
        "Budget Mensile Predefinito (%s)" to mapOf(
            "en" to "Default Monthly Budget (%s)",
            "fr" to "Budget Mensuel par Défaut (%s)",
            "es" to "Presupuesto Mensual Mínimo (%s)",
            "it" to "Budget Mensile Predefinito (%s)"
        ),
        "Valuta dell'App" to mapOf(
            "en" to "App Currency",
            "fr" to "Devise de l'App",
            "es" to "Moneda de la App",
            "it" to "Valuta dell'App"
        ),
        "Seleziona il simbolo monetario che preferisci utilizzare per il budget e le spese." to mapOf(
            "en" to "Select your preferred currency symbol for budget and expenses.",
            "fr" to "Sélectionnez le symbole de devise que vous préférez utiliser pour le budget et les dépenses.",
            "es" to "Selecciona el símbolo de moneda que prefieras utilizar para el presupuesto y los gastos.",
            "it" to "Seleziona il simbolo monetario che preferisci utilizzare per il budget e le spese."
        ),
        "Giorno Inizio Ciclo" to mapOf(
            "en" to "Cycle Start Day",
            "fr" to "Jour de Début du Cycle",
            "es" to "Día de Inicio del Ciclo",
            "it" to "Giorno Inizio Ciclo"
        ),
        "Definisci in quale giorno del mese solare inizia ufficialmente il conteggio del tuo budget." to mapOf(
            "en" to "Specify on which day of the calendar month your budget cycle officially starts.",
            "fr" to "Définissez quel jour du mois civil le décompte de votre budget commence officiellement.",
            "es" to "Define qué día del mes calendario comienza oficialmente el conteo de tu presupuesto.",
            "it" to "Definisci in quale giorno del mese solare inizia ufficialmente il conteggio del tuo budget."
        ),
        "Giorno %d di ogni mese" to mapOf(
            "en" to "Day %d of each month",
            "fr" to "Jour %d de chaque mois",
            "es" to "Día %d de cada mes",
            "it" to "Giorno %d di ogni mese"
        ),
        "Ora Inizio Giornata" to mapOf(
            "en" to "Day Start Hour",
            "fr" to "Heure de Début de la Journée",
            "es" to "Hora de Inicio del Día",
            "it" to "Ora Inizio Giornata"
        ),
        "Imposta l'orario in cui il budget giornaliero si azzera e inizia la giornata successiva (es. alle 04:00)." to mapOf(
            "en" to "Set the hour when the daily budget resets and the next day starts (e.g., at 04:00).",
            "fr" to "Réglez l'heure à laquelle le budget quotidien est réinitialisé et le jour suivant commence (ex. à 04:00).",
            "es" to "Establece la hora en que el presupuesto diario se restablece y comienza el día siguiente (ej. a las 04:00).",
            "it" to "Imposta l'orario in cui il budget giornaliero si azzera e inizia la giornata successiva (es. alle 04:00)."
        ),
        "Reset Giornaliero alle ore: %02d:00" to mapOf(
            "en" to "Daily Reset at: %02d:00",
            "fr" to "Réinitialisation Quotidienne à : %02d:00",
            "es" to "Restablecimiento Diario a las: %02d:00",
            "it" to "Reset Giornaliero alle ore: %02d:00"
        ),
        "Risparmio/Carry Over" to mapOf(
            "en" to "Carry Over Savings",
            "fr" to "Épargne / Report (Carry Over)",
            "es" to "Ahorro / Carry Over",
            "it" to "Risparmio/Carry Over"
        ),
        "Attiva per cumulare al budget odierno i risparmi (o debiti) dei giorni e dei mesi precedenti." to mapOf(
            "en" to "Activate to carry over savings (or deficits) from previous days and months to today's budget.",
            "fr" to "Activez pour cumuler au budget d'aujourd'hui les économies (ou déficits) des jours et mois précédents.",
            "es" to "Activa para acumular al presupuesto de hoy los ahorros (o déficits) de los días y meses anteriores.",
            "it" to "Attiva per cumulare al budget odierno i risparmi (o debiti) dei giorni e dei mesi precedenti."
        ),
        "Consenti Cumulo Budget" to mapOf(
            "en" to "Allow Budget Accumulation",
            "fr" to "Autoriser le Report du Budget",
            "es" to "Permitir Acumulación de Presupuesto",
            "it" to "Consenti Cumulo Budget"
        ),
        "Notifiche Push Personalizzate" to mapOf(
            "en" to "Custom Push Notifications",
            "fr" to "Notifications Push Personnalisées",
            "es" to "Notificaciones Push Personalizadas",
            "it" to "Notifiche Push Personalizzate"
        ),
        "Abilita" to mapOf(
            "en" to "Enable",
            "fr" to "Activer",
            "es" to "Habilitar",
            "it" to "Abilita"
        ),
        "Inserimento Spese Giornaliere" to mapOf(
            "en" to "Daily Expense Reminder",
            "fr" to "Rappel des Dépenses Quotidiennes",
            "es" to "Recordatorio de Gastos Diarios",
            "it" to "Inserimento Spese Giornaliere"
        ),
        "Ricevi un promemoria all'orario indicato per inserire le spese della giornata." to mapOf(
            "en" to "Receive a reminder at the specified time to input the day's expenses.",
            "fr" to "Recevez un rappel à l'heure indiquée pour saisir vos dépenses de la journée.",
            "es" to "Recibe un recordatorio a la hora indicada para ingresar los gastos del día.",
            "it" to "Ricevi un promemoria all'orario indicato per inserire le spese della giornata."
        ),
        "Modifica Orario" to mapOf(
            "en" to "Edit Time",
            "fr" to "Modifier l'Heure",
            "es" to "Editar Hora",
            "it" to "Modifica Orario"
        ),
        "Orario: %s" to mapOf(
            "en" to "Time: %s",
            "fr" to "Horaire : %s",
            "es" to "Hora: %s",
            "it" to "Orario: %s"
        ),
        "Riepilogo Spese Settimanale/Mensile" to mapOf(
            "en" to "Weekly/Monthly Expense Summary",
            "fr" to "Résumé des Dépenses Hebdomadaire/Mensuel",
            "es" to "Resumen de Gastos Semanal/Mensual",
            "it" to "Riepilogo Spese Settimanale/Mensile"
        ),
        "Invia notifiche per rimanere aggiornato sull'andamento globale." to mapOf(
            "en" to "Send notifications to stay updated on global budget progress.",
            "fr" to "Envoyer des notifications pour rester informé de l'évolution globale.",
            "es" to "Enviar notificaciones para mantenerte actualizado sobre el progreso global.",
            "it" to "Invia notifiche per rimanere aggiornato sull'andamento globale."
        ),
        "Notifica Conferma Budget" to mapOf(
            "en" to "Confirm Budget Notification",
            "fr" to "Notification de Confirmation du Budget",
            "es" to "Notificación de Confirmación de Presupuesto",
            "it" to "Notifica Conferma Budget"
        ),
        "Ricevi un avviso il primo giorno del tuo ciclo di budget per confermarlo o adeguarlo." to mapOf(
            "en" to "Receive an alert on the first day of your budget cycle to confirm or adjust it.",
            "fr" to "Recevez une alerte le premier jour de votre cycle budgétaire pour le confirmer ou l'ajuster.",
            "es" to "Recibe una alerta el primer día de tu ciclo de presupuesto para confirmarlo o ajustarlo.",
            "it" to "Ricevi un avviso il primo giorno del tuo ciclo di budget per confermarlo o adeguarlo."
        ),
        "Visualizza Test:" to mapOf(
            "en" to "View Test:",
            "fr" to "Afficher le Test :",
            "es" to "Ver Prueba:",
            "it" to "Visualizza Test:"
        ),
        "Notifica Spese" to mapOf(
            "en" to "Expense Alert",
            "fr" to "Alerte de Dépenses",
            "es" to "Alerta de Gastos",
            "it" to "Notifica Spese"
        ),
        "Mese Nuovo" to mapOf(
            "en" to "New Month",
            "fr" to "Nouveau Mois",
            "es" to "Nuevo Mes",
            "it" to "Mese Nuovo"
        ),
        "Impostazioni Salvate Correttamente!" to mapOf(
            "en" to "Settings Saved Successfully!",
            "fr" to "Paramètres enregistrés avec succès !",
            "es" to "¡Ajustes Guardados Correctamente!",
            "it" to "Impostazioni Salvate Correttamente!"
        ),
        "Inserisci un budget mensile valido." to mapOf(
            "en" to "Please enter a valid monthly budget.",
            "fr" to "Veuillez saisir un budget mensuel valide.",
            "es" to "Por favor ingrese un presupuesto mensual válido.",
            "it" to "Inserisci un budget mensile valido."
        ),
        "Impostazioni salvate!" to mapOf(
            "en" to "Settings saved!",
            "fr" to "Paramètres enregistrés !",
            "es" to "¡Ajustes guardados!",
            "it" to "Impostazioni salvate!"
        ),
        "Salva Impostazioni" to mapOf(
            "en" to "Save Settings",
            "fr" to "Enregistrer les Paramètres",
            "es" to "Guardar Ajustes",
            "it" to "Salva Impostazioni"
        ),
        "Modifica Spesa" to mapOf(
            "en" to "Edit Expense",
            "fr" to "Modifier la Dépense",
            "es" to "Editar Gasto",
            "it" to "Modifica Spesa"
        ),
        "Importo (%s)" to mapOf(
            "en" to "Amount (%s)",
            "fr" to "Montant (%s)",
            "es" to "Importe (%s)",
            "it" to "Importo (%s)"
        ),
        "Seleziona Categoria" to mapOf(
            "en" to "Select Category",
            "fr" to "Sélectionner une Catégorie",
            "es" to "Seleccionar Categoría",
            "it" to "Seleziona Categoria"
        ),
        "Nota / Descrizione" to mapOf(
            "en" to "Note / Description",
            "fr" to "Note / Description",
            "es" to "Nota / Descripción",
            "it" to "Nota / Descrizione"
        ),
        "es. Spesa settimanale" to mapOf(
            "en" to "e.g., Weekly groceries",
            "fr" to "ex. Courses de la semaine",
            "es" to "ej. Gasto semanal",
            "it" to "es. Spesa settimanale"
        ),
        "Data Spesa" to mapOf(
            "en" to "Expense Date",
            "fr" to "Date de la Dépense",
            "es" to "Fecha del Gasto",
            "it" to "Data Spesa"
        ),
        "Seleziona Data" to mapOf(
            "en" to "Select Date",
            "fr" to "Sélectionner la Date",
            "es" to "Seleccionar Fecha",
            "it" to "Seleziona Data"
        ),
        "Inserisci un importo valido." to mapOf(
            "en" to "Please enter a valid amount.",
            "fr" to "Veuillez saisir un montant valide.",
            "es" to "Por favor ingrese un importe válido.",
            "it" to "Inserisci un importo valido."
        ),
        "Seleziona una categoria." to mapOf(
            "en" to "Please select a category.",
            "fr" to "Veuillez sélectionner une catégorie.",
            "es" to "Por favor seleccione una categoría.",
            "it" to "Seleziona una categoria."
        ),
        "Regola Budget" to mapOf(
            "en" to "Adjust Budget",
            "fr" to "Ajuster le Budget",
            "es" to "Ajustar Presupuesto",
            "it" to "Regola Budget"
        ),
        "In questa sezione si possono aggiungere o rimuovere importi dal budget complessivo impostato." to mapOf(
            "en" to "In this section you can add or remove amounts from the overall set budget.",
            "fr" to "Dans cette section, vous pouvez ajouter ou supprimer des montants du budget global configuré.",
            "es" to "En esta sección se pueden agregar o eliminar importes del presupuesto general establecido.",
            "it" to "In questa sezione si possono aggiungere o rimuovere importi dal budget complessivo impostato."
        ),
        "Nota / Motivo" to mapOf(
            "en" to "Note / Reason",
            "fr" to "Note / Motif",
            "es" to "Nota / Motivo",
            "it" to "Nota / Motivo"
        ),
        "es. Rimborso, Regalo, etc." to mapOf(
            "en" to "e.g., Refund, Gift, etc.",
            "fr" to "ex. Remboursement, Cadeau, etc.",
            "es" to "ej. Reembolso, Regalo, etc.",
            "it" to "es. Rimborso, Regalo, etc."
        ),
        "Sforamento" to mapOf(
            "en" to "Budget Limit Exceeded",
            "fr" to "Dépassement du Budget",
            "es" to "Límite Excedido",
            "it" to "Sforamento"
        ),
        "FINE MESE: %s" to mapOf(
            "en" to "END OF MONTH: %s",
            "fr" to "FIN DU MOIS : %s",
            "es" to "FIN DE MES: %s",
            "it" to "FINE MESE: %s"
        ),
        "BUDGET MENSILE IN CORSO" to mapOf(
            "en" to "CURRENT MONTHLY BUDGET",
            "fr" to "BUDGET MENSUEL EN COURS",
            "es" to "PRESUPUESTO MENSUAL EN CURSO",
            "it" to "BUDGET MENSILE IN CORSO"
        ),
        "Hai superato il budget previsto per oggi! Domani l'app ricalcolerà lo spendibile per farti rientrare con facilità." to mapOf(
            "en" to "You have exceeded today's budget! Tomorrow the app will recalculate the remaining amount to help you stay on track.",
            "fr" to "Vous avez dépassé le budget d'aujourd'hui ! Demain, l'application recalculera le montant restant pour vous aider à rester sur la bonne voie.",
            "es" to "¡Has superado el presupuesto de hoy! Mañana la aplicación recalculará el monto restante para ayudarte a mantener el control.",
            "it" to "Hai superato il budget previsto per oggi! Domani l'app ricalcolerà lo spendibile per farti rientrare con facilità."
        ),
        "%s %.2f spesi / %s %.2f" to mapOf(
            "en" to "%s %.2f spent / %s %.2f",
            "fr" to "%s %.2f dépensés / %s %.2f",
            "es" to "%s %.2f gastados / %s %.2f",
            "it" to "%s %.2f spesi / %s %.2f"
        ),
        "NUOVA SPESA RAPIDA" to mapOf(
            "en" to "NEW QUICK EXPENSE",
            "fr" to "NOUVELLE DÉPENSE RAPIDE",
            "es" to "NUEVO GASTO RÁPIDO",
            "it" to "NUOVA SPESA RAPIDA"
        ),
        "Cronologia Recente" to mapOf(
            "en" to "Recent History",
            "fr" to "Historique Récent",
            "es" to "Historial Reciente",
            "it" to "Cronologia Recente"
        ),
        "VEDI TUTTO" to mapOf(
            "en" to "SEE ALL",
            "fr" to "VOIR TOUT",
            "es" to "VER TODO",
            "it" to "VEDI TUTTO"
        ),
        "Nessuna spesa oggi. Comincia a risparmiare!" to mapOf(
            "en" to "No expenses today. Start saving!",
            "fr" to "Aucune dépense aujourd'hui. Commencez à épargner !",
            "es" to "¡Sin gastos hoy! ¡Comienza a ahorrar!",
            "it" to "Nessuna spesa oggi. Comincia a risparmiare!"
        ),
        "Aggiungi Nuova Categoria" to mapOf(
            "en" to "Add New Category",
            "fr" to "Ajouter une Nouvelle Catégorie",
            "es" to "Agregar Nueva Categoría",
            "it" to "Aggiungi Nuova Categoria"
        ),
        "Modifica Categoria" to mapOf(
            "en" to "Edit Category",
            "fr" to "Modifier la Catégorie",
            "es" to "Editar Categoría",
            "it" to "Modifica Categoria"
        ),
        "Nome Categoria" to mapOf(
            "en" to "Category Name",
            "fr" to "Nom de la Catégorie",
            "es" to "Nombre de la Categoría",
            "it" to "Nome Categoria"
        ),
        "es. Intrattenimento" to mapOf(
            "en" to "e.g., Entertainment",
            "fr" to "ex. Divertissement",
            "es" to "ej. Entretenimiento",
            "it" to "es. Intrattenimento"
        ),
        "Seleziona Icona / Emoji" to mapOf(
            "en" to "Select Icon / Emoji",
            "fr" to "Sélectionner un Icône / Émoji",
            "es" to "Seleccionar Icono / Emoji",
            "it" to "Seleziona Icona / Emoji"
        ),
        "Inserisci un nome valido." to mapOf(
            "en" to "Please enter a valid name.",
            "fr" to "Veuillez saisir un nom valide.",
            "es" to "Por favor ingrese un nombre válido.",
            "it" to "Inserisci un nome valido."
        )
    )

    fun translate(key: String): String {
        val locale = Locale.getDefault().language
        val langMap = translations[key] ?: return key
        return langMap[locale] ?: langMap["en"] ?: key
    }
}

fun String.loc(): String {
    return Trans.translate(this)
}

fun String.loc(vararg args: Any): String {
    val translated = Trans.translate(this)
    return try {
        // Apply Italian-style formatting (comma as decimal separator) for doubles in args
        val formattedArgs = args.map { arg ->
            if (arg is Double) {
                String.format(Locale.ITALY, "%.2f", arg)
            } else {
                arg
            }
        }.toTypedArray()
        String.format(translated, *formattedArgs)
    } catch (e: Exception) {
        translated
    }
}

/**
 * Formats a double value with two decimal places and a comma as separator.
 */
fun Double.formatCurrency(): String {
    return String.format(Locale.ITALY, "%.2f", this)
}

/**
 * Parses a string to a double, accepting both dot and comma as decimal separators.
 */
fun String.parseCurrency(): Double? {
    return this.replace(",", ".").toDoubleOrNull()
}
