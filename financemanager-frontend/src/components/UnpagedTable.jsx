import { styled, Table, TableBody, TableCell, tableCellClasses, TableContainer, TableHead, TableRow } from "@mui/material";
import './css/paged-table.css'
import { useEffect, useState } from "react";

const UnpagedTable = ({ headings, rows }) => {
    const [tableRows, setTableRows] = useState([]);
    const minRows = 10;

    const StyledTableCell = styled(TableCell)(() => ({
        [`&.${tableCellClasses.head}`]: {
            backgroundColor: "var(--color-primary)",
            color: "white",
            paddingTop: "20px",
            paddingBottom: "20px"
        },
        [`&.${tableCellClasses.head}:first-child`]: {
            borderTopLeftRadius: "var(--border-radius-2)",
            paddingLeft: "20px"
        },
        [`&.${tableCellClasses.head}:last-child`]: {
            borderTopRightRadius: "var(--border-radius-2)",
            paddingRight: "20px"
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

        console.log(`parsing ${rows.length} rows of data`)

        if (rows.length < minRows) {
            for (let i = 0; i < Math.max(rows.length, minRows); i++) {
                if (i < rows.length) {
                    newRows.push(rows[i]);
                } else {
                    newRows.push(buildEmptyRow(`empty_${i}`));
                }
            }
        } else {
            newRows = rows;
        }

        console.log(`table row length is ${newRows.length}`)
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

    return (
        <div className="table__container">
            <TableContainer sx={{overflow: 'scroll', maxHeight: 600, '&::-webkit-scrollbar': {display: 'none'}}}>
                <Table stickyHeader sx={{ minWidth: "80%", borderColor: "blue", padding: "0" }} aria-label="Income Transactions List">
                    <TableHead>
                        <TableRow>
                            {
                                headings.map(h =>
                                    <StyledTableCell align={h.align ? h.align : "center"} padding="none"><span className="link__span">{h.label}</span></StyledTableCell>
                                )
                            }
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{overflow: 'scroll'}}>
                        {tableRows && tableRows.map((row) => (
                            <StyledTableRow
                                key={row.id}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                padding="none"
                            >
                                {row.data && row.data.map(d =>
                                    <StyledTableCell key={row.id + "_" + d.value} align={d.align ? d.align : "center"} padding="none">{d.value}</StyledTableCell>
                                )}
                            </StyledTableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <div className="search-criteria__container">
            </div>
        </div>
    )
}

export default UnpagedTable