// Calls to the backend's REST API.

// sends a request, with `body` as JSON if given, and returns the server's text reply
async function send(method, path, body) {
    const options = { method: method };
    if (body !== undefined) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
    }
    const response = await fetch(path, options);
    if (!response.ok) {
        throw new Error(method + " " + path + " failed: " + response.status);
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

export async function addItemTypeLocation(barcode, shelf) {
    return await send(
        "PUT",
        descriptorPath(barcode) + "/shelves/" + encodeURIComponent(shelf),
    );
}

export async function removeItemTypeLocation(barcode, shelf) {
    return await send(
        "DELETE",
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
