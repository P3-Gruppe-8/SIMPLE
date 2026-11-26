import { logIn, quickWriteOff } from "./navigation.js";
import { loginIds } from "./session.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";
import { setUpReceipt } from "./receipt.js";
import { setUpDialogs } from "./dialogs.js";

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
    } else {
        quickWriteOff(barcode);
    }
});

setUpDialogs();
