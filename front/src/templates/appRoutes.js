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
import Certificate from "../supportPage/certificatePage/certificate";
import QnA from "../supportPage/qnaPage/qna";
import TeamList from "../admin/teamList/teamList";
import TestManage from "../admin/testManage/testManage";

function AppRoutes() {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const isJoinPath = location.pathname.startsWith("/join");
        const authenticatedPaths = ["/register/team", "/member/profile", "/test/realTest"];
        const isAuthenticatedPath = authenticatedPaths.some(path => location.pathname.startsWith(path));
        const adminPaths = ["/admin/teamList", "/admin/testManage"];
        const isAdminPath = adminPaths.some(path => location.pathname.startsWith(path));

        if (!isJoinPath) {
            sessionStorage.removeItem("isChecked");
        }
        if (isAuthenticatedPath) {
            if (localStorage.getItem("isAuthenticated") !== "true"){
                navigate('/member/login', {replace: true, state: {from: location.pathname}});
            }
        }
        if (isAdminPath){
            if (localStorage.getItem("isAdmin") !== "true"){
                navigate('/admin/login', {replace: true, state: {from: location.pathname}});
            }
        }

        if (location.pathname === "/admin/login"){
            if (localStorage.getItem("isAdmin") === "true"){
                navigate('/admin/teamList');
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
            <Route path="certificate/info" element={<Certificate />}></Route>
            <Route path="qna" element={<QnA/>}></Route>
            <Route path="admin/teamList" element={<TeamList />}></Route>
            <Route path="admin/testManage" element={<TestManage />}></Route>
            <Route path="*" element={<NotFound />}></Route>
        </Routes>
    );
}

export default AppRoutes;