import React, { useEffect, useState } from 'react'
import './home-header.css'
import { useUserContext } from '../../../../context/user-context';
import { UserApi, numberFormatOptions } from '../../../../app-properties';
import Toast from '../../../../components/alerting/Toast';
import Card from '../../../../components/Card';
import { PieChart } from '@mui/x-charts';
import ExpandingCard from '../../../../components/ExpandingCard';

const defaultUserMonthSummary = {
  month: "",
  incomeTotals: {
    grossIncome: 0,
    totalPaycheck: 0,
    totalBenefit: 0,
    netIncome: 0,

  },
  expenseTotals: {
    totalExpense: 0,
    totalTax: 0,
    totalBills: 0,
    totalLoanPayments: 0,
    totalBenefit: 0,
    totalMisc: 0
  },
  transactions: []
};

const HomeHeader = () => {
  const { user, jwt } = useUserContext();
  const [userMonthSummary, setUserMonthSummary] = useState(defaultUserMonthSummary);
  const [pieData, setPieData] = useState([]);

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

  useEffect(() => {
    let data = [];

    data.push({ id: 0, color: 'blue', value: userMonthSummary.incomeTotals.netTotal, label: `Extra Funds` });

    data.push({ id: 1, color: 'red', value: userMonthSummary.expenseTotals.totalTax, label: `Tax` });

    data.push({ id: 1, color: 'purple', value: userMonthSummary.expenseTotals.totalBills, label: `Bills` });

    data.push({ id: 1, color: 'brown', value: userMonthSummary.expenseTotals.totalBenefit, label: `Benefits` })

    data.push({ id: 1, color: 'orange', value: userMonthSummary.expenseTotals.totalMisc, label: `Misc` });

    data.push({ id: 1, color: 'yellow', value: userMonthSummary.expenseTotals.totalLoanPayments, label: `Loans` });

    setPieData(data);
  }, [userMonthSummary])

  const getGrossIncomeContent = () => {
    return (
      <>
        <h5>Gross Income</h5>
        <h2>
          {
            `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.incomeTotals.grossTotal)}`
          }
        </h2>
      </>
    )
  }

  const getGrossIncomeHiddenContent = () => {
    return (
      <>
        {
          userMonthSummary.incomeTotals.totalBenefit > 0 &&
          <>
            <h5 className="expandedCard__heading">Benefits</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.incomeTotals.totalBenefit)}`
              }
            </h2>
          </>
        }
        {
          userMonthSummary.incomeTotals.totalPaycheck > 0 &&
          <>
            <h5 className="expandedCard__heading">Paychecks</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.incomeTotals.totalPaycheck)}`
              }
            </h2>
          </>
        }


      </>
    )
  }

  const getExpenseContent = () => {
    return (
      <>
        <h5>Expenses</h5>
        <h2>
          {
            `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalExpense)}`
          }
        </h2>
      </>
    );
  }

  const getExpenseHiddenContent = () => {
    return (
      <>
        {
          userMonthSummary.expenseTotals.totalBenefit > 0 &&
          <>
            <h5 className="expandedCard__heading">Benefits</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalBenefit)}`
              }
            </h2>
          </>
        }
        {
          userMonthSummary.expenseTotals.totalBills > 0 &&
          <>
            <h5 className="expandedCard__heading">Bills</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalBills)}`
              }
            </h2>
          </>
        }
        {
          userMonthSummary.expenseTotals.totalLoanPayments > 0 &&
          <>
            <h5 className="expandedCard__heading">Loan Payments</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalLoanPayments)}`
              }
            </h2>
          </>
        }
        {
          userMonthSummary.expenseTotals.totalTax > 0 &&
          <>
            <h5 className="expandedCard__heading">Tax</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalTax)}`
              }
            </h2>
          </>
        }
        {
          userMonthSummary.expenseTotals.totalMisc > 0 &&
          <>
            <h5 className="expandedCard__heading">Misc Expenses</h5>
            <h2>
              {
                `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.expenseTotals.totalMisc)}`
              }
            </h2>
          </>
        }
      </>
    )
  }

  return (
    <header id="home-header">
      <div className="container header__container">
        <h1>Welcome {user.firstName}</h1>
        {userMonthSummary.month && <p>Your summary for {userMonthSummary.month}</p>}

        <div className="home__data">

          <div className="home__charts">
            {userMonthSummary && (userMonthSummary.incomeTotals.grossTotal !== 0) &&
              <Card key="5" className="home__header-chart">
                <h3>Income breakdown</h3>
                <PieChart
                  series={[
                    {
                      outerRadius: 150,
                      data: pieData,
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
          <div className="home__cards">
            <ExpandingCard key="home-card-1"
              content={getGrossIncomeContent()}
              hiddenContent={getGrossIncomeHiddenContent()}
              className="home__header-card"
            />
            <ExpandingCard key="home-card-2"
              content={getExpenseContent()}
              hiddenContent={getExpenseHiddenContent()}
              className="home__header-card" />

            <Card key="home-card-3" className="home__header-card">
              <h5>Net Income</h5>
              <h2>{`$${Intl.NumberFormat("en-US", numberFormatOptions).format(userMonthSummary.incomeTotals.netTotal)}`}</h2>
            </Card>
          </div>
        </div>
      </div>
    </header>
  )
}

export default HomeHeader