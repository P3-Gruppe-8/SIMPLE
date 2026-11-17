import { registerItemType } from "./api.js";
import { logIn, quickWriteOff } from "./navigation.js";
import { showToast } from "./toast.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";

const loginIds = ["victor nummer 2", "leon da boss"];
const consumableIds = [
    "5056784902424",
    "5060337502290",
    "consumable 67",
    "consumable 42",
]; // monster rio punch, hvid monner.........
const loanableIds = ["loanable 01", "loanable 02"];

disableEnterToSubmit();

document.querySelectorAll(".barcode-field").forEach(setUpBarcodeField);
document.querySelectorAll(".shelf-select").forEach(setUpShelfSelect);

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

document
    .getElementById("register-consumable-dialog-form")
    .addEventListener("submit", (event) => {
        const itemType = {
            name: document.getElementById("item-type-name").value,
            itemId: document.getElementById("item-id").value,
            shelf: document.getElementById("item-type-shelf").value,
        };
        registerItemType(itemType)
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
        event.target.reset();
    });
