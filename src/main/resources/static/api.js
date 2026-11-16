// Calls to the backend.

function registerItemType(itemType) {
    return fetch("registerItemType", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(itemType),
    }).then((response) => {
        if (!response.ok) {
            throw new Error("registerItemType failed: " + response.status);
        }
        return response.text();
    });
}
