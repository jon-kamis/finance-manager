import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context';
import Toast from '../../../../components/alerting/Toast';
import './tools.css'
import Card from '../../../../components/Card';
import { UserApi, numberFormatOptions } from '../../../../app-properties';
import BankImg from '../../../../assets/bank.png';
import { LuScrollText } from "react-icons/lu";

const LoanTools = () => {
  const { user, jwt } = useUserContext();
  const [userLoanSummary, setUserLoanSummary] = useState({});

  return (
    <section id="loan__tools">
      <h2>Tools</h2>
      <div className="container">
        <div className="tools__container">
          <div className="tools__left">
            <div className="tools__image">
              <img src={BankImg} alt="Graph Image" />
            </div>
          </div>
          <div className="tools__right">
            <div className="tools__cards">
              <Card key="loan__summary-debt" className="tools__card">
                <span className="tools__card-icon"><LuScrollText/></span>
                <h3>Loan Payment Simulator</h3>
              </Card>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default LoanTools