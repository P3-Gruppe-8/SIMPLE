import {
    addItemTypeLocation,
    changeStock,
    deregisterItemType,
    getItemDetails,
    registerItemType,
    removeItemTypeLocation,
} from "./api.js";
import { hide, logIn, quickWriteOff, show } from "./navigation.js";
import { loginIds } from "./session.js";
import { showToast } from "./toast.js";
import { addBarcodeListener, disableEnterToSubmit } from "./scanning.js";
import { setUpBarcodeField } from "./barcodeField.js";
import { setUpShelfSelect } from "./shelfSelect.js";
import {
    getReceiptItems,
    setUpReceipt,
    showReceiptResults,
} from "./receipt.js";

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
            minQuantity: Number(
                document.getElementById("item-type-min-quantity").value,
            ),
        };
        registerItemType(itemType)
            .then((text) => showToast(text))
            .catch((error) => console.error(error));
    });

document
    .getElementById("write-off-dialog-form")
    .addEventListener("submit", () => {
        const receipt = document.getElementById("write-off-receipt");
        showReceiptResults("Write Off", getReceiptItems(receipt), (item) =>
            changeStock(item.barcode, -item.amount),
        );
    });

document
    .getElementById("restock-dialog-form")
    .addEventListener("submit", () => {
        const receipt = document.getElementById("restock-receipt");
        const shelf = document.getElementById("restock-shelf").value;
        showReceiptResults(
            "Restock",
            getReceiptItems(receipt),
            async (item) => {
                await changeStock(item.barcode, item.amount);
                // adding a shelf is idempotent, so it's fine if the item type already has it
                if (shelf) {
                    await addItemTypeLocation(item.barcode, shelf);
                }
            },
        );
    });

// shows an item type's details in `result` and returns them, or null if it couldn't be loaded
async function lookUpItem(barcode, result) {
    result.textContent = "Loading...";
    show(result);
    try {
        const details = await getItemDetails(barcode);
        result.textContent =
            details.name +
            "\nBarcode: " +
            details.barcode +
            "\nIn stock: " +
            details.quantity;
        return details;
    } catch (error) {
        console.error(error);
        if (error.status === 404) {
            result.textContent = "No item type has this barcode";
        } else {
            result.textContent = "Couldn't load the details";
        }
        return null;
    }
}

let itemToDelete = null; // the item type shown in the delete dialog
const deleteButton = document.querySelector(
    '#deregister-dialog-form button[type="submit"]',
);

// looks the barcode up, and only allows Delete if it was found
async function lookUpItemToDelete(barcode) {
    itemToDelete = await lookUpItem(
        barcode,
        document.getElementById("deregister-item-details"),
    );
    deleteButton.disabled = itemToDelete === null;
}

// every scan looks the item up straight away
document
    .getElementById("deregister-item-id")
    .closest(".barcode-field")
    .addEventListener("scan", async () => {
        const barcode = document.getElementById("deregister-item-id").value;
        await lookUpItemToDelete(barcode);
    });

// a typed barcode hasn't been looked up yet, so Delete can look it up
document
    .getElementById("deregister-item-id")
    .addEventListener("input", () => (deleteButton.disabled = false));

document
    .getElementById("deregister-dialog-form")
    .addEventListener("submit", async (event) => {
        event.preventDefault(); // only close once the deletion is confirmed
        const barcode = document.getElementById("deregister-item-id").value;
        if (!barcode) return;
        // a typed barcode may not have been looked up yet
        if (itemToDelete?.barcode !== barcode) {
            await lookUpItemToDelete(barcode);
        }
        if (!itemToDelete) return;

        const item = itemToDelete;
        const prompt = document.getElementById("deregister-confirm-dialog");
        const verb = item.quantity === 1 ? "is" : "are";
        prompt.querySelector(".deregister-confirm-message").textContent =
            "There " +
            verb +
            " " +
            item.quantity +
            " of " +
            item.name +
            " in stock. Are you sure you want to delete all of it?";
        prompt.returnValue = "";
        prompt.addEventListener(
            "close",
            () => {
                if (prompt.returnValue !== "delete") return;
                document.getElementById("deregister-dialog").close();
                deregisterItemType(item.barcode)
                    .then((text) => showToast(text))
                    .catch((error) => console.error(error));
            },
            { once: true },
        );
        prompt.showModal();
    });

// form reset doesn't clear the looked-up item, so do it when the dialog closes
document.getElementById("deregister-dialog").addEventListener("close", () => {
    itemToDelete = null;
    deleteButton.disabled = false;
    hide(document.getElementById("deregister-item-details"));
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
        lookUpItem(barcode, document.getElementById("item-details-result"));
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
