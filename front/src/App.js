import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./mainPage/main";
import NotFound from "./notFound/notFound";
import Join1 from "./join/join1";
import Join2 from "./join/join2";
import MemberLogin from "./login/member/memberLogin";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
          <Routes>
              <Route path="/" element={<Main />}></Route>
              <Route path="join" element={<Join1 />}></Route>
              <Route path="join2" element={<Join2 />}></Route>
              <Route path="member-login" element={<MemberLogin />}></Route>
              <Route path="*" element={<NotFound />}></Route>
          </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
