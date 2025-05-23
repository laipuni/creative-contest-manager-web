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
import MyProfile from "../auth/profile/myProfile";
import TestSubmitInfo from "../testTake/realTest/testSubmitInfo";
import TestSubmit from "../testTake/realTest/testSubmit";
import PastTest from "../testTake/pastTest/pastTest";
import Certificate from "../supportPage/certificatePage/certificate";
import QnA from "../supportPage/qnaPage/qna";
import TeamList from "../admin/teamList/teamList";
import TestManage from "../admin/testManage/testManage";
import FindIdPage from "../auth/login/member/findIdPage";
import ResetPasswordPage from "../auth/login/member/resetPwPage";
import CertificateManage from "../admin/certificateManage/certificateManage";
import ProfileSelect from "../auth/profile/profileSelect";
import PasswordAuth from "../auth/profile/passwordAuth";
import MemberList from "../admin/memberList/memberList";
import NoticeList from "../community/notice/noticeList";
import FreeBoard from "../community/freeBoard/freeBoard";
import NoticeManage from "../admin/notice/noticeManage";
import NoticeWrite from "../admin/notice/noticeWrite";
import NoticeDetail from "../admin/notice/noticeDetail";

function AppRoutes() {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const isJoinPath = location.pathname.startsWith("/join");
        const authenticatedPaths = ["/register/info", "/register/team", "/member/profile", "/test/realTest"];
        const isAuthenticatedPath = authenticatedPaths.some(path => location.pathname.startsWith(path));
        const adminPaths = ["/admin/teamList", "/admin/testManage"];
        const isAdminPath = adminPaths.some(path => location.pathname.startsWith(path));

        if (!isJoinPath) {
            sessionStorage.removeItem("isChecked");
        }
        if (isAuthenticatedPath) {
            if (localStorage.getItem("isAuthenticated") !== "true" && localStorage.getItem("isAdmin") !== "true") {
                navigate('/member/login', {replace: true, state: {from: location.pathname}});
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
            <Route path="community/notice" element={<NoticeList />}></Route>
            <Route path="community/freeBoard" element={<FreeBoard />}></Route>
            <Route path="join/policy" element={<Join1 />}></Route>
            <Route path="join/register" element={<Join2 />}></Route>
            <Route path="member/login" element={<MemberLogin />}></Route>
            <Route path="member/login/findId" element={<FindIdPage />}></Route>
            <Route path="member/login/resetPw" element={<ResetPasswordPage />}></Route>
            <Route path="admin/login" element={<AdminLogin />}></Route>
            <Route path="test/info" element={<TestInfo />}></Route>
            <Route path="register/info" element={<RegisterInfo />}></Route>
            <Route path="register/team" element={<RegisterTeam />}></Route>
            <Route path="member/profile" element={<ProfileSelect />}></Route>
            <Route path="member/profile/auth" element={<PasswordAuth />}></Route>
            <Route path="member/profile/edit" element={<MyProfile />}></Route>
            <Route path="test/realTest/info" element={<TestSubmitInfo />}></Route>
            <Route path="test/realTest/submit" element={<TestSubmit />}></Route>
            <Route path="test/pastTest" element={<PastTest />}></Route>
            <Route path="certificate/info" element={<Certificate />}></Route>
            <Route path="qna" element={<QnA/>}></Route>
            <Route path="admin/memberList" element={<MemberList />}></Route>
            <Route path="admin/teamList" element={<TeamList />}></Route>
            <Route path="admin/testManage" element={<TestManage />}></Route>
            <Route path="admin/certificates" element={<CertificateManage />}></Route>
            <Route path="admin/notices" element={<NoticeManage />}></Route>
            <Route path="admin/notices/write" element={<NoticeWrite />}></Route>
            <Route path="admin/notices/detail" element={<NoticeDetail />}></Route>
            <Route path="*" element={<NotFound />}></Route>
        </Routes>
    );
}

export default AppRoutes;