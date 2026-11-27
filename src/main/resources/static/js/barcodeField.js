// A form field whose value comes from either a barcode scan or manual typing.
import { claimNextScan } from "./scanning.js";

// with data-submit-on-pause, a typed barcode counts as a scan once typing pauses this long
const TYPING_PAUSE_MS = 1000;

export function setUpBarcodeField(field) {
    // grab all the buttons the field should have
    const buttons = field.querySelector(".barcode-buttons");
    const scanButton = field.querySelector(".barcode-scan");
    const scanLabel = scanButton.querySelector(".barcode-scan-label");
    const manualButton = field.querySelector(".barcode-manual");
    const manualIcon = manualButton.querySelector("img");
    const input = field.querySelector("input");
    // ipads have no enter key
    const submitButton = field.querySelector(".barcode-submit");
    const idleLabel = scanLabel.textContent;
    const manualIconSrc = manualIcon.getAttribute("src");
    const manualLabel = manualButton.getAttribute("aria-label");

    // the input sits in the button row, taking the scan button's place when shown
    buttons.insertBefore(input, submitButton ?? manualButton);
    // fields marked data-keep-scanning take every scan, not just the first
    const keepScanning = "keepScanning" in field.dataset;
    // for lookups, where an early submit does no harm
    const submitOnPause = "submitOnPause" in field.dataset;
    // a pattern lets this field's claim win over claims without one (see findClaim in scanning.js)
    const scanPattern = field.dataset.scanPattern
        ? new RegExp(field.dataset.scanPattern)
        : null;

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
                showScanned(); // so the user can see if the scan messed up
            }
            field.dispatchEvent(new Event("scan")); // lets the page react to the scan
        }, scanPattern);
    }

    function stopAwaiting() {
        cancelCapture?.(); // this syntax means "call it if it isn't null" since cancelCapture can either be null or a function (god i wish we had access to TypeScript)
        cancelCapture = null;
        scanButton.classList.remove("awaiting");
        scanLabel.textContent = idleLabel;
    }

    // the input takes the scan button's place, and the manual button becomes an undo button
    function showInput() {
        scanButton.classList.add("invisible");
        input.classList.remove("invisible");
        manualIcon.src = "images/left-long-solid-full.svg";
        manualButton.title = "Undo";
        manualButton.setAttribute("aria-label", "Undo");
    }

    // a scanned barcode is shown read-only
    function showScanned() {
        showInput();
        input.disabled = true;
    }

    function showManualEntry() {
        showInput();
        submitButton?.classList.remove("invisible");
        input.focus();
    }

    // gets called when the dialog the barcode field is potentially in, gets opened/closed.
    // that way, pressing cancel on a dialog does what you expect.
    function reset() {
        clearTimeout(typingTimer);
        stopAwaiting();
        input.value = "";
        input.disabled = false;
        input.classList.add("invisible");
        submitButton?.classList.add("invisible");
        scanButton.classList.remove("invisible");
        manualIcon.src = manualIconSrc;
        manualButton.title = manualLabel;
        manualButton.setAttribute("aria-label", manualLabel);
    }

    scanButton.addEventListener("click", startAwaiting);

    // with keep scanning, a typed barcode counts as a scan when Enter or the submit button is pressed
    function submitTyped() {
        if (input.value) {
            clearTimeout(typingTimer); // already looked up, don't do it twice
            field.dispatchEvent(new Event("scan"));
        }
    }
    input.addEventListener("keydown", (e) => {
        if (keepScanning && e.key === "Enter") {
            e.preventDefault();
            submitTyped();
        }
    });
    submitButton?.addEventListener("click", () => {
        submitTyped();
        input.focus(); // keeps the on-screen keyboard open for the next barcode
    });
    input.addEventListener("input", () => {
        if (!keepScanning || !submitOnPause) return;
        clearTimeout(typingTimer);
        typingTimer = setTimeout(() => {
            if (input.value) {
                field.dispatchEvent(new Event("scan"));
            }
        }, TYPING_PAUSE_MS);
    });

    // in case the scanner isn't working we want to be able to do manual entry.
    // while the input is shown, the same button is the undo button
    manualButton.addEventListener("click", () => {
        const inputShown = !input.classList.contains("invisible");
        if (inputShown) {
            reset();
            startAwaiting();
        } else {
            stopAwaiting();
            showManualEntry();
        }
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
            showScanned();
        } else if (e.newState === "open") {
            // and start awaiting if it's being opened.
            startAwaiting();
        }
    });
}
