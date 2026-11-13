const modeSwitch = document.getElementById("modeSwitch");
const modeOptions = document.querySelectorAll(".mode-option");

modeOptions.forEach((button) => {
    button.addEventListener("click", () => {
        modeOptions.forEach((option) => {
            option.classList.remove("active");
        });

        button.classList.add("active");

        if (button.dataset.mode === "loan") {
            modeSwitch.classList.add("loan");
        } else {
            modeSwitch.classList.remove("loan");
        }

        console.log("Selected mode:", button.dataset.mode);
    });
});

function hideById(id) {
    let element = document.getElementById(id);
    element.classList.add("invisible");
}

function showById(id) {
    let element = document.getElementById(id);
    element.classList.remove("invisible");
}

let viewHistory = [];
showView("defaultView", false);

function showView(id, showBack) {
    viewHistory.push(id);
    let views = document.getElementsByClassName("view");
    for (const view of views) {
        view.classList.add("invisible");
    }
    showById(id);
    hideById("logoutButton");
    if (showBack) {
        showById("backButton");
    } else {
        hideById("backButton");
    }
}

function previousView() {
    let previousViewId = viewHistory[viewHistory.length - 2];
    showView(previousViewId);
    if (previousViewId === "defaultView" && loggedInAs) {
        showById("logoutButton");
    }
    viewHistory.pop();
    viewHistory.pop();
}

let loggedInAs = null;

function logIn(staffId) {
    loggedInAs = staffId;
    hideById("scanIdPrompt");
    showById("moreOptions");
    showById("logoutButton");
}

function logOut() {
    loggedInAs = null;
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
}

// scanners type a whole barcode in a few ms
const SCAN_KEY_GAP_MS = 20;

// barcodes are usually longer than a few characters, so less than that might be random keystrokes
const MIN_BARCODE_LENGTH = 3;

let lastKeyTime = Date.now();
let buffer = "";

addEventListener("keydown", (e) => {
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

const validIds = ["5741000134894"];

function onScan(buffer) {
    console.log(`Got barcode ${buffer}`);
    if (validIds.includes(buffer)) {
        logIn(buffer);
    }
}
