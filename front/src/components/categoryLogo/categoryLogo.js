import './categoryLogo.css'
import '../../styles/styles.css'
import React from 'react'
import trophy from '../../styles/images/test_info_logo.png'

const CategoryLogo = ({logoTitle, imgSrc}) => {
    return (
        <div className="categoryLogo-container">
            <div className="categoryLogo-textbox">
                <p className="categoryLogo-title">{logoTitle}</p>
                <p className="categoryLogo-subtitle">Creative Problem Solving Festival에 오신 것을 환영합니다.</p>
            </div>
            <div className="categoryLogo-imagebox">
                <img className="categoryLogo-image" src={imgSrc} alt="logo-image"/>
            </div>
        </div>
    )
}

export default CategoryLogo;