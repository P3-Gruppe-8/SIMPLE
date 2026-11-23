// Alerts

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
        isNewAlert: "false",
    },
];

updateAlerts(dummyAlertArray);

function recieveAlerts(SSEAlertArray) {
    // function to handle alerts sent with SSE
}

function updateAlerts(alertArray) {
    //removeAlerts();
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
