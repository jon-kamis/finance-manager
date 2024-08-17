import React from 'react'
import Navbar from '../../components/Navbar'
import LoanHeader from './sections/header/LoanHeader'
import LoanTools from './sections/tools/LoanTools'
import LoanList from './sections/loansList/LoanList'
import CreateLoan from '../../modals/loans/CreateLoan'

const Loans = () => {
    return (
        <div className="loans">
          <Navbar />
          <LoanHeader />
          <LoanList />
          <LoanTools />
          <CreateLoan />
        </div>
      )
}

export default Loans