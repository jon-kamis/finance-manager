import React, { useState } from 'react'
import Modal from '../../components/Modal'
import Input from '../../components/form/Input'
import './loanModals.css'
import Toast from '../../components/alerting/Toast';
import { useModalContext } from '../../context/modal-context';
import { payFrequencies, UserApi } from '../../app-properties';
import { useUserContext } from '../../context/user-context';
import Select from '../../components/form/Select';

const emptyLoanReq = {
    name: "",
    term: 0,
    firstPaymentDate: 0,
    principal: 0,
    rate: 0,
    frequency: "monthly"
};

const CreateIncome = () => {
    const { closeModalHandler } = useModalContext();
    const { user, jwt, refreshUserData } = useUserContext();
    const [loanRequest, setLoanRequest] = useState(emptyLoanReq)

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setLoanRequest({
            ...loanRequest,
            [name]: value,
        })
    }

    const handleSubmit = (event) => {
        event.preventDefault();

        let req = loanRequest;
        req.rate = loanRequest.rate / 100;

        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
            body: JSON.stringify(req),
        }

        fetch(`${UserApi}/${user.userId}/loans`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    Toast(`Created Loan ${loanRequest.name}`, "success");
                    setLoanRequest(emptyLoanReq)
                    closeModalHandler();
                    refreshUserData(Date.now());
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    return (
        <Modal className="loan__modal" name="loan__create">
            <h5>New Loan</h5>

            <div className="loan__wrapper">
                <form onSubmit={handleSubmit}>
                    <Input
                        title={"Name"}
                        type={"text"}
                        className={"form-control"}
                        name={"name"}
                        value={loanRequest.name}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Principal"}
                        type={"number"}
                        className={"form-control"}
                        name={"principal"}
                        value={loanRequest.principal}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Term (Months)"}
                        type={"number"}
                        className={"form-control"}
                        name={"term"}
                        value={loanRequest.term}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Rate (%)"}
                        type={"number"}
                        className={"form-control"}
                        name={"rate"}
                        value={loanRequest.rate}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"First Payment Due"}
                        type={"date"}
                        className={"form-control"}
                        name={"firstPaymentDate"}
                        value={loanRequest.firstPaymentDate}
                        onChange={handleChange("")}
                    />

                    <Select
                        title={"Payment Frequency"}
                        className={"form-control"}
                        name={"frequency"}
                        value={loanRequest.frequency}
                        onChange={handleChange("")}
                        options={payFrequencies.map(f => ({ id: f.id, value: f.value }))}
                        placeHolder={"Select"}
                    />

                    <Input
                        type="submit"
                        className="btn primary"
                        value="Create"
                        onClick={handleSubmit}
                    />
                </form>
            </div>
        </Modal>
    )
}

export default CreateIncome