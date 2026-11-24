// A form field whose value comes from either a barcode scan or manual typing.
import { claimNextScan } from "./scanning.js";

// with keep scanning, a typed barcode is looked up once typing pauses this long
const TYPING_PAUSE_MS = 1000;

export function setUpBarcodeField(field) {
    // grab all the buttons the field should have
    const buttons = field.querySelector(".barcode-buttons");
    const scanButton = field.querySelector(".barcode-scan");
    const scanLabel = scanButton.querySelector(".barcode-scan-label");
    const manualButton = field.querySelector(".barcode-manual");
    const input = field.querySelector("input");
    const idleLabel = scanLabel.textContent;
    // fields marked data-keep-scanning take every scan, not just the first
    const keepScanning = "keepScanning" in field.dataset;

    // the value of this variable will be a function which, when called, removes the claim from the claims list.
    let cancelCapture = null;

    // the pending lookup of a typed barcode, see TYPING_PAUSE_MS
    let typingTimer = null;

    // begin waiting for any barcode to be entered
    function startAwaiting() {
        if (cancelCapture) {
            // this makes the function idempotent
            return;
        }
        scanButton.classList.add("awaiting");
        scanLabel.textContent = "Awaiting barcode...";

        // claimNextScan returns the function that removes our claim from the list
        cancelCapture = claimNextScan((barcode) => {
            // whenever the next scan happens this anonymous function fires
            cancelCapture = null;
            stopAwaiting();
            input.value = barcode;
            if (keepScanning) {
                startAwaiting(); // the page shows the result, so the input can stay hidden
            } else {
                showInput(); // so the user can see if the scan messed up
            }
            field.dispatchEvent(new Event("scan")); // lets the page react to the scan
        });
    }

    function stopAwaiting() {
        cancelCapture?.(); // this syntax means "call it if it isn't null" since cancelCapture can either be null or a function (god i wish we had access to TypeScript)
        cancelCapture = null;
        scanButton.classList.remove("awaiting");
        scanLabel.textContent = idleLabel;
    }

    function showInput() {
        buttons.classList.add("invisible");
        input.classList.remove("invisible");
    }

    // gets called when the dialog the barcode field is potentially in, gets opened/closed.
    // that way, pressing cancel on a dialog does what you expect.
    function reset() {
        clearTimeout(typingTimer);
        stopAwaiting();
        input.value = "";
        input.classList.add("invisible");
        buttons.classList.remove("invisible");
    }

    scanButton.addEventListener("click", startAwaiting);

    // with keep scanning, a typed barcode counts as a scan when Enter is pressed or typing pauses
    input.addEventListener("keydown", (e) => {
        if (keepScanning && e.key === "Enter" && input.value) {
            clearTimeout(typingTimer); // already looked up, don't do it twice
            field.dispatchEvent(new Event("scan"));
        }
    });
    input.addEventListener("input", () => {
        if (!keepScanning) return;
        clearTimeout(typingTimer);
        typingTimer = setTimeout(() => {
            if (input.value) {
                field.dispatchEvent(new Event("scan"));
            }
        }, TYPING_PAUSE_MS);
    });

    // in case the scanner isn't working we want to be able to do manual entry
    manualButton.addEventListener("click", () => {
        stopAwaiting();
        showInput();
        input.focus();
    });

    // when the dialog closes or opens
    field.closest("dialog")?.addEventListener("toggle", (e) => {
        // we reset
        reset();
        if (e.newState === "open" && field.dataset.prefill) {
            // the opener already knows the barcode (set data-prefill before opening)
            // this is used in the "prompt for descriptor on unknown item scan" use case
            input.value = field.dataset.prefill;
            delete field.dataset.prefill;
            showInput();
        } else if (e.newState === "open") {
            // and start awaiting if it's being opened.
            startAwaiting();
        }
    });
}
