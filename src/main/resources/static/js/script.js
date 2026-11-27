import { logIn, quickWriteOff } from "./navigation.js";
import { staffUsername } from "./session.js";
import { showErrorToast } from "./toast.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";
import { setUpReceipt } from "./receipt.js";
import { setUpDialogs } from "./dialogs.js";
import { askForStaffId } from "./staffIdDialog.js";

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
    const username = staffUsername(barcode);
    if (username !== null) {
        logIn(username).catch((error) => {
            console.error(error);
            showErrorToast(error.message);
        });
    } else {
        quickWriteOff(barcode);
    }
});

setUpDialogs();

for (const id of ["scanIdPrompt", "loanModeLogInScreen"]) {
    document
        .getElementById(id)
        .addEventListener("click", () =>
            askForStaffId("Scan or type your staff ID."),
        );
}
