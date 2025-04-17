import {Col, Form, Row, Spinner} from "react-bootstrap";
import {useEffect, useState} from "react";
import {instance} from "../../axios/axiosConfig";
import Card from "react-bootstrap/Card";




export const UserPage =  () =>{

    const [isLoaded, setIsLoaded] = useState(false)
    const [data, setData] = useState({})
    const [error, setError] = useState(false)

    const formOnSubmit = (e) =>{
        e.preventDefault()
        let newUser = data

        newUser.identityDTO.name = e.target.name.value
        newUser.identityDTO.surname = e.target.surname.value
        newUser.userDTO.login = e.target.login.value
        newUser.userDTO.password = e.target.password.value

        instance.patch("/user", newUser).then( response =>
            setData(response.data)
        ).catch(error =>{
            setError("Ошибка при обновлении пользователя")
        }
        )
    }



    useEffect(() => {
        const response = instance.get("/user").then(
            response => {
                setData(response.data)
                setIsLoaded(true)
            }
        ).catch(error =>{
            setError(error)
        }
        )
    }, []);
    if (error) {
        return <div>Ошибка: {error.message}</div>;
    }
    else if (!isLoaded) {
        return <div className="flex-grow-1" ><Spinner animation="border" variant="primary" /></div>;
    }
    else

    return (
        <div className="m-3">
            <Row>
                <Col>
                    <Card>
                        <Card.Header>Профиль</Card.Header>
                        <Card.Body>
                            <h5>Имя: {data.identityDTO.name}</h5>
                            <h5>Фамилия: {data.identityDTO.surname}</h5>
                            <h5>Логин: {data.userDTO.login}</h5>
                        </Card.Body>
                    </Card>
                </Col>
                <Col>
                    <Card>
                    <Card.Header> Изменить данные</Card.Header>
                    <Card.Body>
                        <Form onSubmit={formOnSubmit} >
                            Новое имя
                            <Form.Control defaultValue={data.identityDTO.name} type="text" name="name" />
                            Новая фамилия
                            <Form.Control defaultValue={data.identityDTO.surname} type="text" name="surname" />
                            Новый логин
                            <Form.Control defaultValue={data.userDTO.login} type="text" name="login" />
                            Новый пароль
                            <Form.Control type="password" name="password" />
                            <Form.Control className="m-3" type="submit" value="Отправить" />
                        </Form>
                    </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    )
}
