import React from 'react'
import './monthlyTransactions.css'

const MonthlyTransactions = () => {

    const getCurMonth = () => {
        let curMonth = new Date().getMonth();

        switch (curMonth) {
            case 0:
                return "January"
            case 1:
                return "February"
            case 2:
                return "March"
            case 3:
                return "April"
            case 4:
                return "May"
            case 5:
                return "June"
            case 6:
                return "July"
            case 7:
                return "August"
            case 8:
                return "September"
            case 9:
                return "October"
            case 10:
                return "November"
            case 11:
                return "December"
            }
    }

    return (
        <section id="monthly-transactions">
            <h2>{getCurMonth()} Transactions</h2>

            <div className="container monthlyTransactions__container">
            </div>
        </section>
    )
}

export default MonthlyTransactions