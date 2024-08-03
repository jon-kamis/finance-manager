import React from 'react'
import Modal from '../../components/Modal'
import { primaryColors, backgroundColors } from './data'
import PrimaryColor from './PrimaryColor'
import BackgroundColor from './BackgroundColor'
import './theme.css'

const Theme = () => {
    return (
        <Modal className="theme__modal" name="theme">
            <h3>Customize Your Theme</h3>
            <small>Change the primary and background color to your preference</small>
            <div className="theme__primary-wrapper">
                <h5>Primary Color</h5>
                <div className="theme__primary-colors">
                    {
                        primaryColors.map(p => <PrimaryColor key={p.className} className={p.className} />)
                    }
                </div>
            </div>
            <div className="theme__background-wrapper">
                <h5>Background Color</h5>
                <div className="theme__background-colors">
                    {
                        backgroundColors.map(b => <BackgroundColor key={b.className} className={b.className}/>)
                    }
                </div>
            </div>
        </Modal>
    )
}

export default Theme