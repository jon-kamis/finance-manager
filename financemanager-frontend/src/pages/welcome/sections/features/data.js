import { FaHandHoldingDollar } from "react-icons/fa6";
import { FaSackDollar } from "react-icons/fa6";
import { AiOutlineStock } from "react-icons/ai";
import { FaBalanceScaleLeft } from "react-icons/fa";

const features = [
    {
        id: 1,
        icon: <FaHandHoldingDollar/>,
        title: "Loans",
        desc: "Track your existing loans, calculate new payments, and see how your payment could change with comparisons"
    },
    {
        id: 2,
        icon: <FaSackDollar/>,
        title: "Incomes",
        desc: "Track your incomes, including estimated tax on each paycheck"
    },
    {
        id: 3,
        icon: <AiOutlineStock/>,
        title: "Stocks",
        desc: "Track your stock portfolio balance and view historic graphs accurate up to 24hours in the past"
    },
    {
        id: 4,
        icon: <FaBalanceScaleLeft/>,
        title: "Monthly Dashboard",
        desc: "See an estimate on where your money is going each month and how much of your paycheck you will have left"
    }
]

export default features