// Toast: a short notification in the bottom right corner.

const TOAST_DURATION_MS = 5000;
const MAX_TOASTS = 6;

const toasts = document.getElementById("toasts");
const toastTemplate = document.getElementById("toast-template");

export function showToast(message) {
    const toast = toastTemplate.content.firstElementChild.cloneNode(true);
    toast.querySelector(".toast-message").textContent = message;
    toast.addEventListener("click", () => toast.remove());
    setTimeout(() => toast.remove(), TOAST_DURATION_MS);
    toasts.append(toast);

    // drop the oldest so a burst of scans can't fill the screen
    if (toasts.children.length > MAX_TOASTS) {
        toasts.firstElementChild.remove();
    }
}
