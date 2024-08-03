import React, { useState } from 'react'
import Modal from '../../components/Modal'
import Input from '../../components/form/Input'
import Select from '../../components/form/Select'
import { jwtDecode } from "jwt-decode";
import { useUserContext } from '../../context/user-context';
import './register.css'
import Toast from '../../components/alerting/Toast';
import { useModalContext } from '../../context/modal-context';
import { RegisterUrl } from '../../app-properties';
import stateData from './stateData'

const startingRegisterRequest = {
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    localTaxRate: 0.0,
    state: "",
    email: ""
}

const Register = () => {
    const { closeModalHandler } = useModalContext();
    const [registerRequest, setRegisterRequest] = useState(startingRegisterRequest);

    


    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setRegisterRequest({
            ...registerRequest,
            [name]: value,
        })
    }

    const handleSubmit = (event) => {
        event.preventDefault();

        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
            body: JSON.stringify(registerRequest),
        }

        fetch(`${RegisterUrl}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                    setRegisterRequest(startingRegisterRequest);
                } else {

                    setRegisterRequest(startingRegisterRequest);
                    Toast("Registration successful!", "success");
                    closeModalHandler();
                }
            })
            .catch(error => {
                Toast(error.message, "error");
                setRegisterRequest(startingRegisterRequest);
            })
    }

    return (
        <Modal className="register__modal" name="register">

            <h5>Register</h5>
            <small>Welcome to Finance Manager!</small>
            <small>Please enter your personal details below:</small>

            <div className="register__wrapper">
                <form onSubmit={handleSubmit}>
                    <Input
                        title={"First Name"}
                        type={"text"}
                        className={"form-control"}
                        name={"firstName"}
                        value={registerRequest.firstName}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Last Name"}
                        type={"text"}
                        className={"form-control"}
                        name={"lastName"}
                        value={registerRequest.lastName}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Email"}
                        type={"email"}
                        className={"form-control"}
                        name={"email"}
                        value={registerRequest.email}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Username"}
                        type={"text"}
                        className={"form-control"}
                        name={"username"}
                        value={registerRequest.username}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Password"}
                        type={"password"}
                        className={"form-control"}
                        name={"password"}
                        value={registerRequest.password}
                        onChange={handleChange("")}
                    />
                    <Select
                        title={"State"}
                        className={"form-control"}
                        name={"state"}
                        value={registerRequest.state}
                        onChange={handleChange("")}
                        options={stateData.map(s => ({ id: s.id, value: s.value }))}
                        placeHolder={"Select"}
                    />
                    <Input
                        title={"Local Tax Rate (optional)"}
                        type={"number"}
                        className={"form-control"}
                        name={"localTaxRate"}
                        value={registerRequest.localTaxRate}
                        onChange={handleChange("")}
                    />
                    <Input
                        type="submit"
                        className="btn primary"
                        value="Register"
                        onClick={handleSubmit}
                    />
                </form>
            </div>
        </Modal>
    )
}

export default Register