import {useEffect, useState} from "react";
import {instance} from "../../axios/axiosConfig";
import {NavLink, useParams} from "react-router-dom";
import Container from "react-bootstrap/Container";
import {Col, Form, FormCheck, Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";
import ReceiptChart from "./AccountPage/ReceiptChart";
import UserTable from "./AccountPage/UserTable";
import GoalTable from "./AccountPage/GoalTable";
import Button from "react-bootstrap/Button";
import {emptyAccount} from "../../constants/Constants";


const AccountPage = () => {


    const {id} = useParams();

    let newDate = new Date().toISOString().slice(0, 10);

    const [dateEnd, setDateEnd] = useState(newDate);

    const [data, setData] = useState(emptyAccount);

    const fetchData = async () => {
        try {
            const url = "account/ " + id
            const response = await instance.get(url);
            setData(response.data);
        } catch (error) {
            console.error("Ошибка при загрузке данных", error);
        }
    };


    useEffect(() => {
        fetchData();
    }, []);


    const [dateStart, setDateStart] = useState(data.account.dateCreated)

    const [result, setResult] = useState('');

    const [enabled, setEnabled] = useState(false)

    const [datalist, setDataList] = useState([])

    const filterList = () => {
        if (data.receiptList != null)
            setDataList(data.receiptList.filter(item => {
                        const dateA = new Date(dateStart.replace(/-/g, '/'));
                        const dateB = new Date(dateEnd.replace(/-/g, '/'));
                        let dateC
                        if (item.date !=null) {
                             dateC = new Date(item.date.replace(/-/g, '/'));
                        }else{
                            dateC = dateA
                        }
                        return (dateA - dateC <= 0) && (dateB - dateC >= 0)
                    }
                )
            )
    }

    useEffect(() => {
        filterList();
    }, [dateStart, dateEnd]);


    const handleCheck = (e) => {
        setEnabled(!enabled);
        if (enabled === true) {
            setDateEnd(newDate);
            setDateStart(data.account.dateCreated)
        }
    }


    const handleStartDate = (e) => {
        setDateStart(e.target.value)
    }

    const handleEndDate = (e) => {
        setDateEnd(e.target.value)
    }

    if (id === undefined || id === null) return


    return (
        <div style={{width: '99%'}}>
            <Container className="p-2 flex-column flex-grow-1 m-2 w-100" fluid>
                <Row className="m-2">
                    <Col>
                        <Row>
                            <Row className="flex-grow-1 border-2 m-2 rounded-1 justify-content-md-center">
                                <Card>
                                    <Card.Title> Общая информация </Card.Title>
                                    <Card.Subtitle className="m-1"> Название {data.account.name} </Card.Subtitle>
                                    <Card.Subtitle className="m-1"> Дата
                                        создания {data.account.dateCreated}</Card.Subtitle>
                                </Card>
                            </Row>
                            <Row className="flex-grow-1 border-2 m-2 rounded-1 justify-content-md-center">
                                <Card>
                                    <Card.Title> Выбрать период рассмотрения</Card.Title>
                                    <Card.Body>
                                        <Form.Check
                                            type="checkbox"
                                            style={{
                                                textAlign: "start"
                                            }}
                                            name="check"
                                            label="Включить форму"
                                            className="m-2"
                                            onClick={handleCheck}
                                        />
                                        <Form name="date">
                                            <Form.Control className="m-2" value={dateStart} onChange={handleStartDate}
                                                          type="date" disabled={!enabled}></Form.Control>
                                            <Form.Control className="m-2" value={dateEnd} onChange={handleEndDate}
                                                          type="date"
                                                          disabled={!enabled}>
                                            </Form.Control>
                                        </Form>
                                    </Card.Body>
                                </Card>
                            </Row>
                            <Row>
                                <Button variant="primary" className="h-100">
                                    <NavLink to={`/account/${id}/receipts`}>
                                        <text className="text-white">
                                            Перейти к чекам аккаунта
                                        </text>
                                    </NavLink>
                                </Button>
                            </Row>
                        </Row>
                    </Col>
                    <Col>
                        <Card className="rounded-1 border-2 ">
                            <Card.Title> График расходов</Card.Title>
                            <ReceiptChart receiptList={datalist} weight={data.weight}></ReceiptChart>
                        </Card>
                    </Col>
                </Row>
                <Row className="flex-row flex-wrap flex-grow-1">
                    <Col className="rounded-1 border-2 border-black mt-2">
                        <Card>
                            <Card.Title className="mt-2">
                                Пользователи </Card.Title>
                            <UserTable permission={data.userRole} userList={data.userList}></UserTable>
                        </Card>
                    </Col>
                    <Col className="rounded-1 border-2 border-black mt-2">
                        <Card>
                            <Card.Title className="mt-2"> Цели</Card.Title>
                            <GoalTable permission={data.userRole} goalList={data.goalList}></GoalTable>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    )

}

export default AccountPage