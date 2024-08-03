import { createContext, useContext, useState } from "react";

const ModalContext = createContext();

export const ModalProvider = ({children}) => {
    const [showModal, setShowModal] = useState("");

    const showModalHandler = (modalName) => {
        setShowModal(modalName);
    }

    const closeModalHandler = () => {
        setShowModal("");
    }

    return <ModalContext.Provider value={{showModal, showModalHandler, closeModalHandler}}>{children}</ModalContext.Provider>
}

// Custom hook to consume the modal context anywhere in the app
export const useModalContext = () => {
    return useContext(ModalContext);
}