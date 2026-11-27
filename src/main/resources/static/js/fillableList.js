export function setUpFillableList(element) {
    const list = element.querySelector(".search-result");
    if (!list) return;

    const source = list.querySelector("li a")
    if (!source) return;

    const fields = source.textContent
        .split(";")
        .map(field => field.trim())
        .filter(Boolean)

    const container = document.createElement("div");
    container.className = "fillable-fields";

    fields.forEach((fieldName, index) => {
        const id = `fillable-field-${index}`;

        const wrapper = document.createElement("div");
        wrapper.className = "fillable-field";

        const label = document.createElement("label");
        label.htmlFor = id;
        label.textContent = fieldName;

        const input = document.createElement("input");
        input.type = "text";
        input.id = id;
        input.required = true;
        input.name = fieldName;
        input.dataset.field = fieldName;
        input.placeholder = `Enter ${fieldName.toLowerCase()}...`;

        wrapper.append(label, input);
        container.append(wrapper);

    });

    list.replaceWith(container);





}