// tracks the login state

let loggedInAs = null;

// staff ID codes look like "nowaco staff USERNAME", like the shelf codes
const STAFF_CODE = /^nowaco staff (.+)$/;

export function staffUsername(barcode) {
    const match = barcode.match(STAFF_CODE);
    if (match === null) {
        return null;
    }
    return match[1];
}

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
