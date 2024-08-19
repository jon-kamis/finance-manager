import { useThemeContext } from './context/theme-context';
import { useRef } from 'react';
import Welcome from './pages/welcome/Welcome';
import Modals from './modals/Modals';
import Footer from './components/Footer';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'
import { usePageContext } from './context/page-context';
import Income from './pages/incomes/Income';
import Home from './pages/home/Home';
import Loans from './pages/loans/Loans';
import EditCompareLoan from './pages/loans/EditCompareLoan';

const App = () => {
    const mainRef = useRef();
    const { themeState } = useThemeContext();
    const { activePage } = usePageContext();
    
    const getActivePage = () => {
        if (activePage === "welcome") {
            return (<Welcome />);
        } else if (activePage === "incomes") {
            return (<Income />);
        } else if (activePage === "home") {
            return (<Home />);
        } else if (activePage === "loans") {
            return (<Loans />);
        } else if (activePage === "loans-edit") {
            return (<EditCompareLoan />);
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