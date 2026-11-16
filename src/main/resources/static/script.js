const modeSwitch = document.getElementById("modeSwitch");
const modeOptions = document.querySelectorAll(".mode-option");

let showNewAlerts = true;
let alertTestVariable = false;

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

const validIds = ["5741000134894"];

function onScan(buffer) {
    if (validIds.includes(buffer)) {
        logIn(buffer);
    }
}
// Alerts ---------------------
let currentTime = new Date().toString();

let dummyAlertArray = [
    {
        alertType: "stockAlert",
        urgency: "INFO",
        descriptor: "Printer_Cartridge",
        minQuantity: 10,
        currQuantity: 30,
    },
    {
        alertType: "stockAlert",
        urgency: "WARNING",
        descriptor: "Printer_Cartridge",
        minQuantity: 10,
        currQuantity: 20,
    },
    {
        alertType: "stockAlert",
        urgency: "CRITICAL",
        descriptor: "Printer_Cartridge",
        minQuantity: 10,
        currQuantity: 5,
    },
    {
        alertType: "loanAlert",
        urgency: "INFO",
        returnDate: "Today",
        currentDate: currentTime,
        loanable: "Laptop 5",
        borrower: "LTB",
    },
    {
        alertType: "loanAlert",
        urgency: "WARNING",
        returnDate: "Yesterday",
        currentDate: currentTime,
        loanable: "Laptop 5",
        borrower: "LTB",
    },
    {
        alertType: "loanAlert",
        urgency: "CRITICAL",
        returnDate: "1 week ago",
        currentDate: currentTime,
        loanable: "Laptop 5",
        borrower: "LTB",
    },
    {
        alertType: "inactivityAlert",
        urgency: "INFO",
        alertInfo: "Write off has not been used in 3 days",
    },
    {
        alertType: "inactivityAlert",
        urgency: "WARNING",
        alertInfo: "Write off has not been used in 1 week",
    },
    {
        alertType: "inactivityAlert",
        urgency: "CRITICAL",
        alertInfo: "Write off has not been used in 3 weeks",
    },
];

function showAlertView(alertView) {
    showView(alertView);
    hideById("alertButton");
    hideById("modeSwitch");

    if (!showNewAlerts) {
    }
}

function recieveAlerts(alertArray) {
    alertViewVisited = false;
}
