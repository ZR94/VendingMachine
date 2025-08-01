// auth/keycloak.js
const SERVICE_URL = "http://localhost:3000"; // Base URL del tuo backend

// Inizializza Keycloak
const kc = new Keycloak({
    realm: 'CoffeeMachineRealm',
    clientId: 'myclient',
    url: 'http://localhost:8080/auth' // URL del server Keycloak
});

// Funzione per inizializzare Keycloak e gestire il login
async function initKeycloak() {
    try {
        await kc.init({ onLoad: 'login-required' }); // Forza il login
        console.log('Utente autenticato:', kc.token);

        // Estrai dati dal token
        const userData = kc.idTokenParsed;
        console.log('Dati utente:', userData);


    } catch (error) {
        console.error('Errore durante l\'inizializzazione di Keycloak:', error);
    }
}

// Funzione per inviare richieste agli endpoint protetti
async function sendRequest(endpoint) {
    if (!kc.token) {
        console.error('Token non disponibile.');
        return;
    }

    try {
        const response = await fetch(`${SERVICE_URL}${endpoint}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${kc.token}`,
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Risposta:', data);
            return data;
        } else {
            console.error('Errore nella richiesta:', response.status);
        }
    } catch (error) {
        console.error('Errore di rete:', error);
    }
}

// Funzione per il logout
function logout() {
    kc.logout();
}

// Esporta le funzioni per usarle in altri file
export { initKeycloak, sendRequest, logout };

