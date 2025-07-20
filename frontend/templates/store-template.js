"use strict";

function createStoreTable() {
    return `
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar a sinistra -->
            <aside class="col-sm-4 col-12 bg-light p-3" id="left-sidebar">
                <div class="mb-3">
                    <button class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasScrolling" 
                    data-bs-target="#offcanvasRight" aria-controls="offcanvasScrolling">Cart</button>
                </div> 
                <div class="list-group list-group-flush border" >
                    <a href="#" data-id="all" class="list-group-item list-group-item-action active" aria-current="true">
                        <img src="./svg/list.svg" alt="tutti gli items nella wishlist"> All</a>
                    <a href="#" data-id="favourities" class="list-group-item list-group-item-action">
                        <img src="./svg/exclamation-circle.svg" alt="Favourities"> Favourities</a>
                    <a href="#" data-id="private" class="list-group-item list-group-item-action">
                        <img src="./svg/eye-slash.svg" alt="visibilità privata"> Private</a>
                    <a href="#" data-id="public" class="list-group-item list-group-item-action">
                        <img src="./svg/eye.svg" alt="visibilità pubblica"> Pubblic</a>
                </div>
                <div class="list-group list-group-flush border mt-2">
                    <span id="category" class="list-group-item dropdown">
                        <div class="dropdown">
                            <span class="dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                <img src="./svg/filter.svg" alt="filtro per catagoria"> Categories
                            </span>
                            <ul class="dropdown-menu">
                                <!-- le varie voci (categorie) sono inserite da JS -->
                            </ul>
                        </div> 
                    </span>
                </div>

            </aside>

            <!-- Contenuto principale -->
            <div class="col-sm-8 col-12">
                <!-- Titolo Store -->
                <h2 class="my-4 text-center custom-title">FAST LEAN FIT GEAR.</h2>

                <div class="row row-cols-1 row-cols-md-3 g-4" id="my-items"></div>
                
                <div class="listProduct">
                    <div class="offcanvas offcanvas-end" data-bs-scroll="true" data-bs-backdrop="false" 
                        tabindex="-1" id="offcanvasScrolling" aria-labelledby="offcanvasScrollingLabel">
                        <div class="offcanvas-header">
                            <h5 class="offcanvas-title" id="offcanvasScrollingLabel">List cart</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                        </div>
                        <div class="" id="checkout-form"> 

                            <!-- Contenitore scrollabile per gli articoli -->
                            <div class="offcanvas-body listCart"></div>
                            
                            <!-- Contenitore fisso per il pulsante di checkout -->
                            <div class="checkout-container">
                                <div class="d-flex justify-content-center mb-3" id="checkoutBox">
                                    <button class="btn btn-primary btn-checkout" type="button" value="">Checkout</button>
                                </div>
                            </div>
        
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `;
}

function createStoreCard(item) {
    return `
    <div class="col">
        <div class="card h-100" id="${item.id}">
                <img src="${item.img}" class="card-img-top" alt="Generic placeholder image">
            <div class="card-body">
                <h5 class="card-title">${item.name}</h5>
                <p class="card-text"></p>
            </div>
            <div class="card-footer-price" id= "product-card-follow-${item.id}">
                <p> <small class="text-body-secondary">Price: $ ${item.price.toFixed(2)}</small> </p>
                
            </div>

            <div class="card-footer" id= "product-card-${item.id}">

                <button class="btn btn-primary btn-add" type="button" value="${item.id}">Add</button>
                <button type="button" class="btn btn-primary btn-comment-add" data-bs-toggle="modal" data-bs-target="#commentModal-${item.id}" data-bs-whatever="comment-${item.id}" id="comment-${item.id}" value="${item.id}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus-circle" viewBox="0 0 16 16">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                        <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/>
                    </svg> comment
                </button>
                

            </div>

        </div>

        <!-- Modal Comment-->
        <div class="modal fade" id="commentModal-${item.id}" tabindex="-1" aria-labelledby="commentModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5 text-dark" id="commentModalLabel">New comment</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                <div class="modal-body">
                    <form>
                        <div class="mb-3">
                            <label for="message-text" class="col-form-label">Comment:</label>
                            <textarea class="form-control" id="message-text-${item.id}"></textarea>
                        </div>
                    </form>
                </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary btn-saveComment" id="saveComment" data-bs-dismiss="modal">Save comment</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Wishlist-->
        <div class="modal fade" id="staticBackdrop-${item.id}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5 text-dark" id="staticBackdropLabel">Choose visibility</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="visibilityOptions" id="privateOption" value="0">
                            <label class="form-check-label" for="privateOption">
                                Private
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="visibilityOptions" id="publicOption" value="1" checked>
                            <label class="form-check-label" for="publicOption">
                                Public
                            </label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary btn-saveChoice" id="saveChoice" data-bs-dismiss="modal">Save choice</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `;
}

function createCartCard(item, quantity) {
    return `
    <div class="d-flex align-items-center mb-5 cartCard">
        <div class="flex-shrink-0">
        <img src="${item.img}" class="img-fluid cart-img" alt="Generic placeholder image">
            <p class="align-items-center fw-bold">$ ${item.price.toFixed(2)}</p>
        </div>
        <div class="float">
            <h5 class="align-items-center fw-bold">${item.name}</h5>
            <div class="d-flex flex-sm-column mb-3">
                <div class="def-number-input number-input safari_only">
                    <button name="-" value="${item.id}" class="minus btn btn-primary btn-sm btnQnt"> - </button>
                    <span class="value">${quantity}</span>
                    <button name="+" value="${item.id}" class="plus btn btn-primary btn-sm btnQnt"> + </button>
                </div>
            </div>
        </div>
    </div>
    `;
}

function addFollowButton(itemId) {
    return `
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-primary btn-favourite-add" data-bs-toggle="modal" data-bs-target="#staticBackdrop-${itemId}" id="follow-${itemId}" value="${itemId}">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-heart" viewBox="0 0 16 16" value="${itemId}">
            <path d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"></path>
        </svg>
    </button>

    `;
}

function removeFollowButton(itemId) {
    return `
    <button type="button" class="btn btn-primary btn-favourite-remove" id="unfollow-${itemId}" value="${itemId}">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-heart-fill" viewBox="0 0 16 16" value="${itemId}">
            <path fill-rule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314"></path>
        </svg>
    </button>
    `;
}

function addPubIcon() {
    return `
    <img src='./svg/eye.svg' alt='visibilità pubblica'>
    `;
}

function addPrvIcon() {
    return `
    <img src='./svg/eye-slash.svg' alt='visibilità pubblica'>
    `;
}

export { createStoreTable, createStoreCard, createCartCard, addFollowButton, removeFollowButton, addPubIcon, addPrvIcon};