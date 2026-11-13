const modeSwitch = document.getElementById("modeSwitch");
const modeOptions = document.querySelectorAll(".mode-option");
const alertButton = document.getElementById;

let showNewAlerts = true;
let alertTestVariable = false;

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
    if (previousViewId === "defaultView") {
        showById("logoutButton");
    }
    viewHistory.pop();
    viewHistory.pop();
}

function logIn() {
    hideById("scanIdPrompt");
    showById("moreOptions");
    showById("logoutButton");
}

function logOut() {
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
}

addEventListener("keydown", (e) => {
    console.log(e.key);
});

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

function showAlertView(alertView, showBack) {
    showView(alertView, showBack);
    hideById("alertButton");

    if (!showNewAlerts) {
    }
}

function recieveAlerts(alertArray) {
    alertViewVisited = false;
}
