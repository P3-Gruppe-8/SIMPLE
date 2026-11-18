// Alerts.

let showNewAlerts = true;
let alertTestVariable = false;

let currentTime = new Date().toString();

export let dummyAlertArray = [
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

export function recieveAlerts(alertArray) {
    alertViewVisited = false;
}
