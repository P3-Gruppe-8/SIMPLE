// The log in dialog: scan a staff ID code, or type the staff ID if the scanner isn't working.
import { logIn } from "./navigation.js";
import { isLoggedIn, staffUsername } from "./session.js";

const dialog = document.getElementById("staff-id-dialog");
const message = dialog.querySelector(".staff-id-message");
const input = document.getElementById("staff-id-entry");
const field = input.closest(".barcode-field");

// shows `prompt` and waits for a staff ID. returns whether they logged in.
export async function askForStaffId(prompt) {
    message.textContent = prompt;
    let loggedIn = false;

    // a scanned code reads "nowaco staff USERNAME", a typed ID is just the username
    async function tryLogIn() {
        const username = staffUsername(input.value) ?? input.value.trim();
        if (!username) {
            return;
        }
        try {
            await logIn(username);
        } catch (error) {
            console.error(error);
            message.textContent = error.message + ". Try again.";
            return;
        }
        loggedIn = isLoggedIn(); // logIn refuses outside the root views
        dialog.close();
    }

    field.addEventListener("scan", tryLogIn);
    dialog.showModal();
    await new Promise((resolve) =>
        dialog.addEventListener("close", resolve, { once: true }),
    );
    field.removeEventListener("scan", tryLogIn);
    return loggedIn;
}
