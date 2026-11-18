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
