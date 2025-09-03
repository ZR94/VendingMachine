# Analisi Funzionale e Priorità di Sviluppo

Di seguito viene proposta un’analisi funzionale delle principali caratteristiche che il sistema dovrebbe avere, con particolare attenzione alle funzionalità, alle tecnologie e alle priorità di sviluppo. L’obiettivo è fornire una visione di insieme chiara e dare una scaletta di implementazione ragionata, considerando che il backend sarà sviluppato in Java e l’interfaccia utente sarà web-based.

---

## 1. Obiettivi e Contesto

Il sistema deve gestire macchine per la distribuzione automatica di bevande calde (caffè, latte, tè, ecc.) collocate in più istituti scolastici. Si identificano due livelli principali:

1. **Livello “campo” (IoT)**: ogni macchina sul campo simula/gestisce:
   - Erogazione delle bevande.
   - Gestione del denaro inserito dagli utenti.
   - Monitoraggio di cialde, zucchero, bicchieri, ecc.
   - Segnalazione di guasti o necessità di manutenzione.

2. **Livello “gestionale” (Management)**: un applicativo accessibile via web permette ad amministratori e impiegati di:
   - Vedere l’elenco degli istituti e le macchine installate.
   - Aggiungere/rimuovere istituti e macchine (ruolo amministratore).
   - Monitorare lo stato di ogni macchina.
   - Inviare tecnici per risolvere guasti o eseguire manutenzione.
   - Tenere traccia dei ricavi generati dallo svuotamento delle casse.

L’architettura di riferimento prevede:
- **Backend in Java** che espone un’API REST.
- **Front-end web** (Web-App o SPA).
- **Broker MQTT** per le comunicazioni tra macchine IoT e backend.

---

## 2. Analisi Funzionale del Sistema

### 2.1 Funzionalità lato “campo” (macchina)

1. **Gestione del credito inserito dall’utente**  
   - Possibilità di inserire monete/banconote (simulate) in più tranche.
   - Controllo che la cassa abbia sufficiente capienza.
   - Rifiuto dell’inserimento se la cassa è prossima al limite.

2. **Scelta della bevanda e verifica disponibilità**  
   - Selezione della bevanda con eventuale dose di zucchero.
   - Verifica di credito sufficiente e presenza delle cialde richieste.
   - Erogazione rifiutata se le condizioni non sono soddisfatte.

3. **Erogazione della bevanda**  
   - Trattenuta del credito corrispondente e aggiornamento della cassa.
   - Riduzione delle quantità di cialde/bicchieri/zucchero (se gestite).

4. **Restituzione del credito non utilizzato**  
   - L’utente può rinunciare e recuperare il credito residuo.

5. **Segnalazione di eventi e malfunzionamenti**  
   - Notifica al sistema gestionale per fine cialde, cassa piena o guasti.

6. **Interazione col tecnico**  
   - Ricezione di un ordine di manutenzione via MQTT.
   - Aggiornamento dello stato dopo la riparazione o il rifornimento.

### 2.2 Funzionalità lato “gestionale” (management)

1. **Gestione istituti** (solo Admin)  
   - Creazione/rimozione istituti.
   - Visualizzazione elenco degli istituti.

2. **Gestione macchine**  
   - Installazione/rimozione macchine (solo Admin).
   - Visualizzazione stato (Admin e Impiegati).

3. **Monitoraggio e segnalazioni**  
   - Visualizzazione di guasti e necessità di manutenzione.
   - Dettagli su cialde esaurite, cassa piena, ecc.

4. **Invio tecnico per manutenzione**  
   - Possibilità di inviare un tecnico (Admin e Impiegati).
   - La macchina aggiorna lo stato dopo l’intervento.

5. **Gestione ricavi**  
   - Notifica e registrazione dello svuotamento della cassa.
   - Visualizzazione totale ricavi per macchina e istituto.

6. **Autenticazione e ruoli**  
   - **Amministratore**: pieno controllo.
   - **Impiegato**: consultazione dati e invio tecnico.
   - (Opzionale) OAUTH2 o username/password.

### 2.3 Requisiti tecnologici e vincoli

- **Backend in Java**: API REST, accesso DB via JDBC o JSON su file.
- **Front-end Web**: HTML/CSS/JavaScript o framework a scelta.
- **Broker MQTT** per comunicazioni IoT.
- **Sicurezza**: opzionale TLS per HTTP e MQTT. Eventuale OAuth2.

---

## 3. Scaletta di Priorità per lo Sviluppo

L’implementazione può seguire un approccio incrementale:

1. **Progettazione del dominio**  
   - Definizione delle entità (Macchina, Istituto, Utente, Cialda, ecc.).
   - Scelta dell’archiviazione (DB relazionale o JSON).

2. **Implementazione del Backend “management”** (API REST) – Livello base  
   - Endpoints CRUD per istituti e macchine.
   - Gestione utenti e ruoli di base.

3. **Implementazione della logica di gestione “campo”**  
   - Modello dati/servizi per credito, cassa, cialde e guasti.
   - Pubblicazione segnalazioni guasti/ricariche via MQTT.

4. **Integrazione REST ↔ MQTT**  
   - Il backend riceve notifiche dalle macchine via MQTT.
   - Pubblica comandi di intervento (manutenzione, svuotamento cassa).

5. **Gestione ricavi e manutenzione**  
   - Endpoint per svuotare cassa e aggiornare ricavi.
   - Stato manutentivo e guasti risolti.

6. **Front-end Web**  
   - Login e gestione ruoli.
   - Visualizzazione istituti, macchine, stato e segnalazioni.
   - Azioni (aggiungi/rimuovi istituto, invia tecnico, svuota cassa).

7. **Funzionalità avanzate e rifiniture**  
   - TLS (HTTPS e MQTT).
   - Autenticazione via OAuth2.
   - Microservizi interni più complessi.
   - Gestione bevande più articolate.

---

## 4. Considerazioni Finali

- **MVP**: Realizzare innanzitutto una configurazione minima e funzionante (una o poche macchine, interfaccia essenziale, segnalazioni di base).
- **Scalabilità**: Estendere poi con ruoli multipli, più istituti, gestione di cialde multiple e altre features avanzate.
- **Tecnologie consigliate**:
  - **Java (Spring Boot)** per il backend.
  - **Database** (MySQL, PostgreSQL) o file JSON.
  - **Broker MQTT** (es. Mosquitto).
  - **Front-end** con un framework a scelta (React, Angular, Vue, o JSP/Thymeleaf).

Questo approccio iterativo consente di validare gradualmente ogni parte del sistema, partendo dalle basi (CRUD, domain model) fino a integrare la parte IoT e le funzionalità più avanzate di sicurezza e comunicazione.
