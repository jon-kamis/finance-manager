import { createContext, useContext, useEffect, useState } from "react";

const UserContext = createContext();

const emptyUser = {
    jwt: "",
    roles: [],
    username: "",
    userId: "",
    displayName: ""
};

export const UserProvider = ({children}) => {
    const [user, setUser] = useState(emptyUser);

    const setUserHandler = (user) => {
        console.log(user)
        setUser(user);
    }

    const logoutUser = (user) => {
        setUser(emptyUser)
    }

    return <UserContext.Provider value={{user, setUserHandler, logoutUser}}>{children}</UserContext.Provider>
}

// Custom hook to consume the modal context anywhere in the app
export const useUserContext = () => {
    return useContext(UserContext);
}