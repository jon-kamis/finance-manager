import { useThemeContext } from './context/theme-context';
import { useCallback, useEffect, useRef, useState } from 'react';
import Welcome from './pages/welcome/Welcome';
import Modals from './modals/Modals';
import Footer from './components/Footer';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'
import { usePageContext } from './context/page-context';
import Income from './pages/incomes/Income';
import { useUserContext } from './context/user-context';
import { RefreshUrl } from './app-properties';

const App = () => {
    const mainRef = useRef();
    const { themeState } = useThemeContext();
    const { activePage } = usePageContext();
    
    const getActivePage = () => {
        if (activePage === "welcome") {
            return (<Welcome />)
        } else if (activePage === "incomes") {
            return (<Income />)
        }
    }

    return (
        <main className={`${themeState.primary} ${themeState.background}`} ref={mainRef}>

            {
                getActivePage()
            }
            <Footer />
            <Modals />
            <div>
                <ToastContainer
                    position="bottom-center"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable={false}
                    pauseOnHover
                    theme={themeState.background === "bg-dark" ? "dark" : "light"} />
            </div>
        </main>
    )
}

export default App