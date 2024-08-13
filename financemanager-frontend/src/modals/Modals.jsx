import React from 'react'
import Login from './login/Login'
import Logout from './logout/Logout'
import Theme from './theme/Theme'
import Register from './register/Register'
import { useThemeContext } from '../context/theme-context'

const Modals = () => {
    const {themeState} = useThemeContext();

    return (
        <div className={`${themeState.primary} ${themeState.background}`}>
            <Login />
            <Logout />
            <Register />
            <Theme />
        </div>
    )
}

export default Modals