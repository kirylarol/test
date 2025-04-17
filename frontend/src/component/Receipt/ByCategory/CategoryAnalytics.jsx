import {useEffect, useState} from "react";
import {Accordion, Col, Row, Spinner} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import {useLocation, useNavigate} from "react-router-dom";
import {instance} from "../../../axios/axiosConfig";
import {DATE_PRECISIONS} from "../../../constants/Constants";
import {convertLongToDate, convertToDate} from "../../../utils/utils";
import Card from "react-bootstrap/Card";
import {CategoryPieChart} from "./CategoryChart";
import {CategoryDynamicChart} from "./CategoryDynamicChart";
import {keyboard} from "@testing-library/user-event/dist/keyboard";
import Button from "react-bootstrap/Button";


export const CategoryAnalytics = () => {
    const [loaded, setLoaded] = useState(false)
    const [dataByCategory, setDataByCategory] = useState([]);
    const [dataByDate, setDataByDate] = useState([]);
    const [error, setError] = useState(false)
    let url = useLocation();
    const [precision, setPrecision] = useState(DATE_PRECISIONS.DAY);

    const [showPieChart, setShowPieChart] = useState(false);

    const groupByDate = (data, precision) => {
        let dataByDateBuff = []
        data.map((item) => {
            item.positionList.map(
                (position) => {
                    let date = convertToDate(convertLongToDate(position.date), precision);
                    if (dataByDateBuff[date] === undefined) {
                        dataByDateBuff[date] = [];
                        dataByDateBuff[date]["price"] = 0;
                    }
                    if (dataByDateBuff[date][item.categoryName] === undefined) {
                        dataByDateBuff[date][item.categoryName] = [position];
                        dataByDateBuff[date][item.categoryName]["price"] = parseFloat(position.price);
                    } else {
                        dataByDateBuff[date][item.categoryName].push(position);
                        let sum = dataByDateBuff[date][item.categoryName]["price"] + parseFloat(position.price);
                        dataByDateBuff[date][item.categoryName]["price"] = parseFloat(sum.toFixed(2));
                    }
                    let sum = dataByDateBuff[date]["price"] + parseFloat(position.price);
                    dataByDateBuff[date]["price"] = parseFloat(sum.toFixed(2));
                }
            )
        })

        let sortedByDate = Object.keys(dataByDateBuff).sort(
            (a, b) => {
                return a.split(/[.|-]/).slice().reverse() > b.split(/[.|-]/).slice().reverse() ? 1 : -1
            }
        ).map(key => ({key, value: dataByDateBuff[key]}));
        return sortedByDate;
    }


    useEffect(() => {
        try {
            instance.get(url.pathname).then(response => {
                    setDataByCategory(response.data);
                    let dataByDateBuff = {};
                    setDataByDate(groupByDate(response.data, precision));
                    setDataByCategory(response.data)
                    console.log(response.data);
                    setLoaded(true)
                }
            )
        } catch (e) {
            setError(true)
        }
    }, []);

    useEffect(() => {
        setDataByDate(groupByDate(dataByCategory, precision));
    }, [precision, dataByCategory]);


    if (error) {
        return (
            <Container>
                <h1>Ошибка при загрузке данных</h1>
            </Container>
        )
    }

    if (!loaded) {
        return (
            <div className="flex-grow-1 w-100 h-100 d-flex align-items-center justify-content-center">
                <Spinner animation="border"/>
            </div>
        );
    } else
        return (
            <>
                <h1>Аналитика по категориям</h1>
                <Row className="m-2">
                    <Col>
                        {
                            dataByDate.map((item) => {
                                    console.log(item)
                                    return (
                                        <>
                                            <Accordion alwaysOpen>
                                                <Accordion.Item eventKey={item.key}>
                                                    <Accordion.Header>{item.key} Потрачено: {item.value.price}</Accordion.Header>
                                                    <Accordion.Body>
                                                        {
                                                            Object.entries(item.value).map(([key, value]) => {
                                                                if (key === "price") {
                                                                    return (
                                                                        <></>
                                                                    )
                                                                } else
                                                                    return (
                                                                        <>
                                                                            <Accordion alwaysOpen>
                                                                                <Accordion.Item eventKey={key}>
                                                                                    <Accordion.Header> {key} - {value.price} </Accordion.Header>
                                                                                    <Accordion.Body>
                                                                                        {
                                                                                            Object.entries(value).map(([key, value]) => {
                                                                                                return (
                                                                                                    <Row
                                                                                                        className="align-self-start">
                                                                                                        <Col
                                                                                                            className="text-start">
                                                                                                            {value.name}
                                                                                                        </Col>
                                                                                                        <Col
                                                                                                            className="text-end">
                                                                                                            {value.price}
                                                                                                        </Col>
                                                                                                    </Row>
                                                                                                )
                                                                                            })
                                                                                        }
                                                                                    </Accordion.Body>
                                                                                </Accordion.Item>
                                                                            </Accordion>

                                                                        </>
                                                                    )
                                                            })
                                                        }
                                                    </Accordion.Body>
                                                </Accordion.Item>
                                            </Accordion>
                                        </>
                                    )
                                }
                            )
                        }
                    </Col>
                    <Col className="h-50" style={{height: '50%'}}>
                        <Row>
                            <Col>
                            <Button className="m-4">
                                <text onClick={() => setShowPieChart(!showPieChart)}>Переключить график</text>
                            </Button>
                            <Button>
                                <text onClick={() => {
                                    const precisions = Object.values(DATE_PRECISIONS);
                                    const index = precisions.indexOf(precision);
                                    setPrecision(precisions[ (index + 1 )% 4 ] )
                                }}
                                > Переключить точность
                                </text>
                            </Button>
                            </Col>
                            <div style={{maxWidth : '600px'}}>
                                {
                                    showPieChart ? <CategoryPieChart data={dataByCategory}/> :
                                        <CategoryDynamicChart data={dataByDate}/>
                                }
                            </div>
                        </Row>

                    </Col>
                </Row>
            </>
        )
}