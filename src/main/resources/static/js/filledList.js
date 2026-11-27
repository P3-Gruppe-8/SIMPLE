export function setUpFilledList(element) {
    const list = element.querySelector(".search-result");
    if (!list) return;

    const source = list.querySelector("li a")
    if (!source) return;

    const fields = source.textContent
        .split(";")
        .map(field => field.trim())
        .filter(Boolean)

    const container = document.createElement("div");
    container.className = "filled-fields";

    fields.forEach((fieldName, index) => {
        const id = `filled-field-${index}`;

        const wrapper = document.createElement("div");
        wrapper.className = "filled-field";

        const label = document.createElement("label");
        label.htmlFor = id;
        label.textContent = fieldName;

        wrapper.append(label);
        container.append(wrapper);

    });

    list.replaceWith(container);

}