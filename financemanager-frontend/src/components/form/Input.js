import { forwardRef } from "react";

const Input = forwardRef((props, ref) => {
    return (
        <div className="form__input">
                <label htmlFor={props.name} className="form__input-label">
                    {props.title}
                </label>
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