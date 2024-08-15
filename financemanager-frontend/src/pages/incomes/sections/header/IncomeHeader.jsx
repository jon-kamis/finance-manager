import React, { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context';
import Toast from '../../../../components/alerting/Toast';
import './header.css'
import Card from '../../../../components/Card';
import { UserApi } from '../../../../app-properties';
import { PieChart, pieArcLabelClasses } from '@mui/x-charts/PieChart';

const IncomeHeader = () => {
  const { user, jwt } = useUserContext();
  const [userIncomeSummary, setUserIncomeSummary] = useState({});
  const numberFormatOptions = { maximumFractionDigits: 2, minimumFractionDigits: 2 }

  const getAnnualIncomePercentage = () => {
    const percent = (userIncomeSummary.annualSummary.totalIncome - userIncomeSummary.annualSummary.totalTax) / userIncomeSummary.annualSummary.totalIncome;
    return `${(percent * 100).toFixed(0)}%`;
  }

  const getAnnualTaxPercentage = () => {
    const percent = userIncomeSummary.annualSummary.totalTax / userIncomeSummary.annualSummary.totalIncome;
    return `${(percent * 100).toFixed(0)}%`;
  }

  const getMonthIncomePercentage = () => {
    const percent = (userIncomeSummary.monthSummary.totalIncome - userIncomeSummary.monthSummary.totalTax) / userIncomeSummary.monthSummary.totalIncome;
    return `${(percent * 100).toFixed(0)}%`;
  }

  const getMonthTaxPercentage = () => {
    const percent = userIncomeSummary.monthSummary.totalTax / userIncomeSummary.monthSummary.totalIncome;
    return `${(percent * 100).toFixed(0)}%`;
  }

  useEffect(() => {
    const requestOptions = {
      method: "GET",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
      },
      credentials: "include",
    }

    fetch(`${UserApi}/${user.userId}/income-summary`, requestOptions)
      .then((response) => {
        if (!response.ok) throw new Error(response.statusText);
        else return response.json();
      })
      .then((data) => {
        if (data.error) {
          Toast(data.message, "error");

        } else {
          setUserIncomeSummary(data);
        }
      })
      .catch(error => {
        Toast(error.message, "error");
      })
  }, [user])

  return (
    <header id="header">
      <div className="container header__container">
        <h1>Incomes</h1>
        <p>Where your money comes from</p>

        <div className="incomes__cards">
          <Card key="1" className="incomes__header-card">
            <h5>Annual Income</h5>
            <h2>
              {
                userIncomeSummary && userIncomeSummary.annualSummary ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userIncomeSummary.annualSummary.totalIncome)}` : "-"
              }
            </h2>
          </Card>
          <Card key="2" className="incomes__header-card">
            <h5>Annual Tax</h5>
            <h2>
              {
                userIncomeSummary && userIncomeSummary.annualSummary ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userIncomeSummary.annualSummary.totalTax)}` : "-"
              }
            </h2>
          </Card>
          <Card key="3" className="incomes__header-card">
            <h5>Income this Month</h5>
            <h2>
              {
                userIncomeSummary && userIncomeSummary.monthSummary ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userIncomeSummary.monthSummary.totalIncome)}` : "-"
              }
            </h2>
          </Card>
          <Card key="4" className="incomes__header-card">
            <h5>Tax this Month</h5>
            <h2>
              {
                userIncomeSummary && userIncomeSummary.monthSummary ? `$${Intl.NumberFormat("en-US", numberFormatOptions).format(userIncomeSummary.monthSummary.totalTax)}` : "-"
              }
            </h2>
          </Card>
        </div>
        <div className="incomes__charts">
          {userIncomeSummary && userIncomeSummary.annualSummary && (userIncomeSummary.annualSummary.totalIncome !== 0 || userIncomeSummary.annualSummary.totalTax !== 0) &&
            <Card key="5" className="incomes__header-chart">
              <h3>Annual Income</h3>
              <PieChart
                series={[
                  {
                    outerRadius: 150,
                    innerRadius: 75,
                    data: [
                      { id: 0, color: 'green', value: userIncomeSummary.annualSummary.totalIncome - userIncomeSummary.annualSummary.totalTax, label: `${getAnnualIncomePercentage()}` },
                      { id: 1, color: 'red', value: userIncomeSummary.annualSummary.totalTax, label: `${getAnnualTaxPercentage()}` }
                    ],
                    type: 'pie',
                    arcLabel: 'label'
                  }
                ]}
                sx={{
                  [`& .${pieArcLabelClasses.root}`]: {
                    fill: 'white',
                    fontSize: 20,
                  },
                }}
                width={400}
                height={400}
                margin={{ top: 100, bottom: 100, left: 100, right: 100 }}
                slotProps={{ legend: { direction: 'row', position: { vertical: 'top', horizontal: 'middle' }, hidden: true } }}
              />
            </Card>
          }

          {userIncomeSummary && userIncomeSummary.monthSummary && (userIncomeSummary.monthSummary.totalIncome !== 0 || userIncomeSummary.monthSummary.totalTax !== 0) &&
            <Card key="5" className="incomes__header-chart">
              <h3>Income this Month</h3>
              <PieChart
                series={[
                  {
                    outerRadius: 150,
                    innerRadius: 75,
                    data: [
                      { id: 0, color: 'green', value: userIncomeSummary.monthSummary.totalIncome - userIncomeSummary.monthSummary.totalTax, label: `${getMonthIncomePercentage()}` },
                      { id: 1, color: 'red', value: userIncomeSummary.monthSummary.totalTax, label: `${getMonthTaxPercentage()}` }
                    ],
                    type: 'pie',
                    arcLabel: 'label'
                  }
                ]}
                sx={{
                  [`& .${pieArcLabelClasses.root}`]: {
                    fill: 'white',
                    fontSize: 20,
                  },
                }}
                width={400}
                height={400}
                margin={{ top: 100, bottom: 100, left: 100, right: 100 }}
                slotProps={{ legend: { direction: 'row', position: { vertical: 'top', horizontal: 'middle' }, hidden: true } }}
              />
            </Card>
          }
        </div>
        <small>Calculated values are based on position start and end dates</small>
        <small>All values ares estimates and accuracy is not garunteed</small>
      </div>
    </header>
  )
}

export default IncomeHeader