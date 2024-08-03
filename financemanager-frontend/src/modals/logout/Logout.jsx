import React, { useState } from 'react'
import Modal from '../../components/Modal'
import { useUserContext } from '../../context/user-context';
import './logout.css'
import { useModalContext } from '../../context/modal-context';

const Logout = () => {
    const {closeModalHandler} = useModalContext();
    const { logoutUser } = useUserContext();

    return (
        <Modal className="logout__modal" name="logout">
            <h3>Logout</h3>
            <small>Are you sure you want to log out?</small>
            <div className="logout__buttons">
                <button className="btn primary" onClick = {() => {logoutUser(); closeModalHandler();}}>Yes</button>
                <button className="btn" onClick = {() => closeModalHandler()}>No</button>
            </div>
        </Modal>
    )
}

export default Logout