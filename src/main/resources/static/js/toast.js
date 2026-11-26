// Toast: a short notification in the bottom right corner.

const TOAST_DURATION_MS = 5000;
const MAX_TOASTS = 6;

const toasts = document.getElementById("toasts");
const toastTemplate = document.getElementById("toast-template");

export function showToast(message, isError = false) {
    // repeating the newest toast (e.g. quick write-offs) counts up instead of stacking copies
    const newest = toasts.lastElementChild;
    if (
        newest?.querySelector(".toast-message").textContent === message &&
        newest.classList.contains("toast-error") === isError
    ) {
        newest.dataset.count = Number(newest.dataset.count) + 1;
        newest.querySelector(".toast-count").textContent =
            "×" + newest.dataset.count;
        removeLater(newest);
        return;
    }

    const toast = toastTemplate.content.firstElementChild.cloneNode(true);
    toast.querySelector(".toast-message").textContent = message;
    if (isError) {
        toast.classList.add("toast-error");
        toast.querySelector(".toast-icon").src =
            "images/circle-info-solid-full.svg";
    }
    toast.dataset.count = 1;
    toast.addEventListener("click", () => toast.remove());
    removeLater(toast);
    toasts.append(toast);

    // drop the oldest so a burst of scans can't fill the screen
    if (toasts.children.length > MAX_TOASTS) {
        toasts.firstElementChild.remove();
    }
}

export function showErrorToast(message) {
    showToast(message, true);
}

// (re)starts the countdown, so a toast that just counted up stays for the full duration
function removeLater(toast) {
    clearTimeout(Number(toast.dataset.timer));
    toast.dataset.timer = setTimeout(() => toast.remove(), TOAST_DURATION_MS);
}
