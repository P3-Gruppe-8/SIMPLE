// Calls to the backend.

export async function registerItemType(itemType) {
    const response = await fetch("registerItemType", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(itemType),
    });
    if (!response.ok) {
        throw new Error("registerItemType failed: " + response.status);
    }
    return await response.text();
}

export async function writeOff(options) {
    const response = await fetch("writeOff", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(options),
    });
    if (!response.ok) {
        throw new Error("writeOff failed: " + response.status);
    }
    return await response.text();
}

export async function getShelves() {
    const response = await fetch("shelves");
    if (!response.ok) {
        throw new Error("getShelves failed: " + response.status);
    }
    return await response.json();
}
