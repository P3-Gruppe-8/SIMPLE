// Submit handlers and behaviour for the menu dialogs.
import {
    addItemTypeLocation,
    changeStock,
    deregisterItemType,
    getItemDetails,
    getItemTypeLocations,
    registerItemType,
    setItemTypeLocations,
} from "./api.js";
import { hide, show } from "./navigation.js";
import { showToast } from "./toast.js";
import { getReceiptItems, showReceiptResults } from "./receipt.js";

export function setUpDialogs() {
    resetFormsOnClose();
    setUpRegisterDialog();
    setUpWriteOffDialog();
    setUpRestockDialog();
    setUpDeregisterDialog();
    setUpLocationsDialog();
    setUpItemDetailsDialog();
}

function resetFormsOnClose() {
    // so a cancelled dialog doesn't show the old values next time it opens
    document.querySelectorAll("dialog").forEach((dialog) => {
        dialog.addEventListener("close", () =>
            dialog.querySelector("form")?.reset(),
        );
    });
}

function setUpRegisterDialog() {
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
}

function setUpWriteOffDialog() {
    document
        .getElementById("write-off-dialog-form")
        .addEventListener("submit", () => {
            const receipt = document.getElementById("write-off-receipt");
            showReceiptResults("Write Off", getReceiptItems(receipt), (item) =>
                changeStock(item.barcode, -item.amount),
            );
        });
}

function setUpRestockDialog() {
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
}

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

function setUpDeregisterDialog() {
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
    document
        .getElementById("deregister-dialog")
        .addEventListener("close", () => {
            itemToDelete = null;
            deleteButton.disabled = false;
            hide(document.getElementById("deregister-item-details"));
        });
}

function setUpLocationsDialog() {
    const dialog = document.getElementById("locations-dialog");
    const input = document.getElementById("locations-item-id");
    const details = document.getElementById("locations-item-details");
    const shelfList = document.getElementById("locations-shelves");
    const addShelf = document.getElementById("locations-add-shelf");
    const addShelfField = addShelf.closest(".form-field");
    const saveButton = document.querySelector(
        '#locations-dialog-form button[type="submit"]',
    );
    let shownBarcode = null; // the item type whose shelves are shown

    // every scan shows that item type's shelves, replacing any unsaved changes
    input.closest(".barcode-field").addEventListener("scan", async () => {
        const barcode = input.value;
        hideShelves();
        const item = await lookUpItem(barcode, details);
        if (!item || input.value !== barcode) return; // not found, or another scan came in meanwhile
        let shelves;
        try {
            shelves = await getItemTypeLocations(barcode);
        } catch (error) {
            console.error(error);
            details.textContent = "Couldn't load the shelves";
            return;
        }
        if (input.value !== barcode) return;
        details.textContent = item.name; // shows the user the name of the item they scanned for confirmation
        shelfList.replaceChildren(...shelves.map(createShelfRow));
        shownBarcode = barcode;
        show(shelfList);
        show(addShelfField);
        saveButton.disabled = false;
    });

    // picking or scanning a shelf adds it to the top of the list
    addShelf.addEventListener("change", () => {
        const shelf = addShelf.value;
        if (shelf && !shownShelves().includes(shelf)) {
            shelfList.prepend(createShelfRow(shelf));
        }
        addShelf.value = ""; // back to the placeholder, ready for the next one
    });

    // a row with the shelf's name and a button to remove it
    function createShelfRow(shelf) {
        const row = document.createElement("li");
        row.className = "shelf-row";
        row.dataset.shelf = shelf;
        const name = document.createElement("span");
        name.className = "shelf-row-name";
        name.textContent = shelf;
        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.className = "red raised";
        const label = document.createElement("span");
        label.textContent = "Remove";
        removeButton.append(label);
        removeButton.addEventListener("click", () => row.remove());
        row.append(name, removeButton);
        return row;
    }

    function shownShelves() {
        return [...shelfList.children].map((row) => row.dataset.shelf);
    }

    function hideShelves() {
        shownBarcode = null;
        shelfList.replaceChildren();
        hide(shelfList);
        hide(addShelfField);
        saveButton.disabled = true; // nothing to save until an item type's shelves are shown
    }
    hideShelves();

    document
        .getElementById("locations-dialog-form")
        .addEventListener("submit", () => {
            if (shownBarcode === null) return;
            setItemTypeLocations(shownBarcode, shownShelves())
                .then((text) => showToast(text))
                .catch((error) => console.error(error));
        });

    // form reset doesn't clear the shelves, so do it when the dialog closes
    dialog.addEventListener("close", () => {
        hideShelves();
        hide(details);
    });
}

function setUpItemDetailsDialog() {
    document
        .getElementById("item-details-dialog-form")
        .addEventListener("submit", (event) => {
            event.preventDefault(); // keep the dialog open to show the details
            const barcode = document.getElementById(
                "item-details-item-id",
            ).value;
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
}
