import BasicTable from "./BasicTable";
import Input from "./form/Input";
import { MdKeyboardArrowLeft, MdKeyboardDoubleArrowLeft, MdKeyboardArrowRight, MdKeyboardDoubleArrowRight } from "react-icons/md";
import './css/paged-table.css'

const PagedTable = ({ className, headings, rows, searchParameters, setSearchParameters, count }) => {

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setSearchParameters({
            ...searchParameters,
            [name]: value,
        })
    }

    const getItemCountText = () => {
        if (count == 0) {
            return <h5>0 of 0</h5>
        } else {
            var startIndex = ((searchParameters.page - 1) * searchParameters.pageSize) + 1;
            var endIndex = Math.min((searchParameters.page * searchParameters.pageSize), count);
            return <h5>{startIndex} - {endIndex} of {count}</h5>
        }
    }

    const getPageMaximum = () => {
        var max = (count > 0 && searchParameters.pageSize > 0 ? Math.ceil(count / searchParameters.pageSize) : 1);
        console.log("page maximum is " + max);
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

    return (
        <div className="paged-table__container">
            <BasicTable className={className} headings={headings} rows={rows} />
            <div className="search-criteria__container">
                <div className="filter__container">
                    <Input
                        title={"Filter"}
                        type={"text"}
                        className={"form-control"}
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
                    <span onClick={() => setPageMinimum()}><MdKeyboardDoubleArrowLeft/></span>
                    <span onClick={() => decPage()}><MdKeyboardArrowLeft /></span>
                    <span onClick={() => incPage()}><MdKeyboardArrowRight/></span>
                    <span onClick={() => setPageMaximum()}><MdKeyboardDoubleArrowRight/></span>
                </div>
            </div>
        </div>
    )
}

export default PagedTable