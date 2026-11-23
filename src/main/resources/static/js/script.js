import {
    addItemTypeLocation,
    changeStock,
    deregisterItemType,
    getItemDetails,
    registerItemType,
    removeItemTypeLocation,
} from "./api.js";
import { hide, logIn, quickWriteOff, show } from "./navigation.js";
import { showToast } from "./toast.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";
import { setUpSearchableList } from "./searchableList.js";
import { setUpMultiStepDialog } from "./MultiStepDialog.js";

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
document.querySelectorAll(".searchable-list").forEach(setUpSearchableList);
document.querySelectorAll("dialog:has(.form-step)").forEach(setUpMultiStepDialog);

// so a cancelled dialog doesn't show the old values next time it opens
document.querySelectorAll("dialog").forEach((dialog) => {
    dialog.addEventListener("close", () =>
        dialog.querySelector("form")?.reset(),
    );
});

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
            barcode: document.getElementById("item-id").value,
            shelf: document.getElementById("item-type-shelf").value,
        };
        registerItemType(itemType)
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
    });

document
    .getElementById("write-off-dialog-form")
    .addEventListener("submit", () => {
        const barcode = document.getElementById("write-off-item-id").value;
        const amount = Number(
            document.getElementById("write-off-amount").value,
        );
        changeStock(barcode, -amount)
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
    });

document
    .getElementById("restock-dialog-form")
    .addEventListener("submit", () => {
        const barcode = document.getElementById("restock-item-id").value;
        const amount = Number(document.getElementById("restock-amount").value);
        const shelf = document.getElementById("restock-shelf").value;
        changeStock(barcode, amount)
            .then((text) => {
                showToast(text);
                // adding a shelf is idempotent, so it's fine if the item type already has it
                if (shelf) {
                    return addItemTypeLocation(barcode, shelf);
                }
            })
            .catch((error) => console.error(error));
    });

document
    .getElementById("deregister-dialog-form")
    .addEventListener("submit", () => {
        const barcode = document.getElementById("deregister-item-id").value;
        deregisterItemType(barcode)
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
    });

document
    .getElementById("locations-dialog-form")
    .addEventListener("submit", (event) => {
        const barcode = document.getElementById("locations-item-id").value;
        const shelf = document.getElementById("locations-shelf").value;
        // the Add and Remove buttons both submit; their value says which was pressed
        let request;
        if (event.submitter.value === "add") {
            request = addItemTypeLocation(barcode, shelf);
        } else {
            request = removeItemTypeLocation(barcode, shelf);
        }
        request
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
    });

document
    .getElementById("item-details-dialog-form")
    .addEventListener("submit", (event) => {
        event.preventDefault(); // keep the dialog open to show the details
        const barcode = document.getElementById("item-details-item-id").value;
        const result = document.getElementById("item-details-result");
        result.textContent = "Loading...";
        show(result);
        getItemDetails(barcode)
            .then((text) => (result.textContent = text))
            .catch((error) => {
                console.error(error);
                result.textContent = "Couldn't load the details";
            });
    });

// every scan looks the item up straight away
document
    .getElementById("item-details-item-id")
    .closest(".barcode-field")
    .addEventListener("scan", () =>
        document.getElementById("item-details-dialog-form").requestSubmit(),
    );

// form reset doesn't clear the details, so do it when the dialog closes
document
    .getElementById("item-details-dialog")
    .addEventListener("close", () =>
        hide(document.getElementById("item-details-result")),
    );
