import { useEffect, useState } from 'react'
import { useUserContext } from '../../../../context/user-context';
import tableHeadings from './thead_data';
import Toast from '../../../../components/alerting/Toast';
import { UserApi, numberFormatOptions } from '../../../../app-properties';
import PagedTable from '../../../../components/PagedTable';
import './incomes.css'
import { format, parseISO } from "date-fns";
import { formatFmText } from '../../../../functions/textFunctions';
import { FaTrashAlt } from "react-icons/fa";
import { FiEdit3 } from "react-icons/fi";
import { GoPlus } from 'react-icons/go';
import { useModalContext } from '../../../../context/modal-context';

const defaultIncomeResponse = {
  items: [],
  page: 0,
  pageSize: 0,
  count: 0
}

const IncomeList = () => {

  const [incomes, setIncomes] = useState(defaultIncomeResponse);
  const [searchParameters, setSearchParameters] = useState({ filter: "", page: 1, pageSize: 10, sortBy: "", sortType: "asc" })
  const [tableData, setTableData] = useState([]);
  const { user, jwt, refreshUserData } = useUserContext();
  const { showModalHandler } = useModalContext();

  function deleteIncome(id) {
    const requestOptions = {
      method: "DELETE",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
      },
      credentials: "include",
    }

    fetch(`${UserApi}/${user.userId}/incomes/${id}`, requestOptions)
      .then((response) => {
        if (!response.ok) throw new Error(response.statusText);
        else return response.json();
      })
      .then((data) => {
        if (data.error) {
          Toast(data.message, "error");
        } else {
          Toast("Deleted Successfully", "success");
          refreshUserData(Date.now());
        }
      })
      .catch(error => {
        Toast(error.message, "error");
      })
  }

  useEffect(() => {
    var data = [];
    incomes.items.forEach(i => {
      data.push({
        id: i.id,
        data: [
          { align: "left", value: i.name },
          { value: formatFmText(i.category) },
          { value: format(parseISO(i.effectiveDate), 'MMM do yyyy') },
          { value: i.expirationDate ? format(parseISO(i.expirationDate), 'MMM do yyyy') : "" },
          { value: formatFmText(i.frequency) },
          { align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(i.amount) },
          {
            value: <div className="edit__cards">
              <span className="edit__card-icon"><FiEdit3 /></span>
              <span className="edit__card-icon edit__card-delete" onClick={() => deleteIncome(i.id)}><FaTrashAlt /></span>
            </div>
          }
        ]
      });
    });

    setTableData(data);
  }, [incomes])

  useEffect(() => {
    const requestOptions = {
      method: "GET",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
      },
      credentials: "include",
    }

    fetch(`${UserApi}/${user.userId}/incomes?name=${searchParameters.filter}&page=${searchParameters.page}&pageSize=${searchParameters.pageSize}&sortBy=${searchParameters.sortBy}&sortType=${searchParameters.sortType}`, requestOptions)
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

  const genHeadings = () => {
    let headings = [];
    headings.push(...tableHeadings);

    headings.push({
      id: "add_income",
      numeric: false,
      ignoreSort: true,
      disablePadding: true,
      label: <span className="add__icon" onClick={() => showModalHandler("income__create")}><GoPlus /></span>,
      align: "center"
    });

    return headings;

  }

  return (
    <section id="income__list">
      <div className="container income__container">
        <h2>Incomes</h2>
        <PagedTable
          key="income__list"
          className={"table__container-dark"}
          headings={genHeadings(tableHeadings)}
          rows={tableData}
          searchParameters={searchParameters}
          count={incomes && incomes.count ? incomes.count : 0}
          setSearchParameters={setSearchParameters} />
      </div>
    </section>
  )
}

export default IncomeList