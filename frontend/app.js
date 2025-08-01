"use strict";

import Api from './api.js';
import { createLoginForm } from './templates/login-template.js';
import { createSignUpForm } from './templates/sign-template.js';
import { createHomeForm } from './templates/home-template.js';
import {
    navbarUserPage, createUserPage, createWishlistPage, createCard, createHistoryPurchasePage,
    createCardPurchase, createTablePurchase, createTotalRow, createHistoryCommentsPage, cardShowCommentsUser
} from './templates/user-template.js';
import { createStoreTable, createStoreCard, createCartCard, addFollowButton, removeFollowButton, addPubIcon, addPrvIcon } from './templates/store-template.js';
import { navbarAdminPage, createAdminProfile, createUsersPage, createItemsPage, loadUsers, loadItems, cardShowItems } from './templates/admin-template.js';
import { createSearchItemTable, createSearchCommentTable, cardShowComment, createSearchItemCard } from './templates/search-template.js';
import { createContactForm } from './templates/contact-template.js';
import { createPricingForm } from './templates/pricing-template.js';
import { createAlert } from './templates/alert-template.js';
//import { initKeycloak, sendRequest, logout } from './auth.js';
import page from "//unpkg.com/page/page.mjs";

class App {

    /**
     * Constructor of the App class.
     * @param {HTMLElement} appContainer - The element that will contain the application.
     */
    constructor(appContainer) {
        // reference to the the item container (HTML element)
        this.appContainer = appContainer;
        this.logoutLink = document.querySelector('#logout');
        this.loginLink = document.querySelector('#login');
        this.loggedUser = null;
        this.itemCart = [];

        const kc = new Keycloak({
            url: 'http://localhost:8080/auth',
            realm: 'CoffeeMachineRealm',
            clientId: 'myclient',
          });

        // client-side routing with Page.js
        page('/login', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser == null) {
                this.appContainer.innerHTML = "";
                this.appContainer.innerHTML = createLoginForm();
                document.getElementById('login-form').addEventListener('submit', this.onLoginSubmitted);
            }
        });

        page('/signUp', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser == null) {
                this.appContainer.innerHTML = "";
                this.appContainer.innerHTML = createSignUpForm();
                document.getElementById('signUp-form').addEventListener('submit', this.onSignUpSubmitted);
            }
        });

        page('/logout', this.logout);

        page('/userPage', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = "";
                this.appContainer.innerHTML = this.userPersonalPage();
            }
        });

        page('/wishlist', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = this.userWishListPage();
            }
        });

        page('/history', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = this.historyPurchasePage();
            }
        });

        page('/historyComments', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = this.createHistoryComments();
            }
        });

        page('/users', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = this.adminUsersPage();
            }
        });

        page('/items', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.appContainer.innerHTML = this.adminItemsPage();
            }
        });

        page('/store', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.renderNavBar(this.loggedUser.name);
            }
            this.appContainer.innerHTML = "";
            this.appContainer.innerHTML = this.showStore();

        });

        page('/contact', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.renderNavBar(this.loggedUser.name);
            }
            this.appContainer.innerHTML = "";
            this.appContainer.innerHTML = createContactForm();

        });

        page('/pricing', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.renderNavBar(this.loggedUser.name);
            }
            this.appContainer.innerHTML = "";
            this.appContainer.innerHTML = createPricingForm();

        });

        page('/', () => {
            this.loggedUser = JSON.parse(localStorage.getItem('user'));
            if (this.loggedUser != null) {
                this.renderNavBar(this.loggedUser.name);
            }
            this.appContainer.innerHTML = "";
            this.appContainer.innerHTML = createHomeForm();

        });

        this.activeLinkNavbar();
        document.querySelector('.btn-search-item').addEventListener('click', this.searchItems);
        document.querySelector('.btn-search-comment').addEventListener('click', this.searchComments);

        // very simple itemple of how to handle a 404 Page Not Found 
        page('*', () => this.appContainer.innerHTML = 'Page not found!');

        page();
    }

    onLoginSubmitted = async (event) => {
        event.preventDefault();
      
        try {
          await kc.init({ onLoad: 'login-required' });
      
          // Salva i dati dell’utente
          localStorage.setItem('user', JSON.stringify({
            token: kc.token,
            name: kc.idTokenParsed?.preferred_username || "Utente",
            roles: kc.tokenParsed.realm_access.roles,
          }));
      
          page.redirect('/userPage');
        } catch (error) {
          console.error("Errore login", error);
          alert("Errore durante il login");
        }
    }
      



}

export default App;