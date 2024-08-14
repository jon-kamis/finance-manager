import React from 'react'
import ReactDOM from 'react-dom'
import Card from './Card'
import './css/modal.css'
import { useModalContext } from '../context/modal-context'
import { useThemeContext } from '../context/theme-context'

const Modal = ({className, name, children}) => {
    const {showModal, closeModalHandler} = useModalContext();
    const {themeState} = useThemeContext();

    return (
        <>
            {
                showModal === name && ReactDOM.createPortal(<>
                <section id="backdrop" onClick = {closeModalHandler}></section>
                <Card className={`modal__card ${themeState.primary} ${themeState.background} ${className}`}>{children}</Card>
                </>, document.querySelector('#overlays'))
            }
        </>
    )
}

export default Modal