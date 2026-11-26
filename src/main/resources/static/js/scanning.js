import { logKey, logScan } from "./debug.js";

// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 75;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";
let valueBeforeScan; // the focused field's value when the current scan began, undefined outside fields

// the "claims" system lets us take priority over the global barcode scan handler
// this array contains objects with a handler field (some function that uses the barcode) and a pattern field (a regular expression)
// the pattern can be null, in other words you can choose to claim ANY barcode
let claims = [];

// add a claim to the claims array. this returns a "release" function, which the caller will use when they no longer wish to claim barcodes matching their pattern.
export function claimScans(handler, pattern = null) {
    const claim = { handler, pattern };
    claims.push(claim);
    return () => {
        claims = claims.filter((c) => c !== claim); // literally just get the claims list but without the claim the caller added
    };
}

// just like claimScans, but released automatically after one scan.
export function claimNextScan(handler, pattern = null) {
    const release = claimScans((barcode) => {
        release();
        handler(barcode);
    }, pattern);
    return release;
}

// given a barcode, first look for a claim whose pattern matches that barcode. if there is no such claim, look for a claim with no pattern.
// uses findLast specifically because we want the newest matching claim to be chosen over an older matching claim
function findClaim(barcode) {
    return (
        claims.findLast((c) => c.pattern?.test(barcode)) ??
        claims.findLast((c) => !c.pattern)
    );
}

export function addBarcodeListener(onScan) {
    window.addEventListener("keydown", (e) => {
        // a scan into a text field belongs to that field, unless there is a claim
        const inTextField = isTextField(e.target);
        if (!inTextField && e.key.length === 1 && e.key !== " ") {
            // typed characters switch the browser to keyboard mode, which draws a focus ring on the last tapped button
            e.target.blur();
        }
        if (inTextField && claims.length === 0) {
            buffer = "";
            return;
        }
        const currentTime = Date.now();
        logKey(e, currentTime - lastKeyTime);
        if (currentTime - lastKeyTime > SCAN_KEY_GAP_MS) {
            buffer = ""; // we only care about super fast keystrokes
        }
        lastKeyTime = currentTime;
        if (buffer === "") {
            // a scan may be starting; remember the field as it was so we can undo the scanner's typing
            valueBeforeScan = e.target.value;
        }
        if (e.key === " " && buffer !== "" && !inTextField) {
            e.preventDefault(); // a space inside a scan (e.g. "nowaco staff anna") would otherwise press the focused button
        }
        if (e.key === "Enter" && buffer.length >= MIN_BARCODE_LENGTH) {
            e.preventDefault();
            const claim = findClaim(buffer); // if there are no matching claims this is undefined
            if (claim) {
                if (inTextField && valueBeforeScan !== undefined) {
                    // the scanner already typed the barcode into the focused field, so take it back out
                    e.target.value = valueBeforeScan;
                }
                // send the buffer to the found claim's handler
                claim.handler(buffer);
            } else {
                // if there are no claims in the claims array we just give the barcode to the global handler
                onScan(buffer);
            }
            logScan(buffer);
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
    return (
        tag === "INPUT" ||
        tag === "TEXTAREA" ||
        tag === "SELECT" ||
        element.isContentEditable
    );
}

// scanners end every scan with Enter, which would submit a form before all fields are filled
export function disableEnterToSubmit() {
    window.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && e.target.tagName === "INPUT") {
            e.preventDefault();
        }
    });
}
