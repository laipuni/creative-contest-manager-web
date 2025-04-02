import React from 'react';
import loading_video from "../videos/loading-animation.webm"
import './notFound.css'

const NotFound = () => {
    return (
        <div className="not-found-container">
            <div className="not-found-content">
                {/* 배경 video */}
                <video src={loading_video}
                       autoPlay
                       loop
                       muted
                       className="not-found-bg" />

                {/* 404 메시지 */}
                <div className="not-found-text">
                    <h1 className="not-found1">404 Not Found</h1>
                    <p className="not-found2">페이지를 찾을 수 없습니다.</p>
                </div>
            </div>
        </div>
    );
};

export default NotFound;