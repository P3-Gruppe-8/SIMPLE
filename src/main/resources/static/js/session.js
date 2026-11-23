// tracks the login state

let loggedInAs = null;

export const loginIds = ["victor nummer 2", "leon da boss"];

export function isLoggedIn() {
    return loggedInAs !== null;
}

// the scanned staff ID, or null when nobody is logged in
export function getStaffId() {
    return loggedInAs;
}

export function startSession(staffId) {
    loggedInAs = staffId;
}

export function endSession() {
    loggedInAs = null;
}
