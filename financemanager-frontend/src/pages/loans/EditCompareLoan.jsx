import React, { useState } from 'react'
import { useModalContext } from '../../context/modal-context';
import { useUserContext } from '../../context/user-context';
import './sections/edit/edit-compare.css'
import EditLoan from './sections/edit/EditLoan';
import CompareLoan from './sections/edit/CompareLoan';
import Card from '../../components/Card';
import Navbar from '../../components/Navbar';

const EditCompareLoan = () => {
    const [tool, setTool] = useState("edit");

    const getToolPage = () => {
        if (tool === "edit") {
            return (<EditLoan />);
        } else {
            return (<CompareLoan />);
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
                        <button id="loans-edit__btn" className="btn primary" onClick={() => updateTool("edit")}>Edit</button>
                        <button id="loans-edit__btn" className="btn primary" onClick={() => updateTool("compare")}>Compare</button>
                    </div>
                </div>
            </header>
            <section key="editCompareLoan">
                <div className="container editLoan__container">
                    <div className="editLoan__tool-page">
                        <Card className="editLoan__form-container">
                            {
                                getToolPage()
                            }
                        </Card>
                    </div>

                </div>
            </section>
        </div>
    )
}

export default EditCompareLoan