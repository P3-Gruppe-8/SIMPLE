export function setUpDatePicker(picker) {
    const dayUp = picker.querySelector(".date-up-day")
    const dayDown = picker.querySelector(".date-down-day")
    const weekUp = picker.querySelector(".date-up-week")
    const weekDown = picker.querySelector(".date-down-week")

    const input = picker.querySelector(".date-input");

    const setToday = () => {
        input.value = Temporal.Now.plainDateISO().toString();
    };

    setToday();
    picker.closest("dialog")?.addEventListener("toggle", setToday);

    const changeDate = (duration) => {
        const currentDate = input.value ? Temporal.PlainDate.from(input.value) : Temporal.Now.plainDateISO().toString();
        input.value = currentDate.add(duration);
    }

    dayUp.addEventListener("click", () => changeDate({ days: 1 }))

    dayDown.addEventListener("click", () => changeDate({ days: -1 }))

    weekUp.addEventListener("click", (e) => changeDate({ weeks: 1 }))

    weekDown.addEventListener("click", (e) => changeDate({ weeks: -1 }))


}