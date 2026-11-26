// Debug menu: the tablet's office, and a log of what the global keydown listener receives.
import { getOffices } from "./api.js";
import { getOfficeId, setOfficeId } from "./office.js";

const log = document.getElementById("debugKeyLog");
const officeSelect = document.getElementById("debugOffice");

function addLine(text) {
    const line = document.createElement("li");
    line.textContent = text;
    log.prepend(line);
}

export function logKey(event, gapMs) {
    addLine(`key="${event.key}" code=${event.code} gap=${gapMs}ms`);
}

export function logScan(buffer) {
    addLine(`SCAN "${buffer}" (${buffer.length} chars)`);
}

export async function loadOffices() {
    try {
        const offices = await getOffices();
        const placeholder = new Option("Choose an office...", "", true, true);
        placeholder.disabled = true;
        officeSelect.replaceChildren(placeholder);
        for (const office of offices) {
            officeSelect.append(new Option(office.name, office.id));
        }
        officeSelect.value = getOfficeId() ?? "";
    } catch (error) {
        console.error(error);
        officeSelect.options[0].textContent = "Couldn't load offices";
    }
}

officeSelect.addEventListener("change", () => setOfficeId(officeSelect.value));
