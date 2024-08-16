import React, { useEffect, useState } from 'react'
import './monthlyTransactions.css'
import tableHeadings from './thead_data';
import { format, parseISO } from "date-fns";
import { formatFmText } from '../../../../functions/textFunctions';
import Toast from '../../../../components/alerting/Toast';
import { UserApi, numberFormatOptions } from '../../../../app-properties';
import PagedTable from '../../../../components/PagedTable';
import { useUserContext } from '../../../../context/user-context';

const defaultResponse = {
    count: 0,
    page: 1,
    pageSize: 10,
    items: []
};

const MonthlyTransactions = () => {
    const { user, jwt } = useUserContext();
    const [incSearchParameters, setIncSearchParameters] = useState({ filter: "", page: 1, pageSize: 10, sortBy: "", sortType: "asc" })
    const [expSearchParameters, setExpSearchParameters] = useState({ filter: "", page: 1, pageSize: 10, sortBy: "", sortType: "asc" })
    const [incomeTableData, setIncomeTableData] = useState([]);
    const [expenseTableData, setExpenseTableData] = useState([]);
    const [incomes, setIncomes] = useState(defaultResponse);
    const [expenses, setExpenses] = useState(defaultResponse);
    
    function getStartDate() {
      const startDt = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
      return startDt.toISOString().split('T')[0]
    }

    function getEndDate() {
      const endDt = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0);
      return endDt.toISOString().split('T')[0]
    }

    useEffect(() => {
        var data = [];
        incomes.items.forEach(i => {
          data.push({
            id: i.id,
            data: [
              { align: "left", value: i.name },
              { value: formatFmText(i.category) },
              { value: i.date ? format(parseISO(i.date), 'MMM do yyyy') : "" },
              { align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(i.amount) },
            ]
          });
        });
    
        setIncomeTableData(data);
      }, [incomes])

      useEffect(() => {
        var data = [];
        expenses.items.forEach(i => {
          data.push({
            id: i.id,
            data: [
              { align: "left", value: i.name },
              { value: formatFmText(i.category) },
              { value: i.date ? format(parseISO(i.date), 'MMM do yyyy') : "" },
              { align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(i.amount) },
            ]
          });
        });
    
        setExpenseTableData(data);
      }, [expenses])
    
      useEffect(() => {
        const requestOptions = {
          method: "GET",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
          },
          credentials: "include",
        }
    
        fetch(`${UserApi}/${user.userId}/transaction-occurrences?startDate=${getStartDate()}&endDate=${getEndDate()}&type=income&name=${incSearchParameters.filter}&page=${incSearchParameters.page}&pageSize=${incSearchParameters.pageSize}&sortBy=${incSearchParameters.sortBy}&sortType=${incSearchParameters.sortType}`, requestOptions)
          .then((response) => {
            if (!response.ok) throw new Error(response.statusText);
            else return response.json();
          })
          .then((data) => {
            if (data.error) {
              Toast(data.message, "error");
    
            } else {
              setIncomes(data);
            }
          })
          .catch(error => {
            Toast(error.message, "error");
          })
      }, [user, incSearchParameters])

      useEffect(() => {
        const requestOptions = {
          method: "GET",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
          },
          credentials: "include",
        }
    
        fetch(`${UserApi}/${user.userId}/transaction-occurrences?startDate=${getStartDate()}&endDate=${getEndDate()}&type=expense&name=${expSearchParameters.filter}&page=${expSearchParameters.page}&pageSize=${expSearchParameters.pageSize}&sortBy=${expSearchParameters.sortBy}&sortType=${expSearchParameters.sortType}`, requestOptions)
          .then((response) => {
            if (!response.ok) throw new Error(response.statusText);
            else return response.json();
          })
          .then((data) => {
            if (data.error) {
              Toast(data.message, "error");
    
            } else {
              setExpenses(data);
            }
          })
          .catch(error => {
            Toast(error.message, "error");
          })
      }, [user, expSearchParameters])

    const getCurMonth = () => {
        let curMonth = new Date().getMonth();

        switch (curMonth) {
            case 0:
                return "January"
            case 1:
                return "February"
            case 2:
                return "March"
            case 3:
                return "April"
            case 4:
                return "May"
            case 5:
                return "June"
            case 6:
                return "July"
            case 7:
                return "August"
            case 8:
                return "September"
            case 9:
                return "October"
            case 10:
                return "November"
            case 11:
                return "December"
        }
    }

    return (
        <section id="monthly-transactions">
            <h2>{getCurMonth()} Transactions</h2>

            <div className="container monthlyTransactions__container">
                <div className="monthlyTransactions__incomes">
                    <h2>Incomes</h2>
                    <PagedTable 
                        key="mt-income__list" 
                        className={"table__container-dark"} 
                        headings={tableHeadings} 
                        rows={incomeTableData} 
                        searchParameters={incSearchParameters} 
                        count={incomes && incomes.count ? incomes.count : 0} 
                        setSearchParameters={setIncSearchParameters} />
                </div>
                <div className="monthlyTransactions__expenses">
                    <h2>Expenses</h2>
                    <PagedTable 
                        key="mt-expense__list" 
                        className={"table__container-dark"} 
                        headings={tableHeadings} 
                        rows={expenseTableData} 
                        searchParameters={expSearchParameters} 
                        count={expenses && expenses.count ? expenses.count : 0} 
                        setSearchParameters={setExpSearchParameters} />
                </div>
            </div>
        </section>
    )
}

export default MonthlyTransactions