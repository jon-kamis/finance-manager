import Navbar from '../../components/Navbar'
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
    </div>
  )
}

export default Welcome