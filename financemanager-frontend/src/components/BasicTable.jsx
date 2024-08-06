import './css/basic-table.css'

const BasicTable = ({ className, headings, rows }) => {

    const minRows = 10;

    const addEmptyRows = (headings, rows) => {
        var newRows = rows;
        
        if (!rows) {
            return [];
        }

        if(rows.length < minRows) {
            while (rows.length < minRows) {
                rows.push({id: rows.length, data: buildEmptyRow(headings)})
            }
        }

        return newRows;
    }

    const buildEmptyRow = (headings) => {
        var emptyRow = [];

        if (!headings) {
            return emptyRow;
        }

        for (let i = 0; i < headings.length; i++) {
            emptyRow.push("")
        }

        return emptyRow;
    }

    return (
        <div className={`table__container ${className}`}>
            <table>
                <thead>
                    <tr>
                        {headings && headings.map(h =>
                            <th key={h.id}>{h.value}</th>
                        )}
                    </tr>
                </thead>
                <tbody>
                    { rows && addEmptyRows(headings, rows).map(r =>
                        <tr>
                            {r.data.map(d => 
                                <td>{d}</td>
                            )}
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}

export default BasicTable;