import { Routes, Route, useLocation } from "react-router-dom";
import Join2 from "../join/join2";
import Main from "../mainPage/main";
import Join1 from "../join/join1";
import {useEffect} from "react";
import MemberLogin from "../login/member/memberLogin";
import AdminLogin from "../login/admin/adminLogin";
import TestInfo from "../testRegister/testInfo/testInfo";
import RegisterInfo from "../testRegister/registerInfo/registerInfo";
import RegisterTeam from "../testRegister/registerInfo/registerTeam";
import NotFound from "../notFound/notFound";

function AppRoutes() {
    const location = useLocation();

    useEffect(() => {
        const isJoinPath = location.pathname.startsWith("/join");
        if (!isJoinPath) {
            sessionStorage.removeItem("isChecked");
        }
    }, [location]);

    return (
        <Routes>
            <Route path="/" element={<Main />}></Route>
            <Route path="join/policy" element={<Join1 />}></Route>
            <Route path="join/register" element={<Join2 />}></Route>
            <Route path="member/login" element={<MemberLogin />}></Route>
            <Route path="admin/login" element={<AdminLogin />}></Route>
            <Route path="test/info" element={<TestInfo />}></Route>
            <Route path="register/info" element={<RegisterInfo />}></Route>
            <Route path="register/team" element={<RegisterTeam />}></Route>
            <Route path="*" element={<NotFound />}></Route>
        </Routes>
    );
}

export default AppRoutes;