import { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context';
import tableHeadings from './thead_data';
import Toast from '../../../../components/alerting/Toast';
import { UserApi } from '../../../../app-properties';
import PagedTable from '../../../../components/PagedTable';
import './incomes.css'

const defaultIncomeResponse = {
  items: [],
  page: 0,
  pageSize: 0,
  count: 0
}

const IncomeList = () => {

  const [incomes, setIncomes] = useState(defaultIncomeResponse);
  const [searchParameters, setSearchParameters] = useState({filter: "", page: 1, pageSize: 10})
  const [tableData, setTableData] = useState([]);
  const { user } = useUserContext();
  const numberFormatOptions = { maximumFractionDigits: 2, minimumFractionDigits: 2 }

  useEffect(() => {
    var data = [];
    incomes.items.forEach(i => {
      data.push({id: i.id, data : [i.name, i.category, i.frequency, "$"+Intl.NumberFormat("en-US", numberFormatOptions).format(i.amount)]});
    });

    setTableData(data);
  }, [incomes])

  useEffect(() => {
    const requestOptions = {
      method: "GET",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.jwt}`
      },
      credentials: "include",
    }

    fetch(`${UserApi}/${user.userId}/incomes?name=${searchParameters.filter}&page=${searchParameters.page}&pageSize=${searchParameters.pageSize}`, requestOptions)
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
  }, [user, searchParameters])

  return (
    <section id="income__list">
      <div className="container income__container">
        <h2>Incomes</h2>
        <PagedTable key = "income__list"className={"table__container-dark"} headings = {tableHeadings} rows={tableData} searchParameters={searchParameters} count={incomes && incomes.count ? incomes.count : 0} setSearchParameters={setSearchParameters}/>
      </div>
    </section>
  )
}

export default IncomeList