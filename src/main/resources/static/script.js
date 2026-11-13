const modeSwitch = document.getElementById("modeSwitch");
const modeOptions = document.querySelectorAll(".mode-option");
const alertButton = document.getElementById;

modeOptions.forEach((button) => {
    button.addEventListener("click", () => {
        modeOptions.forEach((option) => {
            option.classList.remove("active");
        });

        button.classList.add("active");

        if (button.dataset.mode === "loan") {
            modeSwitch.classList.add("loan");
        } else {
            modeSwitch.classList.remove("loan");
        }

        console.log("Selected mode:", button.dataset.mode);
    });
});

function hideById(id) {
    let element = document.getElementById(id);
    element.classList.add("invisible");
}

function showById(id) {
    let element = document.getElementById(id);
    element.classList.remove("invisible");
}

let viewHistory = [];
showView("defaultView", false);

function showView(id, showBack) {
    viewHistory.push(id);
    let views = document.getElementsByClassName("view");
    for (const view of views) {
        view.classList.add("invisible");
    }
    showById(id);
    hideById("logoutButton");
    if (showBack) {
        showById("backButton");
    } else {
        hideById("backButton");
    }
    document.getElementById(id).style = "display: flex;";

    // Alert view functionality
}

function hideBack() {
    document.getElementById("backButton").style = "display: none;";
}

function showBack() {
    document.getElementById("backButton").style = "display: flex;";
}

function hideBack() {
    document.getElementById("backButton").style = "display: none;";
}

function showBack() {
    document.getElementById("backButton").style = "display: flex;";
}

function previousView() {
    if (viewHistory[viewHistory.length - 1] === "alertView") {
        showById("alertButton");
    }

    let previousViewId = viewHistory[viewHistory.length - 2];
    showView(previousViewId);
    if (previousViewId === "defaultView" && loggedInAs) {
        showById("logoutButton");
    }
    viewHistory.pop();
    viewHistory.pop();
}

let loggedInAs = null;

function logIn(staffId) {
    loggedInAs = staffId;
    hideById("scanIdPrompt");
    showById("moreOptions");
    showById("logoutButton");
}

function logOut() {
    loggedInAs = null;
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
}

// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 20;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";

addEventListener("keydown", (e) => {
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
    console.log(`Got barcode ${buffer}`);
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

let showNewAlerts = false;
let alertTestVariable = false;

function showAlertView(alertView, showBack) {
    showView(alertView, showBack);
    hideById("alertButton");
    showById("newAlerts");

    if (!showNewAlerts) {
        hideById("newAlerts");
    }
    showNewAlerts = false;
}

function recieveAlerts(alertArray) {}

function updateAlertBoxes(alertBoxes) {}
