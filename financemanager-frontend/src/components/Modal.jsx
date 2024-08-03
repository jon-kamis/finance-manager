import React from 'react'
import ReactDOM from 'react-dom'
import Card from './Card'
import './css/modal.css'
import { useModalContext } from '../context/modal-context'

const Modal = ({className, name, children}) => {
    const {showModal, closeModalHandler} = useModalContext();

    return (
        <>
            {
                showModal === name && ReactDOM.createPortal(<>
                <section id="backdrop" onClick = {closeModalHandler}></section>
                <Card className={className}>{children}</Card>
                </>, document.querySelector('#overlays'))
            }
        </>
    )
}

export default Modal