import { getShelves, registerItemType } from "./api.js";
import { logIn, quickWriteOff } from "./navigation.js";
import { showToast } from "./toast.js";
import {
    addBarcodeListener,
    claimScans,
    disableEnterToSubmit,
} from "./scanning.js";
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

// shelf codes look like NOWACO-SHELF-XX, where XX is the shelf ID.
// this is a regular expression, which can be used to check if text is shaped a certain way.
const SHELF_CODE = /^NOWACO-SHELF-(.+)$/;

const shelfDialog = document.getElementById("register-consumable-dialog");
const shelfSelect = document.getElementById("item-type-shelf");

// we store the promise returned by this async function call because we later want to know whether it is done loading
let shelvesLoaded = loadShelves();

// this becomes a release function as described in scanning.js
let releaseShelfScans = null;

// refetch on every open so newly added shelves show up
shelfDialog.addEventListener("toggle", (event) => {
    releaseShelfScans?.(); // this syntax means call if not null
    releaseShelfScans = null;
    if (event.newState === "open") {
        shelvesLoaded = loadShelves();
        releaseShelfScans = claimScans(selectScannedShelf, SHELF_CODE); // call dibs on barcode scans which match the format NOWACO-SHELF-XX
    }
});

// we want errors to go away when the selection in the dropdown is changed
shelfSelect.addEventListener("change", clearShelfError);
function clearShelfError() {
    shelfSelect.classList.remove("invalid");
    shelfSelect.options[0].text = "Choose or scan a shelf...";
}

// called when a barcode in the format NOWACO-SHELF-XX is scanned
async function selectScannedShelf(barcode) {
    const shelfId = barcode.match(SHELF_CODE)[1]; // [1] gets the captured value, which is the part inside the (parentheses) in a regular expression. see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/match
    await shelvesLoaded; // we finally use this value here to ensure we actually have stuff in the dropdown before we do all our logic with it

    // only shelves the server knows about can be picked, so a bad code can't sneak in
    const known = [...shelfSelect.options].some(
        (option) => !option.disabled && option.value === shelfId,
    );
    if (known) {
        shelfSelect.value = shelfId;
        clearShelfError(); // in case of a previous error
    } else {
        setPlaceholder(`Unknown shelf "${shelfId}"`, false);
        shelfSelect.classList.add("invalid");
    }
}

// populates the dropdown with shelves from the getShelves function in api.js
async function loadShelves() {
    setPlaceholder("Loading shelves...");
    shelfSelect.classList.remove("invalid");
    try {
        const shelves = await getShelves();
        setPlaceholder("Choose or scan a shelf...");
        shelfSelect.append(...shelves.map((shelf) => new Option(shelf, shelf)));
    } catch (error) {
        console.error(error);
        setPlaceholder("Couldn't load shelves");
    }
}

// the placeholder is the first, unpickable option. unless `clearShelves` is false, it also removes the loaded shelves.
function setPlaceholder(text, clearShelves = true) {
    const placeholder = new Option(text, "", true, true);
    placeholder.disabled = true;
    if (clearShelves) {
        shelfSelect.replaceChildren(placeholder); // replace absolutely everything in the dropdown with just the placeholder
    } else {
        shelfSelect.options[0].replaceWith(placeholder); // replace only the first item (which should be another placeholder) with the chosen placeholder
        shelfSelect.value = "";
    }
}
