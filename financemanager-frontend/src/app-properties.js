export const ApiUrl = "http://localhost:8080/api"
export const AuthUrl = "/api/auth/login"
export const RefreshUrl = "/api/auth/refresh"
export const RegisterUrl = "/api/auth/register"
export const UserApi = "/api/users"
export const leastDigitsFormatOptions = { maximumFractionDigits: 2, minimumFractionDigits: 0 }
export const numberFormatOptions = { maximumFractionDigits: 2, minimumFractionDigits: 2 }
export const rateFormatOptions = { maximumFractionDigits: 3, minimumFractionDigits: 2 }
export const payFrequencies = [
    { id: 'weekly', value: 'Weekly' },
    { id: 'bi-weekly', value: 'Bi-Weekly' },
    { id: 'monthly', value: 'Monthly' },
    { id: 'semi-monthly', value: 'Semi-Monthly' },
    { id: 'quarterly', value: 'Quarterly' },
    { id: 'annual', value: 'Annual' },
];