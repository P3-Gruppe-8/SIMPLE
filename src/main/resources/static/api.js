// Calls to the backend.

function registerItemType(itemType) {
    return fetch("registerItemType", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(itemType),
    }).then((response) => response.text());
}
