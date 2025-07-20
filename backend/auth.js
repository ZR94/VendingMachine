// auth/keycloak.js
import KcAdminClient from 'keycloak-admin';
import dotenv from 'dotenv';
dotenv.config();

const kcAdminClient = new KcAdminClient();

await kcAdminClient.auth({
  username: process.env.KEYCLOAK_ADMIN_USER,
  password: process.env.KEYCLOAK_ADMIN_PASSWORD,
  grantType: 'password',
  clientId: 'admin-cli',
});

kcAdminClient.setConfig({
  baseUrl: 'http://localhost:8080/auth',
  realmName: 'CoffeeMachineRealm',
});

export default kcAdminClient;

