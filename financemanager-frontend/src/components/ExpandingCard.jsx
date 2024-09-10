import { AiOutlineMinus, AiOutlinePlus } from 'react-icons/ai'
import Card from './Card'
import { useState } from 'react'
import './css/expanding-card.css'

const ExpandingCard = ({className, content, hiddenContent}) => {
    const [showAnswer, setShowAnswer] = useState(false);

  return (
    <Card className={className} onClick={() => setShowAnswer(prev => !prev)}>
        <div>
            {content}
            <button className="expandingCard__icon">
                {showAnswer ? <AiOutlineMinus/> : <AiOutlinePlus/>}
            </button>
        </div>
        {showAnswer && <div className = "expandingCard__hiddenSection">{hiddenContent}</div>}
    </Card>
  )
}

export default ExpandingCard