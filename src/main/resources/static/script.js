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
    if (previousViewId === "defaultView") {
        showById("logoutButton");
    }
    viewHistory.pop();
    viewHistory.pop();
}

function logIn() {
    hideById("scanIdPrompt");
    showById("moreOptions");
    showById("logoutButton");
}

function logOut() {
    showById("scanIdPrompt");
    hideById("moreOptions");
    hideById("logoutButton");
}

addEventListener("keydown", (e) => {
    console.log(e.key);
});
