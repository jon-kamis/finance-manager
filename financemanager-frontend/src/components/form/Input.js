import { forwardRef } from "react";

const Input = forwardRef((props, ref) => {
    return (
        <div className="form__input">
            <div className="form__input-label">
                <label htmlFor={props.name}>
                    <h5>{props.title}</h5>
                </label>
            </div>
            <div className="form__input-field">
                <input
                    type={props.type}
                    className={props.className}
                    id={props.name}
                    ref={ref}
                    name={props.name}
                    placeholder={props.placeholder}
                    onChange={props.onChange}
                    onClick={props.onClick}
                    autoComplete={props.autoComplete}
                    value={props.value}
                />
            </div>
            <div className={props.errorDiv}>{props.errorMsg}</div>
        </div>
    )
})

export default Input;