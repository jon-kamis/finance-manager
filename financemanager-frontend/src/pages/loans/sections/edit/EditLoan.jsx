import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context'
import { payFrequencies, UserApi } from '../../../../app-properties';
import Toast from '../../../../components/alerting/Toast';
import Input from '../../../../components/form/Input';
import Select from '../../../../components/form/Select';

const emptyLoanReq = {
    name: "",
    principal: 0,
    term: 0,
    rate: 0,
    firstPaymentDate: "",
    frequency: "monthly"
}

const EditLoan = () => {
    const { loan, setLoan, jwt, user } = useUserContext();
    const [loanRequest, setLoanRequest] = useState(emptyLoanReq);

    useEffect(() => {
        handleReset();
    }, [loan])

    const handleReset = () => {
        setLoanRequest({
            ...loan,
            rate: (loan.rate * 100),
            firstPaymentDate: (loan.firstPaymentDate.split('T')[0])
        });
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

        let req = loanRequest;
        req.rate = loanRequest.rate / 100;

        const requestOptions = {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
            body: JSON.stringify(req),
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
                    Toast(`Updated Loan ${loanRequest.name}`, "success");
                    setLoan(data)
                    setLoanRequest(emptyLoanReq)
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    return (
        <>
            <h2>Edit {loan.name}</h2>
            <div className="editLoan__form">
                <form onSubmit={handleSubmit}>
                    <Input
                        title={"Name"}
                        type={"text"}
                        className={"editCompare-form"}
                        name={"name"}
                        value={loanRequest.name}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Principal"}
                        type={"number"}
                        className={"editCompare-form"}
                        name={"principal"}
                        value={loanRequest.principal}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Term (Months)"}
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
                        title={"First Payment Due"}
                        type={"date"}
                        className={"editCompare-form"}
                        name={"firstPaymentDate"}
                        value={loanRequest.firstPaymentDate}
                        onChange={handleChange("")}
                    />

                    <Select
                        title={"Payment Frequency"}
                        className={"editCompare-form"}
                        name={"frequency"}
                        value={loanRequest.frequency}
                        onChange={handleChange("")}
                        options={payFrequencies.map(f => ({ id: f.id, value: f.value }))}
                        placeHolder={"Select"}
                    />

                </form>
                <div className="editLoan__inputBtns">
                    <button id="editLoan__saveBtn" className="btn primary" onClick={() => handleSubmit()}>Save</button>
                    <button id="editLoan__restBtn" className="btn primary" onClick={() => handleReset()}>Reset</button>
                </div>
            </div>

        </>
    )
}

export default EditLoan