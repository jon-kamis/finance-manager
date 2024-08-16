import { styled, Table, TableBody, TableCell, tableCellClasses, TableContainer, TableHead, TableRow } from "@mui/material";
import Input from "./form/Input";
import { MdKeyboardArrowLeft, MdKeyboardDoubleArrowLeft, MdKeyboardArrowRight, MdKeyboardDoubleArrowRight, MdStarPurple500 } from "react-icons/md";
import './css/paged-table.css'
import { useEffect, useState } from "react";

const PagedTable = ({ headings, rows, searchParameters, setSearchParameters, count }) => {
    const [tableRows, setTableRows] = useState([]);
    const minRows = 10;

    const StyledTableCell = styled(TableCell)(() => ({
        [`&.${tableCellClasses.head}`]: {
            backgroundColor: "var(--color-primary)",
            color: "white",
            padding: "20px",
        },
        [`&.${tableCellClasses.head}:first-child`]: {
            borderTopLeftRadius: "var(--border-radius-2)",
        },
        [`&.${tableCellClasses.head}:last-child`]: {
            borderTopRightRadius: "var(--border-radius-2)"
        },
        [`&.${tableCellClasses.body}`]: {
            color: "var(--color-black)",
        },
        [`&`]: {
            borderBottom: "none",
        },
    }));

    const StyledTableRow = styled(TableRow)(() => ({
        '&:nth-of-type(odd)': {
            backgroundColor: "var(--color-light)",
        },
        '&:nth-of-type(even)': {
            backgroundColor: "var(--color-white)",
        },
        // hide last border
        '&:last-child td, &:last-child th': {
            border: 0,
        },
        '&:td': {
            color: "var(--color-black)",
        },
        '&': {
            borderLeft: "1px solid var(--color-primary)",
            borderRight: "1px solid var(--color-primary)",
            height: "4rem",
        },
        '&:last-child': {
            borderBottom: "none"
        },
        '& td:first-child': {
            paddingLeft: "20px"
        },
        '& td:last-child': {
            paddingRight: "20px"
        }
    }));

    useEffect(() => {
        var newRows = [];

        if (!rows) {
            return [];
        }

        for (let i = 0; i < searchParameters.pageSize; i++) {
            if (i < rows.length) {
                newRows.push(rows[i])
            } else {
                newRows.push(buildEmptyRow(`empty_${i}`))
            }
        }

        setTableRows(newRows);
    }, [rows]);

    const buildEmptyRow = (id) => {
        var emptyRow = {id: id, data: []};

        if (!headings) {
            return emptyRow;
        }

        for (let i = 0; i < headings.length; i++) {
            emptyRow.data.push({value: <p></p>})
        }

        return emptyRow;
    }

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setSearchParameters({
            ...searchParameters,
            [name]: value,
            page: 1
        })
    }

    const getItemCountText = () => {
        if (count === 0) {
            return <h5>0 of 0</h5>
        } else {
            var startIndex = ((searchParameters.page - 1) * searchParameters.pageSize) + 1;
            var endIndex = Math.min((searchParameters.page * searchParameters.pageSize), count);
            return <h5>{startIndex} - {endIndex} of {count}</h5>
        }
    }

    const getPageMaximum = () => {
        var max = (count > 0 && searchParameters.pageSize > 0 ? Math.ceil(count / searchParameters.pageSize) : 1);
        return max;
    }

    const setPageMinimum = () => {
        setSearchParameters({ ...searchParameters, page: 1 })
    }

    const setPageMaximum = () => {
        setSearchParameters({ ...searchParameters, page: getPageMaximum() })
    }

    const incPage = () => {
        if (searchParameters.page + 1 <= getPageMaximum()) {
            setSearchParameters({ ...searchParameters, page: searchParameters.page + 1 })
        }
    }

    const decPage = () => {
        if (searchParameters.page - 1 >= 1) {
            setSearchParameters({ ...searchParameters, page: searchParameters.page - 1 })
        }
    }

    const updateSort = (name) => {
        if (searchParameters) {
            let sortBy = searchParameters.sortBy;
            let sortType = searchParameters.sortType;
            console.log(`Setting sortBy to ${name}`)
            console.log(searchParameters)
            if (searchParameters.sortBy === name) {
                sortType = searchParameters.sortType && searchParameters.sortType === "asc" ? "desc" : "asc";
            } else {
                sortBy = name;
            }

            setSearchParameters({
                ...searchParameters,
                sortBy: sortBy,
                sortType: sortType
            });
        }
    }

    return (
        <div className="table__container">
            <TableContainer>
                <Table sx={{ minWidth: "80%", borderColor: "blue", padding: "0" }} aria-label="Income Transactions List">
                    <TableHead>
                        <TableRow>
                            {
                                headings.map(h =>
                                    <StyledTableCell align={h.align ? h.align : "center"} padding = "none"><span className ="link__span" onClick = {() => updateSort(`${h.id}`)}>{h.label}</span></StyledTableCell>
                                )
                            }
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tableRows && tableRows.map((row) => (
                            <StyledTableRow
                                key={row.id}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                padding="none"
                            >
                                {row.data && row.data.map(d =>
                                    <StyledTableCell key={row.id + "_" + d.value} align={d.align ? d.align : "center"} padding = "none">{d.value}</StyledTableCell>
                                )}
                            </StyledTableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <div className="search-criteria__container">
                <div className="filter__container">
                    <Input
                        title={"Filter"}
                        type={"text"}
                        className={"form__filter"}
                        name={"filter"}
                        value={searchParameters.filter}
                        onChange={handleChange("")}
                    />
                </div>
                <div className="count__container">
                    {
                        getItemCountText()
                    }
                </div>
                <div className="paging__container">
                    <span onClick={() => setPageMinimum()}><MdKeyboardDoubleArrowLeft /></span>
                    <span onClick={() => decPage()}><MdKeyboardArrowLeft /></span>
                    <span onClick={() => incPage()}><MdKeyboardArrowRight /></span>
                    <span onClick={() => setPageMaximum()}><MdKeyboardDoubleArrowRight /></span>
                </div>
            </div>
        </div>
    )
}

export default PagedTable