import { useState } from "react";

const Select = ( props ) => {
    const [selectedValue, setSelectedValue] = useState(props.value);

    const handleChange = (event) => {
        props.onChange(event)
        setSelectedValue(event.value)
    }

    return (


        <div className="form__select">
            <label htmlFor={props.name} className="form__select-label">
                {props.title}
            </label>
            <select
                className="form__select-input"
                name={props.name}
                id={props.name}
                value={props.value}
                onChange={handleChange}
            >
                <option value="">{props.placeHolder}</option>
                {props.options.map((o) => {
                    return (
                        <option
                            key={o.id}
                            value={o.id}
                        >
                            {o.value}
                        </option>
                    )
                })}
            </select>
            <div className={props.errorDiv}>{props.errorMsg}</div>
        </div>
    )
}
export default Select;