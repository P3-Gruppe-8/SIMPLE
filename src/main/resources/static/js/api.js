// Calls to the backend's REST API.
import { getStaffId } from "./session.js";
import { getOfficeId } from "./office.js";

// sends a request, with `body` as JSON if given, and returns the server's text reply.
// when someone is logged in, their staff ID is included in a header, and so is the tablet's office.
async function send(method, path, body) {
    const options = { method: method, headers: {} };
    if (body !== undefined) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }
    if (getStaffId() !== null) {
        options.headers["X-Staff-Id"] = getStaffId();
    }
    if (getOfficeId() !== null) {
        options.headers["X-Office-Id"] = getOfficeId();
    }
    const response = await fetch(path, options);
    if (!response.ok) {
        const error = new Error(
            method + " " + path + " failed: " + response.status,
        );
        error.status = response.status; // so callers can tell e.g. 404 from other failures
        throw error;
    }
    return await response.text();
}

// barcodes can contain spaces and other characters that aren't allowed in a URL
function descriptorPath(barcode) {
    return "descriptors/" + encodeURIComponent(barcode);
}

export async function registerItemType(itemType) {
    return await send("POST", "descriptors", itemType);
}

// the item type as { barcode, name, quantity }
export async function getItemDetails(barcode) {
    return JSON.parse(await send("GET", descriptorPath(barcode)));
}

export async function deregisterItemType(barcode) {
    return await send("DELETE", descriptorPath(barcode));
}

// negative writes off, positive restocks
export async function changeStock(barcode, change) {
    return await send("PATCH", descriptorPath(barcode), { change: change });
}

// the item type's possible shelves, as a list of shelf names
export async function getItemTypeLocations(barcode) {
    return JSON.parse(await send("GET", descriptorPath(barcode) + "/shelves"));
}

// replaces the item type's possible shelves with `shelves`, a list of shelf names
export async function setItemTypeLocations(barcode, shelves) {
    return await send("PUT", descriptorPath(barcode) + "/shelves", shelves);
}

export async function addItemTypeLocation(barcode, shelf) {
    return await send(
        "PUT",
        descriptorPath(barcode) + "/shelves/" + encodeURIComponent(shelf),
    );
}

export async function getShelves() {
    const response = await fetch("shelves");
    if (!response.ok) {
        throw new Error("getShelves failed: " + response.status);
    }
    return await response.json();
}

// the offices as a list of { id, name }
export async function getOffices() {
    return JSON.parse(await send("GET", "offices"));
}
