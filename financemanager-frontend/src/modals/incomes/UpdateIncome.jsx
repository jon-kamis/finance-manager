import React, { useState } from 'react'
import Modal from '../../components/Modal'
import Input from '../../components/form/Input'
import './incomeModals.css'
import Toast from '../../components/alerting/Toast';
import { useModalContext } from '../../context/modal-context';
import { UserApi } from '../../app-properties';
import Select from '../../components/form/Select';
import { incFrequencies, weekdayData } from './incomeData'
import { useUserContext } from '../../context/user-context';

const emptyIncomeReq = {
    name: "",
    taxWithheld: 0,
    taxCredits: 0,
    taxable: "false",
    filingType: "single",
    category: "paycheck",
    frequency: "semi-monthly",
    weekday: "",
    startDate: "",
    amount: 0,
    daysOfMonth: [],
    effectiveDate: "",
    expirationDate: ""
};

const UpdateIncome = (income) => {
    const { closeModalHandler } = useModalContext();
    const { user, refreshUserData } = useUserContext();
    const [incomeRequest, setIncomeRequest] = useState(emptyIncomeReq)

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setIncomeRequest({
            ...incomeRequest,
            [name]: value,
        })
    }

    function getPayDateFormFields() {
        switch (incomeRequest.frequency) {
            case "weekly":
                return (
                    <Select
                        title={"Payday"}
                        className={"form-control"}
                        name={"weekday"}
                        value={incomeRequest.weekday}
                        onChange={handleChange("")}
                        options={weekdayData.map(w => ({ id: w.id, value: w.value }))}
                        placeHolder={"Select"}
                    />)
            case "semi-monthly":
                return (
                    <Input
                        title={"Days (Separate by comma)"}
                        type={"text"}
                        className={"form-control"}
                        name={"days"}
                        value={incomeRequest.days}
                        onChange={handleChange("")}
                    />
                )
            default:
                return (
                    <Input
                        title={"First Pay Date"}
                        type={"date"}
                        className={"form-control"}
                        name={"startDate"}
                        value={incomeRequest.startDate}
                        onChange={handleChange("")}
                    />
                )
        }
    }

    const handleSubmit = (event) => {
        event.preventDefault();

        if (incomeRequest.frequency === "semi-monthly" && incomeRequest.days) {
            incomeRequest.daysOfMonth = incomeRequest.days.split(",").map(function (i) { return i.trim(); });
        }

        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${user.jwt}`
            },
            credentials: "include",
            body: JSON.stringify(incomeRequest),
        }

        fetch(`${UserApi}/${user.userId}/incomes`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                } else {
                    setIncomeRequest(emptyIncomeReq)
                    Toast("Income Added", "success");
                    closeModalHandler();
                    refreshUserData(Date.now());
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }

    return (
        <Modal className="income__modal" name="income__create">
            <h5>Add Income</h5>

            <div className="login__wrapper">
                <form onSubmit={handleSubmit}>
                    <Input
                        title={"Name"}
                        type={"text"}
                        className={"form-control"}
                        name={"name"}
                        value={incomeRequest.username}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Amount"}
                        type={"number"}
                        className={"form-control"}
                        name={"amount"}
                        value={incomeRequest.amount}
                        onChange={handleChange("")}
                    />
                    <Select
                        title={"Taxable"}
                        className={"form-control"}
                        name={"taxable"}
                        value={incomeRequest.taxable}
                        onChange={handleChange("")}
                        options={[{ id: "true", value: "true" }, { id: "false", value: "false" }]}
                        placeHolder={"Select"}
                    />
                    {incomeRequest.taxable && incomeRequest.taxable === "true" &&
                        <>
                            <Input
                                title={"Tax Credits"}
                                type={"number"}
                                className={"form-control"}
                                name={"tax_credits"}
                                value={incomeRequest.taxCredits}
                                onChange={handleChange("")}
                            />
                            <Input
                                title={"Withheld Tax"}
                                type={"number"}
                                className={"form-control"}
                                name={"taxWithheld"}
                                value={incomeRequest.taxWithheld}
                                onChange={handleChange("")}
                            />
                        </>
                    }

                    <Select
                        title={"Category"}
                        className={"form-control"}
                        name={"category"}
                        value={incomeRequest.category}
                        onChange={handleChange("")}
                        options={[{ id: "paycheck", value: "paycheck" }, { id: "benefit", value: "benefit" }]}
                        placeHolder={"Select"}
                    />
                    <Select
                        title={"Frequency"}
                        className={"form-control"}
                        name={"frequency"}
                        value={incomeRequest.frequency}
                        onChange={handleChange("")}
                        options={incFrequencies.map(f => ({ id: f.id, value: f.value }))}
                        placeHolder={"Select"}
                    />
                    {
                        getPayDateFormFields()
                    }

                    <Input
                        title={"Effective"}
                        type={"date"}
                        className={"form-control"}
                        name={"effectiveDate"}
                        value={incomeRequest.effectiveDate}
                        onChange={handleChange("")}
                    />

                    <Input
                        title={"Expires"}
                        type={"date"}
                        className={"form-control"}
                        name={"expirationDate"}
                        value={incomeRequest.expirationDate}
                        onChange={handleChange("")}
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

export default UpdateIncome