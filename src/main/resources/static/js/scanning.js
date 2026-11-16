// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 50;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";

export function addBarcodeListener(onScan) {
    window.addEventListener("keydown", (e) => {
        // a scan into a text field belongs to that field, not to the global handler
        if (isTextField(e.target)) {
            buffer = "";
            return;
        }
        const currentTime = Date.now();
        if (currentTime - lastKeyTime > SCAN_KEY_GAP_MS) {
            buffer = "";
        }
        lastKeyTime = currentTime;
        if (e.key === "Enter" && buffer.length >= MIN_BARCODE_LENGTH) {
            e.preventDefault();
            onScan(buffer);
            buffer = "";
            return;
        }
        if (e.key.length === 1) {
            buffer += e.key;
        }
    });
}

function isTextField(element) {
    const tag = element.tagName;
    return tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT" || element.isContentEditable;
}

// scanners end every scan with Enter, which would submit a form before all fields are filled
export function disableEnterToSubmit() {
    window.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && e.target.tagName === "INPUT") {
            e.preventDefault();
        }
    });
}
