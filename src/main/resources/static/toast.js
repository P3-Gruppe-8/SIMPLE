// Toast: a short notification in the bottom right corner. Depends on navigation.js.

const TOAST_DURATION_MS = 5000;

const toast = document.getElementById("toast");
const toastMessage = document.getElementById("toast-message");
let toastTimeout;

function showToast(message) {
    toastMessage.textContent = message;
    show(toast);
    // a new toast replaces the old one and restarts the timer
    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(hideToast, TOAST_DURATION_MS);
}

function hideToast() {
    clearTimeout(toastTimeout);
    hide(toast);
}

toast.addEventListener("click", hideToast);
