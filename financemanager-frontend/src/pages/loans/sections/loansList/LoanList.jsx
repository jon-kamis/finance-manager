import React, { useEffect, useState } from 'react'
import './loan-list.css'
import { useModalContext } from '../../../../context/modal-context';
import { useUserContext } from '../../../../context/user-context';
import tableHeadings from './thead_data'
import PagedTable from '../../../../components/PagedTable';
import Toast from '../../../../components/alerting/Toast';
import { UserApi, numberFormatOptions, rateFormatOptions } from '../../../../app-properties';
import { GoPlus } from 'react-icons/go';
import { formatFmText, formatLoanTerm } from '../../../../functions/textFunctions';
import { format, parseISO } from "date-fns";
import { FaTrashAlt } from "react-icons/fa";
import { FiEdit3 } from "react-icons/fi";
import { usePageContext } from '../../../../context/page-context';

const defaultLoanResponse = {
    items: [],
    page: 0,
    pageSize: 0,
    count: 0
}

const LoanList = () => {
    const [loans, setLoans] = useState(defaultLoanResponse);
    const [searchParameters, setSearchParameters] = useState({ filter: "", page: 1, pageSize: 10, sortBy: "", sortType: "asc" })
    const [tableData, setTableData] = useState([]);
    const { user, setLoan, jwt, refreshUserData } = useUserContext();
    const { showModalHandler } = useModalContext();
    const { setActivePageHandler } = usePageContext();

    const genHeadings = () => {
        let headings = [];
        headings.push(...tableHeadings);

        headings.push({
            id: "add_loan",
            ignoreSort: true,
            numeric: false,
            disablePadding: true,
            label: <span className="add__icon" onClick={() => showModalHandler("loan__create")}><GoPlus /></span>,
            align: "center"
        });

        return headings;

    }

    const editLoan = (id) => {
        setLoan(loans.items.find((l) => l.id = id));
        setActivePageHandler("loans-edit");
    }

    useEffect(() => {
        var data = [];
        loans.items.forEach(l => {
            data.push({
                id: l.id,
                data: [
                    { align: "left", value: l.name },
                    { align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(l.principal)  },
                    { value: format(parseISO(l.firstPaymentDate), 'MMM do yyyy') },
                    { value: formatFmText(l.frequency) },
                    { align: "right", value: Intl.NumberFormat("en-US", numberFormatOptions).format(l.payment) },
                    { align: "right", value: Intl.NumberFormat("en-US", rateFormatOptions).format(l.rate * 100) },
                    { align: "right", value: formatLoanTerm(l.term) },
                    {
                        value: <div className="edit__cards">
                            <span className="edit__card-icon" onClick={() => editLoan(l.id)}><FiEdit3 /></span>
                            <span className="edit__card-icon edit__card-delete" onClick={() => deleteLoan(l.id)}><FaTrashAlt /></span>
                        </div>
                    }
                ]
            });
        });

        setTableData(data);
    }, [loans])

    useEffect(() => {
        const requestOptions = {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
        }

        fetch(`${UserApi}/${user.userId}/loans?name=${searchParameters.filter}&page=${searchParameters.page}&pageSize=${searchParameters.pageSize}&sortBy=${searchParameters.sortBy}&sortType=${searchParameters.sortType}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");

                } else {
                    setLoans(data);
                }
            })
            .catch(error => {
                Toast(error.message, "error");
            })
    }, [user, searchParameters])

    function deleteLoan(id) {
        const requestOptions = {
            method: "DELETE",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            credentials: "include",
        }

        fetch(`${UserApi}/${user.userId}/loans/${id}`, requestOptions)
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

    return (
        <section id="loan__list">
            <div className="container income__container">
                <h2>Your Loans</h2>
                <PagedTable
                    key="income__list"
                    className={"table__container-dark"}
                    headings={genHeadings(tableHeadings)}
                    rows={tableData}
                    searchParameters={searchParameters}
                    count={loans && loans.count ? loans.count : 0}
                    setSearchParameters={setSearchParameters} />
            </div>
        </section>
    )
}

export default LoanList