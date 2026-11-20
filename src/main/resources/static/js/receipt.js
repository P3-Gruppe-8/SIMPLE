// A receipt: a table of scanned item types and how many of each. Scanning the same barcode again adds one.

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
            row.insertCell().textContent = barcode; // TODO: show the item type's name once the API returns it
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

    // a minus button, the amount field and a plus button
    function createAmountControls(row) {
        // text, not type="number", so a scan into it can be taken back out (see scanning.js)
        // additionally, type="number" would not save you from accidental scans as barcodes usually consist of... numbers......
        // but since the inputMode is set to numeric you basically can't input letters from the iPad anyway
        const input = document.createElement("input");
        input.type = "text";
        input.inputMode = "numeric";
        input.value = 1;
        input.addEventListener(
            "change",
            () => setAmount(Math.floor(Number(input.value))), // if the input value somehow contains text the whole thing gets cleared, and decimals get floored
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

// shows every line with a ✓ or ✗ once its request is done
export function showReceiptResults(title, items, applyItem) {
    const dialog = document.getElementById("receipt-results-dialog"); // this one is not specific to any receipt, and lives outside the views and modes in the HTML, which is why we use getElementById just this once
    const rows = dialog.querySelector("tbody");
    dialog.querySelector("h2").textContent = title;
    rows.replaceChildren();
    for (const item of items) {
        const row = rows.insertRow();
        row.insertCell().textContent = item.barcode;
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

// the receipt's lines as [{ barcode, amount }]
export function getReceiptItems(receipt) {
    const items = [];
    for (const row of receipt.querySelector("tbody").children) {
        const amount = Number(row.querySelector(".receipt-amount input").value);
        items.push({ barcode: row.dataset.barcode, amount: amount });
    }
    return items;
}
