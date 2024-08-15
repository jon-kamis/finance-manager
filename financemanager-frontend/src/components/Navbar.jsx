import Logo from '../assets/AppIcon.png'
import { IoIosColorPalette } from "react-icons/io";
import { MdPerson, MdOutlineLogin, MdOutlineLogout } from "react-icons/md";
import { useModalContext } from '../context/modal-context'
import { useUserContext } from '../context/user-context.js';
import './css/navbar.css'
import NavMenu from './NavMenu.jsx';

const Navbar = () => {
    const { jwt, user } = useUserContext();
    const { showModalHandler } = useModalContext();

    return (
        <nav>
            <div className="container nav__container">
                <a href="index.html" className='nav__logo'>
                    <img src={Logo} alt="Logo" />
                </a>
                <NavMenu />
                <div className="nav__buttons">
                    <button id="auth__icon" onClick={() => showModalHandler(jwt !== "" ? "logout" : "login")}>{jwt ? <MdOutlineLogout /> : <MdOutlineLogin />}</button>
                    <button id='theme__icon' onClick={() => showModalHandler("theme")}><IoIosColorPalette /></button>
                </div>
                <div className="nav__profile">
                    {
                        user && user.displayName !== "" &&
                        <>
                            <MdPerson id="profile__icon"/>
                            <span>{user.displayName}</span>
                        </>
                    }
                </div>
            </div>
        </nav>
    )
}

export default Navbar