import React from "react";
import { Bar } from "react-chartjs-2";

export const CategoryDynamicChart = ({ data }) => {
    if (!Array.isArray(data)) {
        return <p>Нет данных</p>;
    }

    const labels = data.map((item) => item.key);
    const prices = data.map((item) => item.value.price);

    const chartData = {
        labels: labels,
        datasets: [
            {
                label: "Потрачено за промежуток",
                data: prices.flat(), // Use flat to flatten the array of arrays
                backgroundColor: "rgba(75, 192, 192, 0.6)",
                borderColor: "rgba(75, 192, 192, 1)",
                borderWidth: 1,
            },
        ],
    };

    return (
        <div style={{ height: "400px" }}>
            <h2>Динамика расходов</h2>
            <Bar
                data={chartData}
                options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        x: {
                            beginAtZero: true,
                        },
                        y: {
                            beginAtZero: true,
                        },
                    },
                    plugins: {
                        legend: {
                            display: false,
                        },
                    },
                    title: { text: "Динамика расходов", display: true },
                }}
            />
        </div>
    );
};
