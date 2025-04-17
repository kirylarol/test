import {useEffect, useState} from "react";
import {Col, Row, Spinner, Table} from "react-bootstrap";
import {instance} from "../../../axios/axiosConfig";
import Button from "react-bootstrap/Button";
import {ACCOUNT_ROLES} from "../../../constants/Constants";
import Container from "react-bootstrap/Container";


export const AdminUsers = () => {

    const [data, setData] = useState([]);



    const fecthUsers = () =>{
        const response = instance.get("/admin/users").then(
            response => {
                setData(response.data)
                setLoaded(true)
            }
        ).catch(error => {
                setError(error)
            }
        )
    }

    const onDeleteItem = (item) => {
        return () => {
            instance.delete(`/admin/users/${item.login}`).then(
                response => {
                    fecthUsers();

                }

            ).catch(error => {
                    setError(error)
                }
            )
        }
    }

    const [loaded, setLoaded] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        const response = instance.get("/admin/users").then(
            response => {
                setData(response.data)
                setLoaded(true)
            }
        ).catch(error => {
                setError(error)
            }
        )
        if (data.length !== 0) setLoaded(true)
    }, []);

    if (error) {
        return <div className="h-100">Ошибка: {error.message}</div>;
    }

    if (!loaded) {
        return (
            <div className="h-100 m-5">
                <Spinner className animation="border" role="status">
                    <span className="sr-only">Loading...</span>
                </Spinner>
            </div>
        )
    }else{
        return (
            <Container className="m-5 bg-light rounded-5 ">
            <Table className="w-100 h-100  mt-3" bordered responsive>
                    <tr>
                        <th>
                            Имя пользователя
                        </th>
                        <th>
                            Имя
                        </th>
                        <th>
                            Фамилия
                        </th>
                        <th>
                            Уровень доступа
                        </th>
                        <th>
                            Удалить
                        </th>
                    </tr>
                {data.map((item, index) => {
                    return (
                        <tr>
                            <td>
                                { item.login }
                            </td>
                            <td>
                                { item.name }
                            </td>
                            <td>
                                { item.surname }
                            </td>
                            <td>
                            </td>
                            <td>
                            <Button className="m-1" onClick={onDeleteItem(item)} variant="secondary">
                                Удалить
                            </Button>
                            </td>
                        </tr>
                    )
                })}
            </Table>
            </Container>
        )
    }

}