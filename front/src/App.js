import './App.css';
import {BrowserRouter} from "react-router-dom";
import {useEffect} from "react";
import axios from "axios";
import AppRoutes from "./templates/appRoutes";

function App() {
    //csrf 토큰 생성
    useEffect(() => {
        axios.get("/api/csrf")
            .then((data) => {
            })
            .catch((error) => {alert('서버가 연결되어 있지 않습니다.')});
    }, []);

  return (
    <div className="App">
      <BrowserRouter>
          <AppRoutes />
      </BrowserRouter>
    </div>
  );
}

export default App;
