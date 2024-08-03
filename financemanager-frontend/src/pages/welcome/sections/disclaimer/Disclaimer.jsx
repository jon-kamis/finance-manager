import React from 'react'
import './disclaimer.css'

const Disclaimer = () => {
    return (
        <section id="disclaimer">
            <div className="container disclaimer__container">
                <h2>Disclaimer</h2>
                <h3>Finance Manager is a hobby project</h3>
                <h3>All values depicted are estimates and there is no guarantee to its accuracy</h3>
                <h3>It should <b><i>not</i></b> be used for financial planning or as financial advice </h3>
            </div>
        </section>
    )
}

export default Disclaimer