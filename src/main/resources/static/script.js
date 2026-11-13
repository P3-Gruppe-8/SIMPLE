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

let viewHistory = [];
showView("defaultView");

function showView(id) {
    viewHistory.push(id);
    let views = document.getElementsByClassName("view");
    for (const view of views) {
        view.style = "display: none;";
    }
    document.getElementById(id).style = "display: flex;";
}

function hideBack() {
    document.getElementById("backButton").style = "display: none;";
}

function showBack() {
    document.getElementById("backButton").style = "display: flex;";
}

function previousView() {
    showView(viewHistory[viewHistory.length - 2]);
    viewHistory.pop();
    viewHistory.pop();
}
