import {Routes, Route, useLocation, useNavigate} from "react-router-dom";
import Join2 from "../auth/join/join2";
import Main from "../mainPage/main";
import Join1 from "../auth/join/join1";
import {useEffect} from "react";
import MemberLogin from "../auth/login/member/memberLogin";
import AdminLogin from "../auth/login/admin/adminLogin";
import TestInfo from "../testRegister/testInfo/testInfo";
import RegisterInfo from "../testRegister/registerInfo/registerInfo";
import RegisterTeam from "../testRegister/registerInfo/registerTeam";
import NotFound from "../notFound/notFound";
import MyProfile from "../auth/myProfile";
import TestSubmitInfo from "../testTake/realTest/testSubmitInfo";
import TestSubmit from "../testTake/realTest/testSubmit";
import PastTest from "../testTake/pastTest/pastTest";

function AppRoutes() {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const isJoinPath = location.pathname.startsWith("/join");
        const authenticatedPaths = ["/register/team", "/member/profile", "/test/realTest"];
        const isAuthenticatedPath = authenticatedPaths.some(path => location.pathname.startsWith(path));
        if (!isJoinPath) {
            sessionStorage.removeItem("isChecked");
        }
        if (isAuthenticatedPath) {
            if (localStorage.getItem("isAuthenticated") !== "true"){
                navigate('/member/login', {replace: true, state: {from: location.pathname}});
            }
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
            <Route path="member/profile" element={<MyProfile />}></Route>
            <Route path="test/realTest/info" element={<TestSubmitInfo />}></Route>
            <Route path="test/realTest/submit" element={<TestSubmit />}></Route>
            <Route path="test/pastTest" element={<PastTest />}></Route>
            <Route path="*" element={<NotFound />}></Route>
        </Routes>
    );
}

export default AppRoutes;