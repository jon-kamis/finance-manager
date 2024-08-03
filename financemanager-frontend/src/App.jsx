import { useThemeContext } from './context/theme-context';
import {useRef} from 'react';
import Welcome from './pages/welcome/Welcome';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'
import Modals from './modals/Modals';

const App = () => {
    const mainRef = useRef();
    const {themeState} = useThemeContext();
    
    return (
        <main className={`${themeState.primary} ${themeState.background}`}  ref={mainRef}>
            <Welcome/>
            <Modals/>
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