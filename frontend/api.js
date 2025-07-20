"use strict";

class Api {

    /**
     * Perform the login
     */
    static doLogin = async (username, password) => {
        let response = await fetch('/http://localhost:8000/api/sessions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        });
        if (response.ok) {
            const result = await response.json();
            return result;
        }
        else {
            try {
                const errDetail = await response.json();
                throw errDetail.message;
            }
            catch (err) {
                throw err;
            }
        }
    }

    static doSignUp = async (user) => {
        let response = await fetch('/api/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ user }),
        });
        if (response.ok) {
            const userJson = await response.json();
            return userJson;
        } else {
            try {
                const errDetail = await response.json();
                if (errDetail.errors) {
                    throw errDetail.errors;
                } else {
                    throw new Error(errDetail.message);
                }
            }
            catch (err) {
                throw err;
            }
        }
    }

    static loginWithKeycloak = async (keycloakPayload) => {
        let response = await fetch('/api/keycloak/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(keycloakPayload),
        });
        if (response.ok) {
            const result = await response.json();
            return { success: true, user: result.user };
        }
        else {
            try {
                const errDetail = await response.json();
                throw errDetail.message;
            } catch (err) {
                throw err;
            }
        }

    }

    static registerWithKeycloak = async (keycloakPayload) => {
        let response = await fetch('/api/keycloak/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(keycloakPayload),
        });
        if (response.ok) {
            const result = await response.json();
            return { success: true, message: result.message };
        } else {
            try {
                const errDetail = await response.json();
                throw errDetail.message;
            } catch (err) {
                throw err;
            }
        }
    }

    
}

export default Api;