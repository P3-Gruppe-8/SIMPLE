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

// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 20;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";

window.addEventListener("keydown", (e) => {
    const currentTime = Date.now();
    if (currentTime - lastKeyTime > SCAN_KEY_GAP_MS) {
        buffer = "";
    }
    lastKeyTime = currentTime;
    if (e.key === "Enter" && buffer.length >= MIN_BARCODE_LENGTH) {
        e.preventDefault();
        onScan(buffer);
        buffer = "";
        return;
    }
    if (e.key.length === 1) {
        buffer += e.key;
    }
});

const validIds = ["5741000134894", "5741000142899"]; // booster med sukker, booster uden sukker.....

function onScan(buffer) {
    if (validIds.includes(buffer)) {
        logIn(buffer);
    }
}
// Alerts ---------------------
let showNewAlerts = true;
let currentAlertsArray = [];

function showAlertView(alertView, showBack) {
    showView(alertView, showBack);
    hideById("alertButton");
    hideById("newAlertsHeader");
    hideById("newAlertsRow");
    hideById("newAlertsFooter");
    hideById("oldAlertsRow");

    const headerLabel = document.getElementById("newAlertsHeaderLabel");
    if (currentAlertsArray.length === 0) {
        showById("newAlertsHeader");
        headerLabel.textContent = "NO CURRENT ALERTS";
        return;
    }

    headerLabel.textContent = "NEW";

    let hasOldAlerts = hasOlderAlerts();

    if (hasOldAlerts) {
        showById("oldAlertsRow");
    }

    if (!showNewAlerts) {
        return;
    }

    showById("newAlertsHeader");
    showById("newAlertsRow");

    if (!hasOldAlerts) {
        showNewAlerts = false;
        return;
    }

    showById("newAlertsFooter");
    showNewAlerts = false;
}

function hasOlderAlerts() {
    let hasOldAlerts = false;

    for (let alert in currentAlertsArray) {
        if (currentAlertsArray[alert].isNewAlert === "false") {
            hasOldAlerts = true;
            return hasOldAlerts;
        }
    }
    return hasOldAlerts;
}

/*

let alert = {
    alertType: "stock" or "loan" or "inactivity",
    urgency: "info" or "warning" or "critical",
    alertTextContent: "Write off has not been used in 1 week",
    newAlert: "true" or "false",
}
    */

let dummyAlertArray = [
    {
        alertType: "stock",
        urgency: "info",
        alertTextContent: "Printer Catridge quantity low (20)",
        isNewAlert: "true",
    },
    {
        alertType: "stock",
        urgency: "warning",
        alertTextContent: "Printer Catridge quantity very low (10)",
        isNewAlert: "true",
    },
    {
        alertType: "stock",
        urgency: "critical",
        alertTextContent: "Printer Catridge quantity too low (5)",
        isNewAlert: "true",
    },
    {
        alertType: "loan",
        urgency: "info",
        alertTextContent: "Loan by LTB of Laptop 5 ends tomorrow",
        isNewAlert: "true",
    },
    {
        alertType: "loan",
        urgency: "warning",
        alertTextContent:
            "Loan by LTB of Laptop 5 has not been returned, loan ended yesterday",
        isNewAlert: "true",
    },
    {
        alertType: "loan",
        urgency: "critical",
        alertTextContent:
            "Loan by LTB of Laptop 5 has not been returned, loan ended 1 week ago",
        isNewAlert: "true",
    },
    {
        alertType: "inactivity",
        urgency: "info",
        alertTextContent: "Write off has not been used in 1 day(s)",
        isNewAlert: "true",
    },
    {
        alertType: "inactivity",
        urgency: "warning",
        alertTextContent: "Write off has not been used in 3 day(s)",
        isNewAlert: "true",
    },
    {
        alertType: "inactivity",
        urgency: "critical",
        alertTextContent: "Write off has not been used in 1 week(s)",
        isNewAlert: "true",
    },
];

updateAlerts(dummyAlertArray);

function recieveAlerts(SSEAlertArray) {
    // function to handle alerts sent with SSE
}

function updateAlerts(alertArray) {
    removeAlerts();
    const sortedAlertArray = sortAlerts(alertArray);
    buildAlerts(sortedAlertArray);
    currentAlertsArray = sortedAlertArray;
}

function removeAlerts() {
    // removes all alertboxes in order to rebuild them
    const newAlertsRow = document.getElementById("newAlertsRow");
    const oldAlertsRow = document.getElementById("oldAlertsRow");

    while (newAlertsRow.hasChildNodes()) {
        newAlertsRow.removeChild(newAlertsRow.firstChild);
    }

    while (oldAlertsRow.hasChildNodes()) {
        oldAlertsRow.removeChild(oldAlertsRow.firstChild);
    }
}

function sortAlerts(alertArray) {
    // sorts alerts each time alerts are updated
    return alertArray;
}

function buildAlerts(sortedAlertArray) {
    for (let alert in sortedAlertArray) {
        const alertType = sortedAlertArray[alert].alertType;
        if (alertType === "stock") {
            continue;
        }

        if (alertType === "loan") {
            continue;
        }

        if (alertType !== "inactivity") {
            throw new Error("alertType is not recognized");
        }
    }
}

function buildStockAlert() {}

function buildLoanAlert() {}

function buildInactivityAlert() {}
