import React, { useContext, useEffect } from 'react';
import { Button } from "@mui/material"
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router";
import { setCredentials } from './store/authSlice';
import { useDispatch } from 'react-redux';
function App() {
    const { token, tokenData, logIn, logOut, isAuthenticated } = useContext(AuthContext)
    const dispatch = useDispatch();
    const [authReady, setAuthReady] = userState(false);

    useEffect(() => {
        if(token){
            dispatch(setCredentials({ token, user: tokenData }));
            setAuthReady(true);
        }
    }, [token, tokenData, dispatch, setAuthReady]);
    return (
        <Router>
             
            <Button variant='contained' color='primary'
            onClick={
                ()=>{
                    logIn();
                }
            }>Log in</Button>
        </Router>
    );
}

export default App;