import ReactDom from 'react-dom/client'
import App from './App'
import { ModalProvider } from './context/modal-context';
import { UserProvider } from './context/user-context';
import './index.css'
import { ThemeProvider } from './context/theme-context';
import { PageProvider } from './context/page-context';

const root = ReactDom.createRoot(document.querySelector('#root'));
root.render(
    <PageProvider>
        <UserProvider>
            <ThemeProvider>
                <ModalProvider>
                    <App />
                </ModalProvider>
            </ThemeProvider>
        </UserProvider>
    </PageProvider>);