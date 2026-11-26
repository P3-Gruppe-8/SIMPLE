// Mode switching and view history.
import { checkStaff, writeOff } from "./api.js";
import { endSession, isLoggedIn, startSession } from "./session.js";
import { showErrorToast, showToast } from "./toast.js";
import { loadOffices } from "./debug.js";

const modeSwitch = document.getElementById("modeSwitch");
const modeOptions = document.querySelectorAll(".mode-option");

modeOptions.forEach((button) => {
    button.addEventListener("click", () => {
        modeOptions.forEach((option) => {
            option.classList.remove("active");
        });

        button.classList.add("active");

        if (button.dataset.mode === "loanMode") {
            modeSwitch.classList.add("loan");
        } else {
            modeSwitch.classList.remove("loan");
        }

        console.log(button.dataset.mode);
        setMode(button.dataset.mode);
    });
});

document.querySelectorAll("[data-view]").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.view));
});

document.getElementById("backButton").addEventListener("click", previousView);
document.getElementById("logoutButton").addEventListener("click", logOut);
document
    .getElementById("alertButton")
    .addEventListener("click", () => showAlertView("alertView"));
document.getElementById("debugButton").addEventListener("click", showDebugView);

// tapping the logo reveals the hidden debug button for 2 seconds
const DEBUG_BUTTON_VISIBLE_MS = 2000;
let debugButtonTimer;
document.getElementById("logo").addEventListener("click", () => {
    if (viewHistory.at(-1) === "debugView") {
        return;
    }
    showById("debugButton");
    clearTimeout(debugButtonTimer);
    debugButtonTimer = setTimeout(
        () => hideById("debugButton"),
        DEBUG_BUTTON_VISIBLE_MS,
    );
});

export function hide(element) {
    element.classList.add("invisible");
}

export function show(element) {
    element.classList.remove("invisible");
}

export function hideById(id) {
    let element = document.getElementById(id);
    hide(element);
}

export function showById(id) {
    let element = document.getElementById(id);
    show(element);
}

let currentMode = null;

// the first view of each mode is a "root view"
const rootViews = ["defaultView", "loanModeLogInScreen", "loanModeHomeScreen"];
let viewHistory = [];
setMode("supplyMode");

export function setMode(id) {
    let modes = document.getElementsByClassName("mode");
    for (const mode of modes) {
        hide(mode);
    }
    showById(id);
    currentMode = id;
    if (id === "supplyMode") {
        showView("defaultView");
        if (isLoggedIn()) {
            showById("moreOptions");
            hideById("scanIdPrompt");
        }
    } else if (id === "loanMode") {
        if (isLoggedIn()) {
            showView("loanModeHomeScreen");
        } else {
            showView("loanModeLogInScreen");
        }
    }
}

export function showView(id) {
    let isRootView = rootViews.includes(id);
    if (isRootView) {
        viewHistory.length = 0; // start over
    }
    viewHistory.push(id);
    let views = document.getElementsByClassName("view");
    for (const view of views) {
        hide(view);
    }
    showById(id);
    if (isRootView) {
        hideById("backButton");
    } else {
        showById("backButton");
    }
    if (isRootView && isLoggedIn()) {
        showById("logoutButton");
    } else {
        hideById("logoutButton");
    }
}

export function previousView() {
    if (viewHistory.at(-1) === "alertView") {
        showById("alertButton");
        showById("modeSwitch");
    }
    if (viewHistory.at(-1) === "debugView") {
        showById(currentMode);
        showById("alertButton");
        showById("modeSwitch");
    }

    viewHistory.pop(); // the view we are leaving
    let previousViewId = viewHistory.pop(); // showView pushes it back on
    showView(previousViewId);
}

export function showAlertView(alertView) {
    showView(alertView);
    hideById("alertButton");
    hideById("modeSwitch");
}

// the debug view lives outside the modes, so the current mode is hidden while it is open
export function showDebugView() {
    if (viewHistory.at(-1) === "debugView") {
        return;
    }
    hideById(currentMode);
    showView("debugView");
    loadOffices();
    hideById("debugButton");
    hideById("alertButton");
    hideById("modeSwitch");
}

// throws if the server doesn't know the username
export async function logIn(username) {
    const currentView = viewHistory.at(-1);
    if (!["defaultView", "loanModeLogInScreen"].includes(currentView)) return;
    await checkStaff(username);
    startSession(username);
    if (currentView === "defaultView") {
        hideById("scanIdPrompt");
        showById("moreOptions");
        showById("logoutButton");
    } else if (currentView === "loanModeLogInScreen") {
        showView("loanModeHomeScreen");
    }
}

export function logOut() {
    const currentView = viewHistory.at(-1);
    endSession();
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
    if (currentView === "loanModeHomeScreen") {
        showView("loanModeLogInScreen");
    }
}

export function quickWriteOff(barcode) {
    if (viewHistory.at(-1) !== "defaultView") return;
    writeOff(barcode, 1)
        .then((text) => showToast(text))
        .catch((error) => {
            console.error(error);
            showErrorToast(error.message);
        });
}
