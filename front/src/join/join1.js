import React, {useState} from 'react';
import SubHeader from '../components/subHeader/subHeader'
import './join1.css'
import {useNavigate} from "react-router-dom";
const Join1 = () => {
    const [isChecked, setIsChecked] = useState(false);
    const navigate = useNavigate();

    const handleCheckbox = () => {
        setIsChecked(!isChecked);
    };

    const handleNextPage = () => {
        if (isChecked) {
            navigate('/join2');
        }
        else {
            alert('약관에 동의하셔야 합니다.')
        }
    };

    const handleExit = () => {
        navigate('/');
    };

    return (
        <div className="join1-page-container">
            <SubHeader />
            <div className="join1-content-container">
                <div className="join1-title">
                    <p className="join1-title-text">회원가입</p>
                    <div className="main-header-line" style={{width:'100%'}}></div>
                </div>
                <p className="join1-subtitle">※ 회원가입은 무료이며, 회원님의 정보는 동의없이 공개되지 않습니다.
                    <br />※ 회원가입을 원하시면 아래의 약관을 읽고 동의하여 주십시오.</p>
                <div className="join1-mainbox">
                    <div className="join1-textbox">
                        <p>제1조(목적)<br/>
                            이 약관은 CPS FESTIVAL(이하 "CPS FESTIVAL"라 한다)이 무료로 제공하는 미니홈피 및 자료실, 게시판 서비스(이하 "서비스"라 한다)에 대한 이용
                            조건 및 운영 절차에 관한 사항을 규정함을 목적으로 합니다.</p>

                        <p>제2조(약관의 효력 및 변경)<br/>
                            ① 이 약관은 홈페이지의 온라인으로 공지함으로써 효력을 발생하며, 합리적인 사유가 발생할 경우 관련 법령에 위배되지 않는 범위 안에서 개정할 수 있습니다.<br/>
                            ② 변경된 약관에 동의하지 않을 경우 회원 탈퇴를 할 수 있으며, 변경된 약관의 효력 발생일 이후에도 서비스를 계속 사용할 경우 약관의 변경 사항에 동의한 것으로
                            간주합니다.</p>

                        <p>제3조(용어의 정의)<br/>
                            이 약관에서 사용하는 용어의 정의는 다음 각호의 1과 같습니다.<br/>
                            1. 회 원 : 이용자 아이디를 부여 받은자<br/>
                            2. 아 이 디 : 회원이 선정하고 CPS FESTIVAL가 승인하는 문자와 숫자의 조합<br/>
                            3. 비밀번호 : 비밀 보호를 위해 회원 자신이 설정한 문자와 숫자의 조합</p>

                        <p>제4조(약관 외 준칙)<br/>
                            이 약관에 명시되지 않은 사항은 전기통신기본법, 전기통신사업법, 정보통신망이용촉진에관한법률 및 기타 관련 법령의 규정에 의합니다.</p>

                        <p>제5조(서비스의 종류)<br/>
                            제공하는 서비스는 미니홈피, 각종 게시판, 자료실 등이 있습니다.</p>

                        <p>제6조(가입하기)<br/>
                            ① 서비스는 본 이용 약관 하단의 "회원가입하기" 단추를 누르면 가입 신청과 동시에 사용이 허락됩니다.<br/>
                            ② 회원으로 가입하여 본 서비스를 이용하고자 하는 사람은 CPS FESTIVAL에서 요청하는 제반 정보(이름, 주민등록번호, 연락처 등)를 등록하여야 하며, 실명으로
                            등록하지 않은 회원은 일체의 권리를 주장할 수 없습니다.<br/>
                            ③ CPS FESTIVAL는 실명 확인 조치를 할 수 있으며, 타인의 명의(이름 및 주민등록번호)를 도용하여 가입신청을 한 회원의 아이디는 삭제 및 관련 법령에 따라
                            처벌을 받을 수 있습니다.</p>

                        <p>제7조(서비스 제공)<br/>
                            ① 서비스 제공은 가입 신청 후 관리자의 승인 이후 이루어집니다.<br/>
                            ② 서비스의 이용 시간은 연중 무휴 1일 24시간을 원칙으로 합니다. 다만, 시스템 점검 등 특별한 사유가 있는 경우에는 예외로 합니다.</p>

                        <p>제8조(서비스 제공의 중지)<br/>
                            CPS FESTIVAL는 다음 각 호의 1에 해당하는 경우에는 서비스 제공을 중지하거나 제한할 수 있습니다.<br/>
                            1. 국가의 비상사태 또는 천재지변 등 불가항력의 사유가 발생한 경우<br/>
                            2. 전기통신사업법에 규정된 기간통신사업자가 전기통신서비스를 중지했을 경우<br/>
                            3. 서비스용 설비의 보수, 공사 또는 장애로 인한 부득이한 경우<br/>
                            4. 서비스 이용의 폭주 등으로 정상적인 서비스 이용에 지장이 있을 경우<br/>
                            5. 기타 원활한 시스템 관리를 위해 조치가 필요한 경우</p>

                        <p>제9조(회원 아이디 관리)<br/>
                            ① CPS FESTIVAL는 회원에 대하여 약관에 정하는 바에 따라 회원 아이디를 부여합니다.<br/>
                            ② 회원 아이디는 원칙적으로 변경이 불가하며 부득이한 사유로 인하여 변경하고자 하는 경우에는 이미 보유한 아이디를 해지하고 재가입하여야 합니다.<br/>
                            ③ 회원이 부여받은 아이디는 타인에게 전매 또는 양도할 수 없습니다.<br/>
                            ④ 회원에게 부여된 아이디와 비밀번호 누설로 인해 발생하는 문제 또는 제3자의 부정 사용 등으로 발생하는 모든 결과에 대한 책임은 당해 회원에게 있습니다.<br/>
                            ⑤ 자신의 아이디가 부정하게 사용된 경우 당해 회원은 반드시 CPS FESTIVAL에 그 사실을 통보하여야 합니다.</p>

                        <p>제10조(이용 제한)<br/>
                            CPS FESTIVAL는 다음 각 호의 1에 해당하는 회원 아이디를 삭제할 수 있습니다.<br/>
                            1. 허위로 가입 신청한 경우<br/>
                            2. 본인의 실명으로 가입하지 않은 경우<br/>
                            3. 타인의 아이디와 비밀번호를 도용한 경우<br/>
                            4. 6개월 이상 홈페이지에 로그인(접속)하지 않는 경우<br/>
                            5. 서비스 운영을 고의로 방해하는 경우<br/>
                            6. 본 약관 제13조(회원의 게시물) 규정에 반한 경우</p>

                        <p>제11조(회원의 의무)<br/>
                            ① 회원은 공공질서 및 미풍양속에 위반되는 내용물이나 제3자의 저작권 등 기타권리를 침해하는 내용물을 발송하는 행위를 하지 않아야 합니다. 만약 위 사항을 위배하였을
                            때 발생하는 결과에 대한 모든 책임은 당해 회원에게 있습니다.</p>

                        <p>제12조(회원의 게시물)<br/>
                            회원이 게재한 내용물이 다음 각 호의 1에 해당한다고 판단되는 경우에는 사전통지 없이 삭제할 수 있습니다.<br/>
                            1. 공공질서 및 미풍양속에 위반하는 내용인 경우<br/>
                            2. 범죄 행위에 관련된 경우<br/>
                            3. 타인의 명예를 훼손하는 내용인 경우<br/>
                            4. CPS FESTIVAL의 저작권, 제3자의 저작권 등 기타 권리를 침해하는 내용인 경우<br/>
                            5. 기타 관련 법령에 위배된다고 판단되는 경우</p>

                        <p>제13조(의무)<br/>
                            ① CPS FESTIVAL는 서비스 제공과 관련하여 회원의 신상정보를 본인의 승낙 없이 제3자에게 누설, 배포하지 않습니다. 다만, 각호의 1에 해당하는 경우에는
                            그러하지 않습니다.<br/>
                            1. 관계법령에 의하여 수사상의 목적으로 관계기관으로부터 요구받은 경우<br/>
                            2. 정보통신윤리위원회의 요청이 있는 경우<br/>
                            3. 기타 관계법령에 의한 경우<br/>
                            ② CPS FESTIVAL는 원활한 서비스를 위해 회원의 신상정보에 관한 통계 자료를 작성하여 사용할 수 있습니다.<br/>
                            ③ 회원은 약관에서 규정하는 모든 사항을 준수하여야 합니다.</p>

                        <p>제14조(저작권 권리)<br/>
                            서비스에 이용된 자료에 대한 권리는 다음 각 호의 1과 같습니다.<br/>
                            1. 회원은 게재된 자료를 상업적인 목적으로 이용할 수 없습니다.<br/>
                            2. 게시물에 대한 권리와 책임은 게시자에게 있습니다.<br/>
                            3. CPS FESTIVAL는 회원의 게시물에 대한 신뢰도나 정확성 등 내용에 관해서는 책임을 지지 않습니다.</p>

                        <p>제15조(이용자의 개인정보보호)<br/>
                            CPS FESTIVAL는 관련법령이 정하는 바에 따라서 이용자 등록정보를 포함한 이용자의 개인정보를 보호하기 위해 노력합니다.</p>

                        <p>제16조(운영 세칙)<br/>
                            기타 필요한 사항은 CPS FESTIVAL가 정하여 공지사항 란에 게시하여 모든 회원에게 알립니다.</p>

                        <p>제17조(분쟁 조정)<br/>
                            서비스 이용시 발생한 분쟁에 대해 소송이 제기될 경우 CPS FESTIVAL의 소재지를 관할하는 법원을 관할법원으로 합니다.</p>

                        <p>부 칙<br/>
                            제1조(시행일)<br/>
                            이 약관은 2011년 04월부터 시행합니다.</p>
                    </div>
                    <div className="join1-buttonbox">
                        <div className="join1-buttonbox-upper">
                            <input
                                type="checkbox"
                                checked={isChecked}
                                onChange={handleCheckbox}/>
                            <p className="join1-upper-text">위 약관에 동의합니다.</p>
                        </div>
                        <div className="join1-buttonbox-lower">
                            <button className="join1-button" onClick={handleNextPage}>확인</button>
                            <button className="join1-button"
                                    style={{background: 'lightgray', color: 'black'}}
                                    onClick={handleExit}>나가기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Join1;