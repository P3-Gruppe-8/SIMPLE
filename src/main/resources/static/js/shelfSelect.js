// A dropdown of the shelves fetched from the server, which can also be set by scanning a shelf code.
import { getShelves } from "./api.js";
import { claimScans } from "./scanning.js";

// shelf codes look like NOWACO-SHELF-XX, where XX is the shelf ID.
// this is a regular expression, which can be used to check if text is shaped a certain way.
const SHELF_CODE = /^NOWACO-SHELF-(.+)$/;

const DEFAULT_PLACEHOLDER = "Choose or scan a shelf...";

export function setUpShelfSelect(select) {
    // we store the promise returned by this async function call because we later want to know whether it is done loading
    let shelvesLoaded = loadShelves();

    // this becomes a release function as described in scanning.js
    let releaseShelfScans = null;

    // when the dialog the dropdown is in closes or opens
    select.closest("dialog")?.addEventListener("toggle", (event) => {
        releaseShelfScans?.(); // this syntax means call if not null
        releaseShelfScans = null;
        if (event.newState === "open") {
            shelvesLoaded = loadShelves(); // refetch so newly added shelves show up
            releaseShelfScans = claimScans(selectScannedShelf, SHELF_CODE); // call dibs on barcode scans which match the format NOWACO-SHELF-XX
        }
    });

    // we want errors to go away when the selection in the dropdown is changed
    select.addEventListener("change", clearError);
    function clearError() {
        select.classList.remove("invalid");
        select.options[0].text = DEFAULT_PLACEHOLDER;
    }

    // called when a barcode in the format NOWACO-SHELF-XX is scanned
    async function selectScannedShelf(barcode) {
        const shelfId = barcode.match(SHELF_CODE)[1]; // [1] gets the captured value, which is the part inside the (parentheses) in a regular expression. see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/match
        await shelvesLoaded; // we finally use this value here to ensure loading has finished before we do all our logic with the dropdown

        // only shelves the server knows about can be picked, so a bad code can't sneak in
        const known = [...select.options].some(
            (option) => !option.disabled && option.value === shelfId,
        );
        if (known) {
            select.value = shelfId;
            clearError(); // in case of a previous error
        } else {
            setPlaceholder(`Unknown shelf "${shelfId}"`, false);
            select.classList.add("invalid");
        }
    }

    // populates the dropdown with shelves from the getShelves function in api.js
    async function loadShelves() {
        setPlaceholder("Loading shelves...");
        select.classList.remove("invalid");
        try {
            const shelves = await getShelves();
            setPlaceholder(DEFAULT_PLACEHOLDER);
            select.append(...shelves.map((shelf) => new Option(shelf, shelf)));
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
            select.replaceChildren(placeholder); // replace absolutely everything in the dropdown with just the placeholder
        } else {
            select.options[0].replaceWith(placeholder); // replace only the first item (which should be another placeholder) with the chosen placeholder
            select.value = "";
        }
    }
}
