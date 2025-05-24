import React from "react";

const DetailComponent = ({ data }) => {
    return (
        <div className="admin-teamList-body-title" style={{backgroundColor: 'lightpink'}}>
            <div className="admin-teamList-body-title-textbox">
                <p className="admin-teamList-body-title-text">거주지 : </p>
                <p className="admin-teamList-body-title-text"
                style={{lineHeight: '120%', overflowX: 'auto'}}>{data.street} <br/>- {data.detail}</p>
            </div>
            <div className="admin-teamList-body-verticalLine"></div>
            <div className="admin-teamList-body-title-textbox">
                <p className="admin-teamList-body-title-text">전화번호 : </p>
                <p className="admin-teamList-body-title-text">{data.phoneNumber}</p>
            </div>
            <div className="admin-teamList-body-verticalLine"></div>
            <div className="admin-teamList-body-title-textbox">
                <p className="admin-teamList-body-title-text">이메일 : </p>
                <p className="admin-teamList-body-title-text">{data.email}</p>
            </div>
        </div>
    )
};

export default DetailComponent;