import { show, hide } from "./navigation.js";

export function setUpMultiStepDialog(dialog) {
    const form = dialog.querySelector("form");
    const steps = [...form.querySelectorAll(".form-step")];
    const cancelButton = form.querySelector(".cancel-button");
    const backButton = form.querySelector(".back-button");
    const nextButton = form.querySelector(".next-button");
    const submitButton = form.querySelector(".submit-button");

    let current = 0;

    show(backButton);

    function showStep(index) {
        current = index;

        steps.forEach((step, i) => {
            if (i === current) {
                show(step);
            } else {
                hide(step);
            }
        })

        const isFirst = current === 0;
        const isLast = current === steps.length - 1;

        if (isFirst) {
            hide(backButton);
            show(cancelButton);
        } else {
            show(backButton);
            hide(cancelButton);
        }
        if (isLast) {
            hide(nextButton);
            show(submitButton);
        } else {
            show(nextButton);
            hide(submitButton);
        }

    }

    nextButton.addEventListener("click", () => { showStep(current + 1); })

    backButton.addEventListener("click", () => { showStep(current - 1); })

    dialog.addEventListener("close", () => {
        showStep(0);
    });

    showStep(0);
}