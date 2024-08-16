import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context';
import Toast from '../../../../components/alerting/Toast';
import './header.css'
import Card from '../../../../components/Card';
import { UserApi, numberFormatOptions } from '../../../../app-properties';

const LoanHeader = () => {
  const { user, jwt } = useUserContext();
  const [userLoanSummary, setUserLoanSummary] = useState({});

  return (
    <header id="header">
      <div className="container header__container">
        <h1>Loans</h1>
        <div className="loanHeader__container">
          <div className="loanHeader__cards">
            <Card key="loan__summary-cost" className="loan__card">
              <h5>Loans</h5>
              <h2>
                {
                  userLoanSummary && userLoanSummary.count ? `${userLoanSummary.count}` : "0"
                }
              </h2>
            </Card>
            <Card key="loan__summary-debt" className="loan__card">
              <h5>Current Debt</h5>
              <h2>
                {
                  userLoanSummary && userLoanSummary.currentDebt ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userLoanSummary.currentDebt)}` : "$0"
                }
              </h2>
            </Card>
            <Card key="loan__summary-cost" className="loan__card">
              <h5>Monthly Cost</h5>
              <h2>
                {
                  userLoanSummary && userLoanSummary.monthlyCost ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userLoanSummary.monthlyCost)}` : "$0"
                }
              </h2>
            </Card>
          </div>
        </div>
      </div>
    </header>
  )
}

export default LoanHeader