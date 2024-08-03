import React from 'react'
import Icon from '../../../../assets/AppIcon.png'
import './header.css'

const WelcomeHeader = () => {
  return (
    <header id="header">
      <div className="container header__container">
        <h1>Welcome to Finance Manager</h1>
        <p>An application built to help you manage your personal finances</p>
        <div className="header__icon">
          <div className="header__icon-background-wrapper">
            <div className="header__icon-background">
              <img src={Icon} alt="App Icon"></img>
            </div>
          </div>
        </div>
      </div>
    </header>
  )
}

export default WelcomeHeader