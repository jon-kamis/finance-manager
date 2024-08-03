import React from 'react'
import features from './data'
import Card from '../../../../components/Card'
import './features.css'

const Features = () => {
    return (
        <section id="features">
            <h2>Features</h2>
            <div className="features__cards">
                {
                    features.map(f =>
                        <Card key={f.id} className="features__card">
                            <span className="features__card-icon">{f.icon}</span>
                            <h5>{f.title}</h5>
                            <small>{f.desc}</small>
                        </Card>
                    )
                }
            </div>
        </section>
    )
}

export default Features