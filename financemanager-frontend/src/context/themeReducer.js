export const themeReducer = (state, action) => {
    // For primary colors
    if (action.type === 'color-1') {
        return { ...state, primary: 'color-1' }
    } else if (action.type === 'color-2') {
        return { ...state, primary: 'color-2' }
    } else if (action.type === 'color-3') {
        return { ...state, primary: 'color-3' }
    } else if (action.type === 'color-4') {
        return { ...state, primary: 'color-4' }
    } else if (action.type === 'color-5') {
        return { ...state, primary: 'color-5' }
    } else if (action.type === 'color-6') {
        return { ...state, primary: 'color-6' }
    }

    // For background colors
    if (action.type === "bg-light") {
        return {...state, background: 'bg-light'}
    } else if (action.type === "bg-dark") {
        return {...state, background: 'bg-dark'}
    }
}