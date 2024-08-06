import { createContext, useContext, useState } from "react";

const PageContext = createContext();

export const PageProvider = ({children}) => {
    const [activePage, setActivePage] = useState("welcome");

    const setActivePageHandler = (page) => {
        console.log(page);
        setActivePage(page);
    }

    return <PageContext.Provider value={{activePage, setActivePageHandler}}>{children}</PageContext.Provider>
}

// Custom hook to consume the modal context anywhere in the app
export const usePageContext = () => {
    return useContext(PageContext);
}