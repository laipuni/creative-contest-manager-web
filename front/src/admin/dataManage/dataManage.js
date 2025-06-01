import AdminHeader from "../components/adminHeader/adminHeader";
import AdminSidebar from "../components/adminSidebar/adminSidebar";
import React, {useEffect, useState} from "react";
import './dataManage.css'
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
} from "recharts";
import apiClient from "../../templates/apiClient";

const DataManage = () => {
    const [organizationData, setOrganizationData] = useState([]);
    const [cityData, setCityData] = useState([])
    useEffect(() => {
        apiClient.get('/api/admin/statistics/organization')
            .then((res)=>{
                const rawData = res.data.data.distributionList;
                const aggregatedData = Object.values(
                    rawData.reduce((acc, item) => {
                        if (!acc[item.description]) {
                            acc[item.description] = { description: item.description, count: 0 };
                        }
                        acc[item.description].count += item.count;
                        return acc;
                    }, {})
                );
                console.log(aggregatedData);
                setOrganizationData(aggregatedData);
            })
            .catch((err)=>{})
    }, []);
    useEffect(() => {
        apiClient.get('/api/admin/statistics/members/city')
            .then((res)=>{
                const rawData = res.data.data.cityDistributionDtoList;
                const aggregatedData = Object.values(
                    rawData.reduce((acc, item) => {
                        if (!acc[item.city]) {
                            acc[item.city] = { city: item.city, count: 0 };
                        }
                        acc[item.city].count += item.count;
                        return acc;
                    }, {})
                );
                console.log(aggregatedData);
                setCityData(aggregatedData);
            })
            .catch((err)=>{})
    }, []);
    return (
        <div className="admin-teamList-container">
            <AdminHeader/>
            <div className="admin-main-container">
                <AdminSidebar height='1000px'/>
                <div className="admin-teamList-main-container">
                    <div className="admin-teamList-header">
                        <div className="admin-teamList-titlebox" style={{width: '97%'}}>
                            <div className="admin-teamList-title">데이터 분석</div>
                            <div className="admin-teamList-underline"></div>
                        </div>
                    </div>
                    <div className="admin-datamanage-container">
                        <div className="admin-datamanage-dashboard">
                            <div className="admin-datamanage-title-container">
                                <p className="admin-datamanage-title-text">직업 분포</p>
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart
                                        data={organizationData}

                                    >
                                        <XAxis dataKey="description" type="category" />
                                        <YAxis type="number" />
                                        <Tooltip />
                                        <Bar dataKey="count" fill="#444" barSize={30} /> />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </div>
                        <div className="admin-datamanage-dashboard">
                            <div className="admin-datamanage-title-container">
                                <p className="admin-datamanage-title-text">거주지(도시) 분포</p>
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart
                                        data={cityData}
                                    >
                                        <XAxis dataKey="city" type="category" />
                                        <YAxis type="number" />
                                        <Tooltip />
                                        <Bar dataKey="count" fill="#444" barSize={30} />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default DataManage;