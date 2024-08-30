import React, { useState } from 'react'
import './sections/edit/edit-compare.css'
import EditLoan from './sections/edit/EditLoan';
import Card from '../../components/Card';
import Navbar from '../../components/Navbar';
import SimulateLoanChanges from './sections/edit/SimChanges';
import EditPayments from './sections/edit/EditPayments';

const EditCompareLoan = () => {
    const [tool, setTool] = useState("edit");

    const getToolPage = () => {
        if (tool === "edit") {
            return (<EditLoan />);
        } else if (tool === "edit-payment") {
            return (<EditPayments />);
        } else {
            return (<SimulateLoanChanges />);
        }
    }

    function updateTool(newTool) {
        setTool(newTool);
    }

    return (
        <div className="wrapper">
            <Navbar />

            <header id="editCompare__header">
                <div className="container header__container">
                    <h1>Edit and Compare Loans</h1>

                    <div className="editLoan__btns">
                        <button id="loans-edit__btn" className="btn primary" onClick={() => updateTool("edit")}>Edit Details</button>
                        <button id="loans-edit__btn" className="btn primary" onClick={() => updateTool("edit-payment")}>Edit Payment</button>
                        <button id="loans-edit__btn" className="btn primary" onClick={() => updateTool("compare")}>Simulate Changes</button>
                    </div>
                </div>
            </header>

            {
                getToolPage()
            }

        </div>
    )
}

export default EditCompareLoan