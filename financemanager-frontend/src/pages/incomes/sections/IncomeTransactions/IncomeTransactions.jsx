import React, { useEffect, useState } from 'react'
import tableHeadings from './thead_data';
import Toast from '../../../../components/alerting/Toast';
import { useUserContext } from '../../../../context/user-context';
import { UserApi } from '../../../../app-properties';
import './transactions.css'
import PagedTable from '../../../../components/PagedTable';
import { formatFmText } from '../../../../functions/textFunctions';

const defaultTransactionResponse = {
    items: [],
    page: 0,
    pageSize: 0,
    count: 0
} 

const IncomeTransactions = () => {
    const [transactions, setTransactions] = useState(defaultTransactionResponse);
    const [tableData, setTableData] = useState([]);
    const [searchParameters, setSearchParameters] = useState({ filter: "", page: 1, pageSize: 10, sortBy: "", sortType: "asc" })
    const { user, jwt } = useUserContext();
    const numberFormatOptions = { maximumFractionDigits: 2, minimumFractionDigits: 2 }

    useEffect(() => {
        let data = [];
        transactions.items.forEach(t => {
            data.push({ id: t.id, data: [{align: "left", value: t.name}, { value: formatFmText(t.type)}, { value: formatFmText(t.frequency)}, {align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(t.amount)}]})
        });

        setTableData(data);
    }, [transactions])

    useEffect(() => {
        const requestOptions = {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
        }

        fetch(`${UserApi}/${user.userId}/transactions?parentName=incomes&name=${searchParameters.filter}&page=${searchParameters.page}&pageSize=${searchParameters.pageSize}&sortBy=${searchParameters.sortBy}&sortType=${searchParameters.sortType}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");

                } else {
                    setTransactions(data);
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }, [user, searchParameters])

    return (
        <section id="income__list">
            <div className="container income__container-transactionsList">
                <h2>Income Transactions</h2>

                <PagedTable headings = {tableHeadings} rows = {tableData} searchParameters={searchParameters} count={transactions && transactions.count ? transactions.count : 0} setSearchParameters={setSearchParameters}/>
            </div>
        </section>
    )
}

export default IncomeTransactions