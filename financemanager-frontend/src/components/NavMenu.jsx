import React from 'react'
import { useUserContext } from '../context/user-context';
import data from './data/navdata.js'

const NavMenu = () => {
  const { user } = useUserContext();

  return (
    <ul className="nav__menu">
      {
        data.map(i => 
          (!i.hasOwnProperty('requires') || (user.jwt && user.roles && user.roles.includes(i.requires))) &&
          <li key={i.id}><span>{i.title}</span></li>
        )
      }
    </ul>
  )
}

export default NavMenu