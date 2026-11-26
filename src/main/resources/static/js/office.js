// The office this tablet belongs to, chosen in the debug menu and remembered by the browser.

const STORAGE_KEY = "officeId";

// the chosen office's ID, or null if none is chosen yet
export function getOfficeId() {
    try {
        return localStorage.getItem(STORAGE_KEY);
    } catch (error) {
        return null; // storage can be blocked, e.g. in private browsing
    }
}

export function setOfficeId(officeId) {
    try {
        localStorage.setItem(STORAGE_KEY, officeId);
    } catch (error) {
        console.error(error);
    }
}
