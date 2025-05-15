import './App.css';
import {BrowserRouter} from "react-router-dom";
import {useEffect} from "react";
import axios from "axios";
import AppRoutes from "./templates/appRoutes";
import ScrollToTopButton from "./components/scrollTop/scrollTop";

function App() {
    //csrf 토큰 생성
    useEffect(() => {
        axios.get("/api/csrf")
            .then((data) => {
            })
            .catch((error) => {alert('서버가 연결되어 있지 않습니다.')});
    }, []);

    return (
        <BrowserRouter>
            <div className="App">
                <AppRoutes />
                <ScrollToTopButton />
            </div>
        </BrowserRouter>
    );
}

export default App;
