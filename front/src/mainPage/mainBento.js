import { useState, useEffect } from "react";
import { Pause, Play, ChevronLeft, ChevronRight } from "lucide-react";
import mainImage1 from "../styles/images/main_visual_01.jpg"
import mainImage2 from "../styles/images/main_visual_02.jpg"
import image1 from "../styles/images/block2_img.jpg"
import image2 from "../styles/images/block3_img.jpg"
import image3 from "../styles/images/block4_img.jpg"
import "./mainBento.css"
import {Link} from "react-router-dom";

const slides = [
    {
        id: 1,
        image: mainImage1,
    },
    {
        id: 2,
        image: mainImage2,
    },
];

const categories = [
    {
        title: "참가신청안내",
        subtitle: "Registration Information",
        text: "CPS FESTIVAL의\n접수기간 및 신청방법에 대하여\n상세하게 알려드립니다.",
        image: image1,
        tail: "left"
    },
    {
        title: "창의문제학습실",
        subtitle: "Problem learning center",
        text: "단계별 퍼즐 창의문제를 통해서\n창의력 문제해결능력을\n높여보세요!",
        image: image2,
        tail: "right"
    },
    {
        title: "예선대회",
        subtitle: "Preliminary Contest",
        text: "온라인으로 실시되는\n예선대회를 통해서\n여러분의 실력을 알 수 있습니다.",
        image: image3,
        tail: "bottom"
    }
];

export default function MainBento() {
    const [current, setCurrent] = useState(0);
    const [paused, setPaused] = useState(false);

    useEffect(() => {
        if (paused) return;
        const id = setInterval(() => {
            setCurrent((c) => (c + 1) % slides.length);
        }, 5000);
        return () => clearInterval(id);
    }, [paused]);


    return (
        <div className="cps-wrapper">
            <div className="cps-slider">
                <img src={slides[current].image} alt="메인 슬라이드" className="cps-slide-image"/>
                <div className="cps-overlay">
                    <p className="cps-small">CREATIVE PROBLEM SOLVING</p>
                    <h1 className="cps-title" style={{color: 'orange'}}>CPS FESTIVAL</h1>
                    <h2 className="cps-title">창의문제해결능력</h2>
                    <p className="cps-desc">
                        IT융복합 시대를 선도할 수 있는 창의력 인재 육성을 목표로<br/> 협력, 창의, 도전을 통한
                        문제해결능력을 배양
                    </p>
                </div>
                <div className="cps-controls">
                    <button onClick={() => setCurrent((current - 1 + slides.length) % slides.length)}>
                        <ChevronLeft/>
                    </button>
                    <button onClick={() => setPaused((p) => !p)}>
                        {paused ? <Play/> : <Pause/>}
                    </button>
                    <button onClick={() => setCurrent((current + 1) % slides.length)}>
                        <ChevronRight/>
                    </button>
                </div>
            </div>

            <div className="cps-categories">
                {categories.map((cat, i) => (
                    <div key={i} className={`cps-category-row ${i % 2 === 0 ? 'normal' : 'reverse'}`}>
                        {/* 텍스트 박스 */}
                        { i === 0 ? (
                            <Link to="/test/info" className={`cps-cat-box cps-tail-${cat.tail}`}>
                                <p className="cps-cat-sub">{cat.subtitle}</p>
                                <h3 className="cps-cat-title">{cat.title}</h3>
                                <p className="cps-cat-text">{cat.text}</p>
                            </Link>
                        ) : i === 1 ? (
                            <div
                                className={`cps-cat-box cps-tail-${cat.tail}`}
                                onClick={() => alert("준비 중입니다")}
                                style={{ cursor: "pointer" }}
                            >
                                <p className="cps-cat-sub">{cat.subtitle}</p>
                                <h3 className="cps-cat-title">{cat.title}</h3>
                                <p className="cps-cat-text">{cat.text}</p>
                            </div>
                        ) : (
                            <Link to="/register/info" className={`cps-cat-box cps-tail-${cat.tail}`}>
                                <p className="cps-cat-sub">{cat.subtitle}</p>
                                <h3 className="cps-cat-title">{cat.title}</h3>
                                <p className="cps-cat-text">{cat.text}</p>
                            </Link>
                        )}

                        {/* 이미지 부분 */}
                        <div
                            className="cps-cat-image"
                            style={{backgroundImage: `url(${cat.image})`}}
                        />
                    </div>
                ))}
            </div>
        </div>
    );
}