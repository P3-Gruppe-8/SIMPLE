// Alerts

import { hideById, showById, showView } from "./navigation.js";

let showNewAlerts = false;
let currentAlertsArray = [];

export function showAlertView(alertView) {
    showView(alertView);
    hideById("alertButton");
    hideById("modeSwitch");
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

function hasNewAlerts() {
    for (let alert in currentAlertsArray) {
        const isNewAlert = currentAlertsArray[alert].isNewAlert;

        if (isNewAlert === "true") {
            showNewAlerts = true;
            return;
        }
    }
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

function ageAlerts(currentAlertsArray) {
    for (let alert in currentAlertsArray) {
        const isNewAlert = currentAlertsArray[alert].isNewAlert;

        if (isNewAlert === "true") {
            isNewAlert = "false";
            return hasOldAlerts;
        }
    }
    console.log(currentAlertsArray);
}

/*

let alert = {
    alertType: "stock" or "loan" or "inactivity",
    urgency: "info" or "warning" or "critical",
    alertTextContent: "Write off has not been used in 1 week",
    newAlert: "true" or "false",
}
    */

export let dummyAlertArray = [
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
        isNewAlert: "false",
    },
    {
        alertType: "loan",
        urgency: "warning",
        alertTextContent:
            "Loan by LTB of Laptop 5 has not been returned, loan ended yesterday",
        isNewAlert: "false",
    },
    {
        alertType: "loan",
        urgency: "critical",
        alertTextContent:
            "Loan by LTB of Laptop 5 has not been returned, loan ended 1 week ago",
        isNewAlert: "false",
    },
    {
        alertType: "inactivity",
        urgency: "info",
        alertTextContent: "Write off has not been used in 1 day(s)",
        isNewAlert: "false",
    },
    {
        alertType: "inactivity",
        urgency: "warning",
        alertTextContent: "Write off has not been used in 3 day(s)",
        isNewAlert: "false",
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

export function updateAlerts(alertArray) {
    console.log("updating alerts");
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
        const alertObject = sortedAlertArray[alert];

        createAlert(alertObject);
    }
}

function createAlert(alertObject) {
    const alertType = alertObject.alertType;
    const urgencyValue = alertObject.urgency;

    // first create icon element
    const newIconElement = createIcon(alertType, urgencyValue);

    // create alert element
    const newAlertElement = document.createElement("div");

    let classString = "alert-box";
    const stockString = "stock";
    const loanString = "loan";
    const inactivityString = "inactivity";

    switch (alertType) {
        case "stock":
            classString = classString.concat(" ", stockString);
            break;
        case "loan":
            classString = classString.concat(" ", loanString);
            break;
        case "inactivity":
            classString = classString.concat(" ", inactivityString);
            break;
        default:
            throw new Error("Could not determine type of alert when building");
    }

    const infoString = "info";
    const warningString = "warning";
    const criticalString = "critical";

    switch (urgencyValue) {
        case "info":
            classString = classString.concat(" ", infoString);
            break;
        case "warning":
            classString = classString.concat(" ", warningString);
            break;
        case "critical":
            classString = classString.concat(" ", criticalString);
            break;
        default:
            throw new Error(
                "Could not determine urgency of alert when building",
            );
    }

    newAlertElement.setAttribute("class", classString);

    // create label
    const newAlertLabel = document.createElement("p");
    const labelTextContent = alertObject.alertTextContent;

    newAlertLabel.setAttribute("class", "alert-label");
    newAlertLabel.textContent = labelTextContent;

    newAlertElement.appendChild(newAlertLabel);

    // append icon and alert to new or old alert rows
    const isNewAlert = alertObject.isNewAlert;

    if (isNewAlert === "true") {
        const newAlertsRow = document.getElementById("newAlertsRow");
        newAlertsRow.appendChild(newIconElement);
        newAlertsRow.appendChild(newAlertElement);
        return;
    }

    if (isNewAlert !== "true") {
        const oldAlertsRow = document.getElementById("oldAlertsRow");
        oldAlertsRow.appendChild(newIconElement);
        oldAlertsRow.appendChild(newAlertElement);
        return;
    }

    throw new Error("Could not determine if alert is new when building");
}

function createIcon(alertType, urgencyValue) {
    const newIconElement = document.createElement("div");
    const newImageElement = document.createElement("img");

    let classString = "alert-box-icon";

    const stockString = "stock-icon";
    const stockImageClassString = "stock-img";
    const stockImagePathString = "images/box-open-solid-full.svg";

    const loanString = "loan-icon";
    const loanImageClassString = "loan-img";
    const loanImagePathString = "images/address-book.svg";

    const inactivityString = "inactivity-icon";
    const inactivityImageClassString = "inactivity-img";
    const inactivityImagePathString = "images/activity.svg";

    switch (alertType) {
        case "stock":
            classString = classString.concat(" ", stockString);
            newImageElement.setAttribute("class", stockImageClassString);
            newImageElement.setAttribute("src", stockImagePathString);
            break;
        case "loan":
            classString = classString.concat(" ", loanString);
            newImageElement.setAttribute("class", loanImageClassString);
            newImageElement.setAttribute("src", loanImagePathString);
            break;
        case "inactivity":
            classString = classString.concat(" ", inactivityString);
            newImageElement.setAttribute("class", inactivityImageClassString);
            newImageElement.setAttribute("src", inactivityImagePathString);
            break;
        default:
            throw new Error("Could not determine type of alert when building");
    }

    const infoString = "info";
    const warningString = "warning";
    const criticalString = "critical";

    switch (urgencyValue) {
        case "info":
            classString = classString.concat(" ", infoString);
            break;
        case "warning":
            classString = classString.concat(" ", warningString);
            break;
        case "critical":
            classString = classString.concat(" ", criticalString);
            break;
        default:
            throw new Error(
                "Could not determine urgency of alert when building",
            );
    }

    newIconElement.setAttribute("class", classString);
    newIconElement.appendChild(newImageElement);

    return newIconElement;
}
