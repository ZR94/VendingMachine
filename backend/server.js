"use strict";

var express = require('express');
var session = require('express-session');
var Keycloak = require('keycloak-connect');
const cors = require('cors');
const path = require('path');
const FileStore = require('session-file-store')(session);
//const dao = require('./dao.js');
//const jwt = require('jsonwebtoken');
const jwtDecode = require('jwt-decode');


var app = express();
const port = 3000;

app.use(express.json());

var memoryStore = new session.MemoryStore();

app.use(session({
  store: new FileStore({ path: path.resolve(__dirname, 'sessions') }),
  secret: 'a secret sentence not to share with anybody and anywhere, used to sign the session ID cookie',
  resave: false,
  saveUninitialized: false,
  // removing the following line will cause a browser's warning, since session cookie
  // cross-site default policy is currently not recommended
  cookie: { sameSite: 'lax' }
}));

var keycloak = new Keycloak({ store: memoryStore });
var stringReplace = require('string-replace-middleware');
var KC_URL = process.env.KC_URL || "http://localhost:8080/auth";
var SERVICE_URL = process.env.SERVICE_URL || "http://localhost:3000/secured";

app.use(stringReplace({
   'SERVICE_URL': SERVICE_URL,
   'KC_URL': KC_URL
}));
app.use(express.static(path.join(__dirname, '../frontend')));
app.use(keycloak.middleware());

// === REST API === //

app.get('/secured', keycloak.protect('realm:Administrator'), function (req, res) {
  res.setHeader('content-type', 'text/plain');
  res.send('Secret message!');
});

app.get('/public', function (req, res) {
  res.setHeader('content-type', 'text/plain');
  res.send('Public message!');
});
 
app.get('/', function(req, res) {
  res.sendFile(path.join(__dirname, 'public/index.html'));
});

app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  try {
    // Richiedi token all'endpoint token di Keycloak
    const tokenRes = await fetch(`${KC_URL}/realms/CoffeeMachineRealm/protocol/openid-connect/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: qs.stringify({
        client_id: 'myClient', // cambia con il tuo client
        grant_type: 'password',
        username,
        password
      })
    });

    if (!tokenRes.ok) {
      const err = await tokenRes.json();
      return res.status(401).json({ message: 'Credenziali non valide', detail: err });
    }

    const tokenData = await tokenRes.json(); // contiene access_token

    // Decodifica token per ottenere i ruoli
    const decoded = jwtDecode(tokenData.access_token);
    const roles = decoded.realm_access?.roles || [];

    res.json({
      accessToken: tokenData.access_token,
      roles: roles
    });

  } catch (error) {
    console.error('Errore login:', error);
    res.status(500).json({ message: 'Errore interno' });
  }
});

app.listen(port, () => console.log(`server listening at http://localhost:${port}`));

