// Mode switching, view history and login state. Depends on nothing.

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

function hide(element) {
    element.classList.add("invisible");
}

function show(element) {
    element.classList.remove("invisible");
}

function hideById(id) {
    let element = document.getElementById(id);
    hide(element);
}

function showById(id) {
    let element = document.getElementById(id);
    show(element);
}

let loggedInAs = null;

// the first view of each mode is a "root view"
const rootViews = ["defaultView", "loanModeLogInScreen", "loanModeHomeScreen"];
let viewHistory = [];
setMode("supplyMode");

function setMode(id) {
    let modes = document.getElementsByClassName("mode");
    for (const mode of modes) {
        hide(mode);
    }
    showById(id);
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

function showView(id) {
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

function previousView() {
    if (viewHistory.at(-1) === "alertView") {
        showById("alertButton");
        showById("modeSwitch");
    }

    viewHistory.pop(); // the view we are leaving
    let previousViewId = viewHistory.pop(); // showView pushes it back on
    showView(previousViewId);
}

function showAlertView(alertView) {
    showView(alertView);
    hideById("alertButton");
    hideById("modeSwitch");
}

function logIn(staffId) {
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

function logOut() {
    const currentView = viewHistory.at(-1);
    loggedInAs = null;
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
    if (currentView === "loanModeHomeScreen") {
        showView("loanModeLogInScreen");
    }
}
