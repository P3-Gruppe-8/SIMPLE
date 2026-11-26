// Debug menu: logs what the global keydown listener receives.

const log = document.getElementById("debugKeyLog");

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
