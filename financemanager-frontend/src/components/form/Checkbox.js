const CheckBox = (props) => {
    return (
        <div className="form__checkbox">
            <input
                id={props.name}
                className="form__checkbox-input"
                type="checkbox"
                value={props.value}
                name={props.name}
                onChange={props.onChange}
                checked={props.checked}
            />
            <label className="form__checkbox-label" htmlFor={props.name}>
                {props.title}
            </label>
        </div>
    )
}

export default CheckBox;