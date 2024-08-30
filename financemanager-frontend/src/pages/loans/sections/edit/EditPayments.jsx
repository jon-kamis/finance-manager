import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context'
import { LoanApi, UserApi } from '../../../../app-properties';
import Toast from '../../../../components/alerting/Toast';
import Input from '../../../../components/form/Input';
import Card from '../../../../components/Card';
import './edit-compare.css'
import { formatNumber } from '../../../../functions/textFunctions';
import UnpagedTable from '../../../../components/UnpagedTable';
import tableHeadings from './editPayments_headings';
import { format, parseISO } from "date-fns";
import { FiEdit3 } from 'react-icons/fi';
import { FaTrashAlt } from 'react-icons/fa';

const emptyPaymentRequest = {
    amount: 0,
    effectiveDate: "",
    expirationDate: ""
}

const EditPayments = () => {
    const { loan, loanHandler, jwt, user, refreshUserData } = useUserContext();
    const [paymentRequest, setPaymentRequest] = useState(emptyPaymentRequest);
    const [tableData, setTableData] = useState([]);

    useEffect(() => {
        handleReset();
    }, [loan])

    const handleReset = () => {
        setPaymentRequest(emptyPaymentRequest);

        if (loan && loan.manualPayments) {
            let rows = [];
            loan.manualPayments.map(p => {
                rows.push({
                    id: `editPaymentTable_${p.id}`,
                    data: [
                        { align: "right", value: `$${formatNumber(p.amount)}`},
                        { value: format(parseISO(p.effectiveDate), 'MMM dd yyyy') },
                        { value: p.expirationDate ? format(parseISO(p.expirationDate), 'MMM dd yyyy') : '-'},
                        {
                            align: "right",
                            value: <div className="edit__cards">
                                <span className="edit__card-icon" onClick={() => ""}><FiEdit3 /></span>
                                <span className="edit__card-icon edit__card-delete" onClick={() => deleteLoanPayment(p.id)}><FaTrashAlt /></span>
                            </div>
                        }
                    ]
                });
            });

            setTableData(rows);
        } else {
            setTableData([]);
        }
    }

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;

        setPaymentRequest({
            ...paymentRequest,
            [name]: value,
        })
    }

    const handleSubmit = (event) => {
        event.preventDefault();

        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
            body: JSON.stringify(paymentRequest),
        }

        fetch(`${LoanApi}/${loan.id}/payments`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    updateLoan();
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    const updateLoan = () => {
        const requestOptions = {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
        }

        fetch(`${UserApi}/${user.userId}/loans/${loan.id}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    loanHandler(data);
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    function deleteLoanPayment(id) {
        const requestOptions = {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
        }

        fetch(`${LoanApi}/${loan.id}/payments/${id}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    Toast("Deleted Successfully", "success");
                    updateLoan();
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    return (
        <>
            <section key="editCompareLoan">
                <div className="container editLoan__container">
                    <div className="editLoan__tool-page">
                        <div className="simChanges__container">
                            <Card className="editLoan__form-container">

                                <h2>Edit {loan.name} Payments</h2>
                                <h3>Standard Payment is ${formatNumber(loan.payment)}</h3>
                                <div className="editLoan__form">
                                    <form onSubmit={handleSubmit}>

                                        <Input
                                            title={"Amount*"}
                                            type={"number"}
                                            className={"editCompare-form"}
                                            name={"amount"}
                                            value={paymentRequest.amount}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            title={"Start Date*"}
                                            type={"date"}
                                            className={"editCompare-form"}
                                            name={"effectiveDate"}
                                            value={paymentRequest.effectiveDate}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            title={"End Date"}
                                            type={"date"}
                                            className={"editCompare-form"}
                                            name={"expirationDate"}
                                            value={paymentRequest.expirationDate}
                                            onChange={handleChange("")}
                                        />
                                        <Input
                                            type="submit"
                                            className="btn primary"
                                            value="Submit"
                                            onClick={handleSubmit}
                                        />
                                        <small>* indicates required field</small>
                                    </form>

                                </div>
                            </Card>
                        </div>
                    </div>
                </div>
            </section>

            <section id="loanPayment__list">

                <div className="container container__editPaymentList">
                    <h1>Existing Payments</h1>
                    <UnpagedTable headings={tableHeadings} rows={tableData} />
                </div>
            </section>
        </>
    )
}

export default EditPayments