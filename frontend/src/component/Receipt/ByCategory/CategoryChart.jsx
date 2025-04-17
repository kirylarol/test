import React, { useEffect, useState } from "react";
import { Pie } from "react-chartjs-2";

export const CategoryPieChart = ({ data }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                data: [],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(255, 159, 64, 0.6)',
                    'rgba(255, 205, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                ],
                borderWidth: 4,
            },
        ],
    });

    useEffect(() => {
        const initializeChartData = () => {
            if (data && data.length > 0) {
                const labels = data.map((item) => item.categoryName);
                const prices = data.map((item) =>
                    item.positionList.reduce((sum, position) => {
                        return sum + position.price;
                    }, 0));

                setChartData({
                    labels: labels,
                    datasets: [
                        {
                            data: prices,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.6)',
                                'rgba(255, 159, 64, 0.6)',
                                'rgba(255, 205, 86, 0.6)',
                                'rgba(75, 192, 192, 0.6)',
                            ],
                            borderWidth: 4,
                            borderColor: "#777",
                        },
                    ],
                });
            }
        };
        initializeChartData();
    }, [data]);

    return (
        <div className="App">
            <Pie
                data={chartData}
                options={{
                    responsive: true,
                    title: { text: "Распределение по категориям", display: true },
                }}
            />
        </div>
    );
};
