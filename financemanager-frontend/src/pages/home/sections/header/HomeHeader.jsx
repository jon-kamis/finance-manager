import React, { useEffect, useState } from 'react'
import './home-header.css'
import { useUserContext } from '../../../../context/user-context';
import { UserApi, numberFormatOptions } from '../../../../app-properties';
import Toast from '../../../../components/alerting/Toast';
import Card from '../../../../components/Card';
import { pieArcLabelClasses, PieChart } from '@mui/x-charts';

const defaultUserMonthSummary = {
  totals: {
    month: "",
    totalIncome: 0,
    totalExpense: 0,
    netIncome: 0,
    totalTax: 0,
    totalBills: 0,
    totalLoanPayments: 0,
    totalMisc: 0
  },
  transactions: []
};

const HomeHeader = () => {
  const { user, jwt } = useUserContext();
  const [userMonthSummary, setUserMonthSummary] = useState(defaultUserMonthSummary);

  useEffect(() => {
    const requestOptions = {
      method: "GET",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
      },
      credentials: "include",
    }

    fetch(`${UserApi}/${user.userId}/summary`, requestOptions)
      .then((response) => {
        if (!response.ok) throw new Error(response.statusText);
        else return response.json();
      })
      .then((data) => {
        if (data.error) {
          Toast(data.message, "error");

        } else {
          setUserMonthSummary(data);
        }
      })
      .catch(error => {
        Toast(error.message, "error");
      })
  }, [user, jwt])

  return (
    <header id="home-header">
      <div className="container header__container">
        <h1>Welcome {user.firstName}</h1>
        {userMonthSummary.totals.month && <p>Your summary for {userMonthSummary.totals.month}</p>}

        <div className="home__data">
          <div className="home__cards">
            <Card key="home-card-1" className="home__header-card">
              <h5>Gross Income</h5>
              <h2>
                {
                  `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.totals.totalIncome)}`
                }
              </h2>
            </Card>
            <Card key="home-card-2" className="home__header-card">
              <h5>Expenses</h5>
              <h2>
                {
                  `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.totals.totalExpense)}`
                }
              </h2>
            </Card>
            <Card key="home-card-3" className="home__header-card">
              <h5>Net Income</h5>
              <h2>{`$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.totals.netIncome)}`}</h2>
            </Card>
          </div>
          <div className="home__charts">
            {userMonthSummary && (userMonthSummary.totals.totalIncome !== 0) &&
              <Card key="5" className="home__header-chart">
                <h3>Income breakdown</h3>
                <PieChart
                  series={[
                    {
                      outerRadius: 150,
                      data: [
                        userMonthSummary.totals.netIncome > 0 && { id: 0, color: 'blue', value: userMonthSummary.totals.netIncome, label: `Extra Funds` },
                        userMonthSummary.totals.totalTax > 0 && { id: 1, color: 'red', value: userMonthSummary.totals.totalTax, label: `Tax` },
                        userMonthSummary.totals.totalBills > 0 && { id: 1, color: 'purple', value: userMonthSummary.totals.totalBills, label: `Bills` },
                        userMonthSummary.totals.totalMisc > 0 && { id: 1, color: 'orange', value: userMonthSummary.totals.totalMisc, label: `Misc` },
                        userMonthSummary.totals.totalLoanPayments > 0 && { id: 1, color: 'yellow', value: userMonthSummary.totals.totalLoanPayments, label: `Loans` }

                      ],
                      type: 'pie',
                    }
                  ]}
                  width={500}
                  height={500}
                  slotProps={{
                    legend: {
                      direction: 'column',
                      position: { verticle: 'top', horizontal: 'right' },
                      labelStyle: {
                        fontSize: 14,
                        fill: 'var(--color-black)'
                      }
                    }
                  }}
                />
              </Card>
            }
          </div>
        </div>
      </div>
    </header>
  )
}

export default HomeHeader