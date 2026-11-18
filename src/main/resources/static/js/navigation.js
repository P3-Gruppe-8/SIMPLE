// Mode switching, view history and login state.

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
document.getElementById("devButton").addEventListener("click", showDevView);

// tapping the logo reveals the hidden dev button for 2 seconds
const DEV_BUTTON_VISIBLE_MS = 2000;
let devButtonTimer;
document.getElementById("logo").addEventListener("click", () => {
    if (viewHistory.at(-1) === "devView") {
        return;
    }
    showById("devButton");
    clearTimeout(devButtonTimer);
    devButtonTimer = setTimeout(
        () => hideById("devButton"),
        DEV_BUTTON_VISIBLE_MS,
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

let loggedInAs = null;
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
        if (loggedInAs) {
            showById("moreOptions");
            hideById("scanIdPrompt");
        }
    } else if (id === "loanMode") {
        if (loggedInAs) {
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
    if (isRootView && loggedInAs) {
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
    if (viewHistory.at(-1) === "devView") {
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

// the dev view lives outside the modes, so the current mode is hidden while it is open
export function showDevView() {
    if (viewHistory.at(-1) === "devView") {
        return;
    }
    hideById(currentMode);
    showView("devView");
    hideById("devButton");
    hideById("alertButton");
    hideById("modeSwitch");
}

export function logIn(staffId) {
    const currentView = viewHistory.at(-1);
    if (!["defaultView", "loanModeLogInScreen"].includes(currentView)) return;
    loggedInAs = staffId;
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
    loggedInAs = null;
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
    if (currentView === "loanModeHomeScreen") {
        showView("loanModeLogInScreen");
    }
}
