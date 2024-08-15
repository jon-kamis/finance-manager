import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { RefreshUrl } from "../app-properties";
import { jwtDecode } from "jwt-decode";

const UserContext = createContext();

const emptyUser = {
    roles: [],
    username: "",
    userId: "",
    displayName: "",
    refreshTrigger: ""
};

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(emptyUser);
    const [refreshToken, setRefreshToken] = useState("");
    const [jwt, setJwt] = useState("");
    const TICK_INTERVAL = 600000;

    const updateUserTokens = (jwt, refreshToken) => {
        console.log(refreshToken)
        setRefreshToken(refreshToken);
        setJwt(jwt);

        if(jwt && (!user.username || jwtDecode(jwt).sub !== user.username)) {
            setUser({
                ...user,
                username: jwtDecode(jwt).sub,
                userId: jwtDecode(jwt).userId,
                displayName: jwtDecode(jwt).displayName,
                roles: jwtDecode(jwt).userRoles.split(',')
            });
        }
        setRefreshToken(refreshToken);
        setJwt(jwt)
    }

    useEffect(() => {
        const interval = setInterval(() => {
            if (refreshToken) {
                const requestOptions = {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: "include",
                    body: JSON.stringify({ refreshToken: refreshToken }),
                }

                fetch(`${RefreshUrl}`, requestOptions)
                    .then((response) => {
                        if (!response.ok) throw new Error(response.statusText);
                        else return response.json();
                    })
                    .then((data) => {
                        if (data.accessToken && data.refreshToken) {
                            updateUserTokens(data.accessToken, data.refreshToken);
                        } else {
                            throw new Error("expected tokens were not returned")
                        }
                    })
                    .catch(error => {
                        console.log("user is not logged in", error)
                    })
            }
        }, TICK_INTERVAL);

        return () => clearInterval(interval);
    }, [refreshToken]);

    const logoutUser = (user) => {
        setUser(emptyUser);
        setRefreshToken("");
    }

    const refreshUserData = (timestamp) => {
        setUser({
            ...user,
            refreshTrigger: timestamp
        })
    }

    return <UserContext.Provider value={{ user, jwt, updateUserTokens, logoutUser, refreshUserData }}>{children}</UserContext.Provider>
}

// Custom hook to consume the modal context anywhere in the app
export const useUserContext = () => {
    return useContext(UserContext);
}