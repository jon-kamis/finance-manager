import React, { useState } from 'react'
import Modal from '../../components/Modal'
import Input from '../../components/form/Input'
import { useUserContext } from '../../context/user-context';
import './login.css'
import Toast from '../../components/alerting/Toast';
import { useModalContext } from '../../context/modal-context';
import { AuthUrl } from '../../app-properties';
import { usePageContext } from '../../context/page-context';

const Login = () => {
    const { showModalHandler, closeModalHandler } = useModalContext();
    const { updateUserTokens } = useUserContext();
    const { setActivePageHandler } = usePageContext();

    const [loginRequest, setLoginRequest] = useState({
        username: "",
        password: "",
    })

    const handleChange = () => (event) => {
        let value = event.target.value;
        let name = event.target.name;
        setLoginRequest({
            ...loginRequest,
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
            body: JSON.stringify(loginRequest),
        }

        fetch(`${AuthUrl}`, requestOptions)
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
                else return response.json();
            })
            .then((data) => {
                if (data.error) {
                    Toast(data.message, "error");
                    setLoginRequest({
                        username: "",
                        password: ""
                    })
                } else {
                    setLoginRequest({
                        username: "",
                        password: ""
                    })

                    updateUserTokens(data.accessToken, data.refreshToken);

                    Toast("Login successful!", "success");
                    closeModalHandler();
                    setActivePageHandler("home")
                }
            })
            .catch(error => {
                Toast(error.message, "error");
                setLoginRequest({
                    username: "",
                    password: ""
                })
            })
    }

    return (
        <Modal className="login__modal" name="login">

            <h5>Login</h5>

            <div className="login__register">
                <small>Don't have an account? <span onClick={() => showModalHandler("register")}>Register</span></small>
            </div>

            <div className="login__wrapper">
                <form onSubmit={handleSubmit}>
                    <Input
                        title={"Username"}
                        type={"text"}
                        className={"form-control"}
                        name={"username"}
                        value={loginRequest.username}
                        onChange={handleChange("")}
                    />
                    <Input
                        title={"Password"}
                        type={"password"}
                        className={"form-control"}
                        name={"password"}
                        value={loginRequest.password}
                        onChange={handleChange("")}
                    />
                    <Input
                        type="submit"
                        className="btn primary"
                        value="Login"
                        onClick={handleSubmit}
                    />
                </form>
            </div>
        </Modal>
    )
}

export default Login