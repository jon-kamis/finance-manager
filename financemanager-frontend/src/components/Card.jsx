import './css/card.css'
import React from 'react'

const Card = ({children, className, onClick}) => {
  return (
    <article className={`card ${className}`} onClick={onClick}>
        {children}
    </article>
  )
}

export default Card