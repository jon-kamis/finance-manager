import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context'
import { intFormatOptions, LoanApi, numberFormatOptions, rateFormatOptions } from '../../../../app-properties';
import Toast from '../../../../components/alerting/Toast';
import Input from '../../../../components/form/Input';
import Card from '../../../../components/Card';
import './edit-compare.css'
import { formatInt, formatLoanTerm, formatNumber } from '../../../../functions/textFunctions';
import { format, parseISO } from "date-fns";
import UnpagedTable from '../../../../components/UnpagedTable';
import tableHeadings from './thead_data'

const emptyLoanReq = {
    name: "",
    balance: 0,
    term: 0,
    rate: 0,
    firstPaymentDate: "",
    frequency: "monthly"
}

const SimulateLoanChanges = () => {
    const { loan, setLoan, jwt, user } = useUserContext();
    const [loanRequest, setLoanRequest] = useState(emptyLoanReq);
    const [changes, setChanges] = useState([]);
    const [loanComparison, setLoanComparison] = useState()
    const [tableData, setTableData] = useState([]);

    useEffect(() => {
        handleReset();
    }, [loan])

    useEffect(() => {
        let changeArr = [];

        console.log("Checking for loan changes")

        if (Intl.NumberFormat("en-US", rateFormatOptions).format(loan.rate * 100) != loanRequest.rate) {
            console.log("loan rate has changed")
            changeArr.push(`Rate: ${Intl.NumberFormat("en-US", rateFormatOptions).format(loan.rate * 100)}% -> ${loanRequest.rate}%`)
        }

        if ((loan.term - loan.currentPaymentNumber) != loanRequest.term) {
            console.log("term has changed")
            changeArr.push(`Term: ${loan.term - loan.currentPaymentNumber} -> ${loanRequest.term}`)
        }

        if (loan.balance != loanRequest.balance) {
            console.log("principal has changed")
            changeArr.push(`Principal: $${loan.balance} -> $${loanRequest.balance}`)
        }

        if (loan.payment != loanRequest.payment) {
            console.log("payment has changed")
            changeArr.push(`Payment: $${loan.payment} -> $${loanRequest.payment}`)
        }

        setChanges(changeArr);
    }, [loanRequest])

    const handleReset = () => {
        setLoanRequest({
            ...loan,
            rate: Intl.NumberFormat("en-US", rateFormatOptions).format(loan.rate * 100),
            principal: loan.balance,
            firstPaymentDate: (loan.firstPaymentDate.split('T')[0]),
            term: (loan.term - loan.currentPaymentNumber)
        });
        setLoanComparison();
    }

    const getNetHeading = (val, type) => {
        if (type === "rate") {
            return (<h2 className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}${Intl.NumberFormat("en-US", rateFormatOptions).format(Math.abs(val))}`}</h2>);
        } else if (type === "term") {
            return (<h2 className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}${formatLoanTerm(Math.abs(val))}`}</h2>);
        } else {
            return (<h2 className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}$${Intl.NumberFormat("en-US", numberFormatOptions).format(Math.abs(val))}`}</h2>);
        }
    }

    const getNetText = (val, type) => {

        if (val == 0) {
            return (<p>-</p>);
        }

        if (type === "rate") {
            return (<p className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}${Intl.NumberFormat("en-US", rateFormatOptions).format(Math.abs(val))}`}</p>);
        } else if (type === "term") {
            return (<p className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}${Intl.NumberFormat("en-US", intFormatOptions).format(Math.abs(val))}`}</p>);
        } else if (type === "principal") {
            return (<p className={val < 0 ? "simChanges__card-red" : val > 0 ? "simChanges__card-green" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}$${Intl.NumberFormat("en-US", numberFormatOptions).format(Math.abs(val))}`}</p>);
        } else {
            return (<p className={val < 0 ? "simChanges__card-green" : val > 0 ? "simChanges__card-red" : ""}>{`${val < 0 ? "- " : val > 0 ? "+ " : ""}$${Intl.NumberFormat("en-US", numberFormatOptions).format(Math.abs(val))}`}</p>);
        }
    }

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;

        setLoanRequest({
            ...loanRequest,
            [name]: value,
        })
    }

    const handleSubmit = () => {

        let req = {
            frequency: loan.frequency,
            firstPaymentDate: loan.paymentSchedule[loan.currentPaymentNumber -1].paymentDate,
            originalLoan: {  
                term: loan.term - loan.currentPaymentNumber,
                principal: loan.balance,
                rate: loan.rate
            },
            newLoan: {
                term: (loanRequest.term),
                principal: loanRequest.principal,
                rate: loanRequest.rate / 100,
                payment: loanRequest.payment != loan.payment ? loanRequest.payment : ""
            }
        }

        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
            body: JSON.stringify(req),
        }

        fetch(`${LoanApi}/compare`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    setLoanComparison(data);
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    useEffect(() => {

        if (loanComparison && loanComparison.paymentSchedule) {
            let rows = [];
            loanComparison.paymentSchedule.map(p => {
                rows.push({
                    id: `simChangesTable__${p.paymentNumber}`,
                    data: [
                        { value: format(parseISO(p.paymentDate), 'MMM do yyyy') },
                        { align: "right", value: <p>${formatNumber(p.newPayment.principal)}<br/>{getNetText(p.netPayment.principal, p.newPayment.principal == 0 || p.originalPayment.principal == 0 ? "" : "principal")}</p>},
                        { align: "right", value: <p>${formatNumber(p.newPayment.principalToDate)}<br/>{getNetText(p.netPayment.principalToDate, "principal")}</p>},
                        { align: "right", value: <p>${formatNumber(p.newPayment.interest)}<br/>{getNetText(p.netPayment.interest)}</p>},
                        { align: "right", value: <p>${formatNumber(p.newPayment.interestToDate)}<br/>{getNetText(p.netPayment.interestToDate)}</p>},
                        { align: "right", value: <p>${formatNumber(p.newPayment.amount)}<br/>{getNetText(p.netPayment.amount)}</p>},
                        { align: "right", value: <p>${formatNumber(p.newPayment.balance)}<br/>{getNetText(p.netPayment.balance)}</p>},
                    ]
                });
            });

            setTableData(rows);
        } else {
            setTableData([]);
        }

    }, loanComparison)

    return (
        <>
            <section key="editCompareLoan">
                <div className="container editLoan__container">
                    <div className="editLoan__tool-page">
                        <div className="simChanges__container">
                            <Card className="editLoan__form-container">

                                <h2>Edit {loan.name}</h2>
                                <div className="editLoan__form">
                                    <form onSubmit={handleSubmit}>

                                        <Input
                                            title={"Balance"}
                                            type={"number"}
                                            className={"editCompare-form"}
                                            name={"balance"}
                                            value={loanRequest.balance}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            title={"Remaining Term (months)"}
                                            type={"number"}
                                            className={"editCompare-form"}
                                            name={"term"}
                                            value={loanRequest.term}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            title={"Rate (%)"}
                                            type={"number"}
                                            className={"editCompare-form"}
                                            name={"rate"}
                                            value={loanRequest.rate}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            title={"Payment"}
                                            type={"number"}
                                            className={"editCompare-form"}
                                            name={"payment"}
                                            value={loanRequest.payment}
                                            onChange={handleChange("")}
                                        />

                                    </form>
                                    <div className="editLoan__inputBtns">
                                        <button id="editLoan__saveBtn" className="btn primary" onClick={() => handleSubmit()}>Compare Changes</button>
                                        <button id="editLoan__restBtn" className="btn primary" onClick={() => handleReset()}>Reset</button>
                                    </div>

                                    <div className="simChanges__changes-box">
                                        {
                                            changes.length > 0 &&
                                            <div className="simChanges__changeList">
                                                <h2>Changes:</h2>
                                                {changes.map(c => (
                                                    <div className="simChanges__changeList-item"><h3>{c}</h3></div>
                                                ))}
                                            </div>
                                        }
                                    </div>
                                </div>
                                <small>NOTE: Comparison values simulate refinancing a loan and do not take payments made so far into account</small>
                            </Card>
                        </div>
                    </div>
                </div>
            </section>

            {loanComparison &&
                <section key="simLoan__changes">
                    <div className="container editLoan__container">
                        <div className="simChanges__cards">
                            <Card className="simChanges__card">
                                <h3>Original Loan</h3>
                                <h5>Minimum Payment</h5>
                                <h2>${formatNumber(loanComparison.originalSummary.payment)}</h2>
                                <h5>Interest</h5>
                                <h2>${formatNumber(loanComparison.originalSummary.interest)}</h2>
                                <h5>Term</h5>
                                <h2>{formatLoanTerm(loanComparison.originalSummary.term)}</h2>
                            </Card>
                            <Card className="simChanges__card">
                                <h3>New Loan</h3>
                                <h5>Minimum Payment</h5>
                                <h2>${formatNumber(loanComparison.compareSummary.payment)}</h2>
                                <h5>Interest</h5>
                                <h2>${formatNumber(loanComparison.compareSummary.interest)}</h2>
                                <h5>Term</h5>
                                <h2>{formatLoanTerm(loanComparison.compareSummary.term)}</h2>
                            </Card>
                            <Card className="simChanges__card">
                                <h3>Net Changes</h3>
                                <h5>Minimum Payment</h5>
                                <h2>{getNetHeading(loanComparison.netSummary.payment, "number")}</h2>
                                <h5>Interest</h5>
                                <h2>{getNetHeading(loanComparison.netSummary.interest)}</h2>
                                <h5>Term</h5>
                                <h2>{getNetHeading(loanComparison.netSummary.term, "term")}</h2>
                            </Card>

                        </div>
                        <div className="simChanges__table">
                            <UnpagedTable headings={tableHeadings} rows={tableData} />
                        </div>
                    </div>
                </section >
            }
        </>
    )
}

export default SimulateLoanChanges