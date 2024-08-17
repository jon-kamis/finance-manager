import { leastDigitsFormatOptions } from "../app-properties";

export function formatFmText(text) {
    switch (text) {
        case "paycheck":
            return "Paycheck";
        case "taxes":
            return "Taxes";
        case "bill":
            return "Bill";
        case "benefit":
            return "Benefit"
        case "loan-payment":
            return "Loan"
        case "semi-monthly":
            return "Semi Monthly"
        case "monthly":
            return "Monthly"
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

export function formatLoanTerm(term) {
    if (term < 100) {
        return `${term} months`
    } else {
        return `${Intl.NumberFormat("en-US", leastDigitsFormatOptions).format(term / 12)} years`
    }
}