import React from 'react'
import Navbar from '../../components/Navbar'
import IncomeHeader from './sections/header/IncomeHeader'
import IncomeList from './sections/IncomeList/IncomeList'
import IncomeTransactions from './sections/IncomeTransactions/IncomeTransactions'

const Income = () => {
  return (
    <div className="income">
      <Navbar />
      <IncomeHeader/>
      <IncomeList/>
      <IncomeTransactions/>
    </div>
  )
}

export default Income