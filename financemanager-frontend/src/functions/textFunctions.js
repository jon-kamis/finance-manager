export function formatFmText(text) {
    switch (text) {
        case "paycheck":
            return "Paycheck";
        case "semi-monthly":
            return "Semi Monthly"
        case "bi-weekly":
            return "Bi-Weekly"
        case "quarterly":
            return "Quarterly"
        case "annual":
            return "Annual"
        case "income":
            return "Income";
        case "expense":
            return "Expense"
        default:
            return text;
    }
}