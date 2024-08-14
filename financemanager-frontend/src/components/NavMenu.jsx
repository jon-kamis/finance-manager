import React from 'react'
import { useUserContext } from '../context/user-context';
import data from './data/navdata.js'
import { usePageContext } from '../context/page-context.js';

const NavMenu = () => {
  const { user } = useUserContext();
  const { setActivePageHandler } = usePageContext();

  return (
    <ul className="nav__menu">
      {
        data.map(i => 
          (!i.hasOwnProperty('requires') || (user.jwt && user.roles && user.roles.includes(i.requires))) &&
          <li key={i.id}><span onClick = {() => setActivePageHandler(i.navName)}>{i.title}</span></li>
        )
      }
    </ul>
  )
}

export default NavMenu