import Navbar from '../../components/Navbar'
import Footer from '../../components/Footer'
import Theme from '../../modals/theme/Theme';
import WelcomeHeader from './sections/header/WelcomeHeader';
import Disclaimer from './sections/disclaimer/Disclaimer';
import Features from './sections/features/Features';
import About from './sections/about/About';

const Welcome = () => {
  return (
    <div className="welcome">
      <Navbar />
      <WelcomeHeader />
      <About />
      <Features />
      <Disclaimer />
      <Theme />
      <Footer />
    </div>
  )
}

export default Welcome