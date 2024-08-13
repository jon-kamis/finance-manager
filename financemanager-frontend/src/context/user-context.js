import { createContext, useContext, useEffect, useState } from "react";

const UserContext = createContext();

const emptyUser = {
    jwt: "",
    roles: [],
    username: "",
    userId: "",
    displayName: "",
    refreshTrigger: ""
};

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(emptyUser);

    const setUserHandler = (user) => {
        setUser(user);
    }

    const logoutUser = (user) => {
        setUser(emptyUser)
    }

    const refreshUserData = (timestamp) => {
        setUser({
            ...user,
            refreshTrigger: timestamp
        })
    }

    return <UserContext.Provider value={{ user, setUserHandler, logoutUser, refreshUserData }}>{children}</UserContext.Provider>
}

// Custom hook to consume the modal context anywhere in the app
export const useUserContext = () => {
    return useContext(UserContext);
}