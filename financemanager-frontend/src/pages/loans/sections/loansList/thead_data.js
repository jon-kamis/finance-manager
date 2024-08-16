const data = [
    {
        id: "name",
        numeric: false,
        disablePadding: true,
        label: "Name",
        align: "left"
    },
    {
        id: "principal",
        numeric: false,
        disablePadding: true,
        label: "Principal",
        align: "right"
    },
    {
        id: "startDate",
        numeric: false,
        disablePadding: true,
        label: "First Payment",
    },
    {
        id: "frequency",
        numeric: false,
        disablePadding: true,
        label: "Pay Frequency",
    },
    {
        id: "payment",
        numeric: true,
        disablePadding: true,
        label: "Payment",
        align: "right"
    },
    {
        id: "rate",
        numeric: true,
        disablePadding: true,
        label: "Rate (%)",
        align: "right",
    },
    {
        id: "term",
        numeric: true,
        disablePadding: true,
        label: "Term",
        align: "right",
    }
];

export default data;