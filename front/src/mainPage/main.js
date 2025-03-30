import React from 'react';
import MainHeader from "../components/mainHeader/mainHeader";
import "./main.css"

const Main = () => {
    return (
        <div className="main-page-container">
            <div className="main-content-container">
                <MainHeader />
                <div className="main-content">
                    <div className="main-content-bento">
                        <div className="bento-top">
                            <div className="bento-top-content">
                                <p>hi</p>
                            </div>
                            <div className="bento-top-content" style={{background: "rgba(89, 167, 255, 0.36)"}}></div>
                        </div>
                        <div className="bento-bot">
                            <div className="bento-bot-left">
                                <div className="bento-bot-left-upper">
                                    <p>hi</p>
                                </div>
                                <div className="bento-bot-left-lower">
                                    <p>hi</p>
                                </div>
                            </div>
                            <div className="bento-bot-right">
                                <p>hi</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Main;