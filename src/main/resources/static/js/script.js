import { logIn, quickWriteOff } from "./navigation.js";
import { loginIds } from "./session.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";
import { setUpReceipt } from "./receipt.js";
import { setUpDialogs } from "./dialogs.js";

const consumableIds = [
    "5056784902424",
    "5060337502290",
    "5741000142899",
    "consumable 67",
    "consumable 42",
]; // monster rio punch, hvid monner.........
const loanableIds = ["loanable 01", "loanable 02"];

disableEnterToSubmit();

document.querySelectorAll(".barcode-field").forEach(setUpBarcodeField);
document.querySelectorAll(".shelf-select").forEach(setUpShelfSelect);
// not every .receipt: the results dialog has one too, but nothing to scan into it
setUpReceipt(document.getElementById("write-off-receipt"));
setUpReceipt(document.getElementById("restock-receipt"));

addBarcodeListener((barcode) => {
    console.log(`Got barcode ${barcode}`);
    // an open dialog has the user's attention; don't log in or write off behind it
    if (document.querySelector("dialog[open]")) {
        return;
    }
    if (loginIds.includes(barcode)) {
        logIn(barcode);
    }
    if (consumableIds.includes(barcode)) {
        console.log("which is in the consumable list");
        quickWriteOff(barcode);
    }
});

setUpDialogs();
