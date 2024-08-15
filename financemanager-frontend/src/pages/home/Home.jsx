import Navbar from '../../components/Navbar'
import HomeHeader from './sections/header/HomeHeader'
import NetWorth from './sections/net-worth/NetWorth'
import MonthlyTransactions from './sections/transactions/MonthlyTransactions'

const Home = () => {

  return (
    <div className="home">
      <Navbar />
      <HomeHeader />
      <MonthlyTransactions />
      <NetWorth/>
    </div>
  )
}

export default Home