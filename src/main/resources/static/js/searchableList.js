export function setUpSearchableList(element) {
    // Declare variables
    const input = element.querySelector(".input-list-search");
    const ul = element.querySelector(".search-result");

    let selectedItem = null;

    // On change of input in searchfield filter list items
    element.addEventListener("input", (e) => {
        const filter = input.value.toLowerCase();

        // Loop through all list items, and hide those who don't match the search query
        for (const li of ul.querySelectorAll("li")) {
            const matches = li.textContent.toLowerCase().includes(filter)
            li.style.display = matches ? "" : "none";
        }
    });

    // Make list items clickable
    ul.addEventListener("click", (e) => {
        const li = e.target.closest("li");
        if (!li || !ul.contains(li)) return;

        e.preventDefault(); // Stop the <a href="#"> from jumping the page MAYBE DELETE IN FINAL

        // Clear previous selection
        if (selectedItem) {
            selectedItem.classList.remove("selected")
            selectedItem.removeAttribute("aria-selected");
        }

        // Select new item
        selectedItem = li;
        li.classList.add("selected")
        li.setAttribute("aria-selected", "true");

        // Show choice in input field MAYBE REMOVE
        const value = li.textContent.trim()
        input.value = value;

        // Create event of selection
        element.dispatchEvent(new CustomEvent("itemsselected", {
            detail: { value },
            bubbles: true
        }));

    })

    function reset() {
        if (selectedItem) {
            selectedItem.classList.remove("selected")
            selectedItem.removeAttribute("aria-selected");
            selectedItem = null;
        }

        input.value = "";

    }

    input.closest("dialog")?.addEventListener("toggle", (e) => {
        reset();
    });

}