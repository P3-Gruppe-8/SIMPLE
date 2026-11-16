import { registerItemType } from "./api.js";
import { logIn, quickWriteOff } from "./navigation.js";
import { showToast } from "./toast.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";

const loginIds = ["5741000134894", "5741000142899"]; // booster med sukker, booster uden sukker.....
const consumableIds = ["5056784902424"]; // monster rio punch.........

disableEnterToSubmit();

document.querySelectorAll(".barcode-field").forEach(setUpBarcodeField);

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
