import './about.css'
import Image from "../../../../assets/stock_graph.jpg"
import Card from "../../../../components/Card"
import data from './data'

const About = () => {
  return (
    <section id="about">
      <div className="container about__container">
        <div className="about__left">
          <div className="about__image">
            <img src={Image} alt="Graph Image" />
          </div>
        </div>
        <div className="about__right">
          <h2>What is Finance Manager?</h2>
          <div className="about__cards">
            {
              data.map(a => (
                <Card key={a.id} className="about__card">
                  <span className="about__card-icon">{a.icon}</span>
                  <h5>{a.title}</h5>
                  <small>{a.desc}</small>
                </Card>
              ))
            }
          </div>

          <p>
            Hi, my name is Jonathan Kamis and I am the creator of Finance Manager.
          </p>
          <p>
            I am passionate about building projects and wanted to build something truely useful. That project became finance manager. My primary reasons for building it were to have fun and to learn more about react
            as I believe it can help further advance my career. Originally, Finance manager looked a lot rougher than what you see here. I built the back end of the application in Go with my very first attempt at react for the
            user facing side of the application. I was happy with my results but the finished product reflected the learning experience. I wanted to give the project a second chance at life and the results are what you see on your
            screen now. Additionally, the backend was switched out from Go to Java Spring Boot which I had significantly more experience in. 
          </p>
        </div>

      </div>
    </section>
  )
}

export default About