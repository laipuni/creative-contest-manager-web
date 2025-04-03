import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./mainPage/main";
import NotFound from "./notFound/notFound";
import Join1 from "./join/join1";
import Join2 from "./join/join2";
import MemberLogin from "./login/member/memberLogin";
import AdminLogin from "./login/admin/adminLogin";
import TestInfo from "./testRegister/testInfo/testInfo";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
          <Routes>
              <Route path="/" element={<Main />}></Route>
              <Route path="join/policy" element={<Join1 />}></Route>
              <Route path="join/register" element={<Join2 />}></Route>
              <Route path="member/login" element={<MemberLogin />}></Route>
              <Route path="admin/login" element={<AdminLogin />}></Route>
              <Route path="test/info" element={<TestInfo />}></Route>
              <Route path="*" element={<NotFound />}></Route>
          </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
