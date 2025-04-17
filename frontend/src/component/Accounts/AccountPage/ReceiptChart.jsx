import React, {useEffect, useState} from 'react';
import {Bar, Line} from "react-chartjs-2";

import Chart from 'chart.js/auto';
import Button from "react-bootstrap/Button";


function ReceiptChart({receiptList, weight}) {

    function compare(a, b) {
        let dateA = new Date(a.date);
        let dateB = new Date(b.date);

        return dateA - dateB;
    }

    const updateDates = () => {
        let dates = receiptList.map((receipt) => {
            const date = new Date(receipt.date);
            if (isNaN(date.getDate())) {
                const dateParts = receipt.date.split('-');
                if (dateParts.length === 3) {
                    const d = parseInt(dateParts[1]);
                    const m = parseInt(dateParts[0]);
                    const y = parseInt(dateParts[2]);
                    if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
                        switch (chartState) {
                            case 0:
                                return `${d.toString().padStart(2, '0')}.${m.toString().padStart(2, '0')}.${y}`;
                            case 1:
                                return `${m.toString().padStart(2, '0')}.${y}`;
                            case 2:
                                return `${y}`;
                        }

                    }
                }
            } else {
                const d = date.getDate().toString().padStart(2, '0');
                const m = (date.getMonth() + 1).toString().padStart(2, '0');
                const y = date.getFullYear();
                switch (chartState) {
                    case 0:
                        return `${d.toString().padStart(2, '0')}.${m.toString().padStart(2, '0')}.${y}`;
                    case 1:
                        return `${m.toString().padStart(2, '0')}.${y}`;
                    case 2:
                        return `${y}`;
                }
            }

        })
        setDates([... new Set(dates)]);
    }


    const updateYourPrice = () => {
        let w = Object.values(receiptList.reduce((acc, receipt) => {
            let key;
            let localdate = new Date(receipt.date);
            switch (chartState) {
                case 0:
                    key = localdate.toString();
                    break
                case 1:
                    key = localdate.getMonth() + '.' + localdate.getFullYear();
                    break
                case 2:
                    key = localdate.getFullYear();
            }
            if (!acc[key]) {
                acc[key] = 0;
            }
            acc[key] += parseFloat(receipt.total) * parseFloat(weight);
            return acc;
        }, {}))
        setYourprices(
            w
        )
    }

    const updatePrices = () => {
        let w = Object.values(receiptList.reduce((acc, receipt) => {
            let key;
            let localdate = new Date(receipt.date);
            switch (chartState) {
                case 0:
                    key = localdate.toString();
                    break
                case 1:
                    key = localdate.getMonth() + '.' + localdate.getFullYear();
                    break
                case 2:
                    key = localdate.getFullYear();
            }
            if (!acc[key]) {
                acc[key] = 0;
            }
            acc[key] += parseFloat(receipt.total);
            return acc;
        }, {}))

        setPrices(w);
    }

    const [chartState, setState] = useState(0);
    const [dates, setDates] = useState([]);
    const [prices, setPrices] = useState([]);
    const [yourprices, setYourprices] = useState([]);

    let obj = {
        labels:
        dates,
        datasets:
            [
                {
                    label: 'Цена чека',
                    data: prices,
                    fill: 'rgb(152,210,33)',
                    tension: 0.1,
                },
                {
                    label: 'Цена Вашего чека',
                    data: yourprices,
                    fill: 'rgb(71,101,101)',
                    tension: 0.1,
                },
            ]
    }

    const [dataset, setDataset] = useState(obj);

    const updateDataset = () => {
        let obj = {
            labels:
            dates,
            datasets:
                [
                    {
                        label: 'Цена чека',
                        data: prices,
                        fill: 'rgb(152,210,33)',
                        tension: 0.1,
                    },
                    {
                        label: 'Цена Вашего чека',
                        data: yourprices,
                        fill: 'rgb(71,101,101)',
                        tension: 0.1,
                    },
                ]
        }
        setDataset(obj);

    }
    const buttonHandler = (e) => {
        setState((chartState + 1) % 3)
    }

    receiptList.sort(compare)

    const updateAll = () => {
        updateDates();
        updatePrices()
        updateYourPrice();
    }

    useEffect(() => {
        updateAll();
    }, [receiptList])


    useEffect(() => {
        updateAll();
    }, [chartState])

    useEffect(() => {
        updateDataset();
    }, [prices, yourprices, dates])

    dataset.labels.sort((a, b) => {
         return b.split('').reverse().join('') < a.split('').reverse().join('');
    });


    const options = {
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Дата покупки',
                },
            },
            y: {
                title: {
                    display: true,
                    text: 'Цена',
                },
            },
        },
    };

    return (
        <>
            <Button style={{width: "30%"}} size="sm" className="ms-5" onClick={buttonHandler}>Переключить
                состояние</Button>
            <Bar data={dataset} options={options}/>
        </>
    );
}

export default ReceiptChart;