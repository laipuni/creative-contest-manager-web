import React from 'react';
import MainHeader from "../components/mainHeader/mainHeader";
//import MainHeader from 'src/components/mainHeader/mainHeader';

const Main = () => {
    return (
        <div className="main-page-container">
            <div className="main-content-container">
                <MainHeader />
                <div className="main-content">
                    <h3>안녕하세요. 메인페이지 입니다.</h3>
                </div>
            </div>
        </div>
    );
};

export default Main;