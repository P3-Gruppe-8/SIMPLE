// A receipt: a table of scanned item types and how many of each
import { getItemDetails } from "./api.js";
import { logIn } from "./navigation.js";
import { isLoggedIn, loginIds } from "./session.js";
import { claimScans } from "./scanning.js";

export function setUpReceipt(receipt) {
    const form = receipt.closest("form");
    const field = form.querySelector(".barcode-field");
    const input = field.querySelector("input");
    const rows = receipt.querySelector("tbody");
    const submitButton = form.querySelector('button[type="submit"]');

    field.addEventListener("scan", () => {
        const barcode = input.value.trim();
        input.value = ""; // ready for the next manual entry
        if (barcode) {
            addOne(barcode);
        }
    });

    // so a cancelled receipt isn't there next time the dialog opens
    form.closest("dialog").addEventListener("close", () => {
        rows.replaceChildren();
        updateSubmitButton();
    });

    function addOne(barcode) {
        let row = findRow(barcode);
        if (row) {
            const amountInput = row.querySelector(".receipt-amount input");
            amountInput.value = Number(amountInput.value) + 1;
        } else {
            row = rows.insertRow();
            row.dataset.barcode = barcode;
            const nameCell = row.insertCell();
            nameCell.className = "receipt-name";
            nameCell.textContent = barcode; // until the name arrives, or if the barcode is unknown
            getItemDetails(barcode)
                .then((details) => (nameCell.textContent = details.name))
                .catch((error) => {
                    if (error.status === 404) {
                        row.classList.add("unknown");
                        unknownPrompts = unknownPrompts
                            .then(() => askAboutUnknown(row, nameCell))
                            .catch(console.error); // so one failed prompt won't stop every later prompt from opening
                    } else {
                        console.error(error);
                    }
                });
            const amountCell = row.insertCell();
            amountCell.className = "receipt-amount";
            amountCell.append(...createAmountControls(row));
            updateSubmitButton();
        }
        // the latest scan goes on top, so it's visible on a long receipt
        rows.prepend(row);
        receipt.scrollTop = 0;
        highlight(row);
    }

    // the user either undoes the scan or registers the barcode as a new item type
    async function askAboutUnknown(row, nameCell) {
        if (!row.isConnected) {
            return; // the receipt was closed or the line removed while this waited
        }
        const prompt = document.getElementById("unknown-barcode-dialog");
        prompt.querySelector(".unknown-barcode").textContent =
            row.dataset.barcode;
        prompt.returnValue = "";
        prompt.showModal();
        // while we ask about this barcode, other scans are ignored, so the worker deals with one item at a time
        const releaseBlock = claimScans(() => {
            // tell them the scan was ignored, e.g. shake the dialog or show "answer this first"
        }, /.*/);
        try {
            await waitForClose(prompt);

            const wantsToRegister = prompt.returnValue === "register";
            // because of the way conditionals are evaluated, this auto-runs the ask for id part specifically if the user is not logged in but pressed "register item type".
            if (wantsToRegister && (isLoggedIn() || (await askForStaffId()))) {
                const name = await registerUnknown(row.dataset.barcode);
                if (name !== null) {
                    nameCell.textContent = name;
                    row.classList.remove("unknown");
                    return;
                }
            }
            // this happens when they pressed "undo scan"
            row.remove();
            updateSubmitButton();
        } finally {
            releaseBlock();
        }
    }

    // a minus button, the amount field and a plus button
    function createAmountControls(row) {
        // text, not type="number", so a scan into it can be taken back out (see scanning.js)
        // additionally, type="number" would not save you from accidental scans as barcodes usually consist of numbers
        // but since the inputMode is set to numeric you basically can't input letters from the iPad anyway
        const input = document.createElement("input");
        input.type = "text";
        input.inputMode = "numeric";
        input.value = 1;
        input.addEventListener(
            "change",
            () => setAmount(Math.floor(Number(input.value))), // if the input value somehow contains text the whole line gets removed due to math.floor returning NaN (see the inside of setAmount). decimals get floored
        );

        const minus = createStepButton("images/minus-solid-full.svg");
        minus.addEventListener("click", () =>
            setAmount(Number(input.value) - 1),
        );
        const plus = createStepButton("images/plus-solid-full.svg");
        plus.addEventListener("click", () =>
            setAmount(Number(input.value) + 1),
        );

        // an amount below 1 (or a cleared field) removes the line
        function setAmount(amount) {
            if (amount >= 1) {
                input.value = amount;
            } else {
                row.remove();
                updateSubmitButton();
            }
        }

        return [minus, input, plus];
    }

    function createStepButton(icon) {
        const button = document.createElement("button");
        button.type = "button"; // the default type would submit the form
        button.className = "receipt-step raised";
        const image = document.createElement("img");
        image.src = icon;
        image.alt = "";
        button.append(image);
        return button;
    }

    function findRow(barcode) {
        for (const row of rows.children) {
            if (row.dataset.barcode === barcode) {
                return row;
            }
        }
        return null;
    }

    function highlight(row) {
        row.classList.remove("scanned");
        void row.offsetWidth; // makes the browser notice the removal
        row.classList.add("scanned");
    }

    function updateSubmitButton() {
        submitButton.disabled = rows.children.length === 0;
    }

    updateSubmitButton();
}

// a queue of "unknown barcode" prompts, so they open one at a time.
// it always holds the last prompt in line; each new prompt chains onto it with .then,
// so it opens only once the one before it is closed.
// it starts as an already-finished promise so the first prompt opens right away.
let unknownPrompts = Promise.resolve();

// opens the register dialog with the barcode filled in. returns the entered name, or null if cancelled.
async function registerUnknown(barcode) {
    const dialog = document.getElementById("register-consumable-dialog");
    const form = dialog.querySelector("form");
    let name = null;
    // script.js does the registering; this only notices that it happened
    const onSubmit = () =>
        (name = document.getElementById("item-type-name").value);
    form.addEventListener("submit", onSubmit);
    document
        .getElementById("item-id")
        .closest(".barcode-field").dataset.prefill = barcode;
    dialog.showModal();
    await waitForClose(dialog);
    form.removeEventListener("submit", onSubmit);
    return name;
}

// lets someone who isn't logged in scan their staff ID. returns whether they logged in.
async function askForStaffId() {
    const dialog = document.getElementById("staff-id-dialog");
    const message = dialog.querySelector(".staff-id-message");
    message.textContent = "Scan your staff ID to register a new item type.";
    let loggedIn = false;
    // with a pattern, this claim gets chosen instead of the receipt's own pattern-less one (see findClaim in scanning.js)
    const release = claimScans((barcode) => {
        if (loginIds.includes(barcode)) {
            logIn(barcode);
            loggedIn = isLoggedIn(); // logIn refuses outside the root views
            dialog.close();
        } else {
            message.textContent = "That isn't a staff ID. Try again.";
        }
    }, /.*/); // the pattern matches everything
    dialog.showModal();
    await waitForClose(dialog);
    release();
    return loggedIn;
}

function waitForClose(dialog) {
    return new Promise((resolve) =>
        dialog.addEventListener("close", resolve, { once: true }),
    );
}

// shows every line with a ✓ or ✗ once its request is done
export function showReceiptResults(title, items, applyItem) {
    const dialog = document.getElementById("receipt-results-dialog"); // this one is not specific to any receipt, and lives outside the views and modes in the HTML, which is why we use getElementById just this once
    const rows = dialog.querySelector("tbody");
    dialog.querySelector("h2").textContent = title;
    rows.replaceChildren();
    for (const item of items) {
        const row = rows.insertRow();
        row.insertCell().textContent = item.name;
        const amountCell = row.insertCell();
        amountCell.className = "receipt-amount";
        amountCell.textContent = item.amount;
        const statusCell = row.insertCell();
        statusCell.className = "receipt-status";
        statusCell.textContent = "…";
        applyItem(item)
            .then(() => {
                statusCell.textContent = "✓";
                statusCell.classList.add("succeeded");
            })
            .catch((error) => {
                console.error(error);
                statusCell.textContent = "✗";
                statusCell.classList.add("failed");
            });
    }
    dialog.showModal();
}

// the receipt's lines as [{ barcode, name, amount }]
export function getReceiptItems(receipt) {
    const items = [];
    for (const row of receipt.querySelector("tbody").children) {
        const amount = Number(row.querySelector(".receipt-amount input").value);
        const name = row.querySelector(".receipt-name").textContent;
        items.push({
            barcode: row.dataset.barcode,
            name: name,
            amount: amount,
        });
    }
    return items;
}
