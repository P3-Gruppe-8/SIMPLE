import { registerItemType } from "./api.js";
import { logKey, logScan } from "./dev.js";
import { logIn } from "./navigation.js";
import { showToast } from "./toast.js";

// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 20;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";

window.addEventListener("keydown", (e) => {
    const currentTime = Date.now();
    logKey(e, currentTime - lastKeyTime);
    if (currentTime - lastKeyTime > SCAN_KEY_GAP_MS) {
        buffer = "";
    }
    lastKeyTime = currentTime;
    if (e.key === "Enter" && buffer.length >= MIN_BARCODE_LENGTH) {
        e.preventDefault();
        logScan(buffer);
        onScan(buffer);
        buffer = "";
        return;
    }
    if (e.key.length === 1) {
        buffer += e.key;
    }
});

const validIds = ["5741000134894", "5741000142899"]; // booster med sukker, booster uden sukker.....

function onScan(buffer) {
    if (validIds.includes(buffer)) {
        logIn(buffer);
    }
}

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

logIn("temp");
