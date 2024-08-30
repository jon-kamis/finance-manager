import './css/footer.css'
import NavMenu from './NavMenu'

const Footer = () => {
    return (
        <footer id="footer">
            <div className="container footer__container">
                <NavMenu/>
                <div className="footer_disclaimer">
                    <small>Disclaimer</small>
                    <small>Finance Manager is a hobby project.
                        All values depicted are estimates and there is no guarantee to its accuracy.
                        It should not be used for financial planning or as financial advice</small>

                </div>
            </div>
            <div className="footer__copyright">
                <small>Finance Manager &copy; 2024 by Jon Kamis is licensed under CC BY-NC-ND 4.0</small>
            </div>
        </footer>
    )
}

export default Footer