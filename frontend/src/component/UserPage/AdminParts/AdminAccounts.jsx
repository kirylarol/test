import {useEffect, useState} from "react";
import {Spinner, Table} from "react-bootstrap";
import {instance} from "../../../axios/axiosConfig";

export const AdminAccounts = () => {
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, setError] = useState(false)
    const [data, setData] = useState({})

    const onDeleteAccount =  (item) =>{
        return () => {
            instance.delete(`/admin/accounts/${item.id}`).then(
                response => {
                    fetchAccounts();
                }
            ).catch(error => {
                    setError(error)
                }
            )
        }

    }

    const fetchAccounts = () =>{
        setIsLoaded(false)
        const response = instance.get("/admin/accounts").then(
            response => {
                setData(response.data)
                setIsLoaded(true)
            }
        ).catch(error =>{
                setError(error)
            }
        )
    }

    useEffect(() => {
       fetchAccounts()
    }, []);


    if (error) {
        return <div>Ошибка: {error.message}</div>;
    }

    if (!isLoaded) {
        return <div className="flex-grow-1" ><Spinner animation="border" variant="primary" /></div>;
    }

    const onDeleteItem = (item) => {
        return () => {
            instance.delete(`/admin/accounts/${item.id}`).then(
                response => {
                    window.location.reload()
                }
            ).catch(error => {
                    setError(error)
                }
            )
        }

    }

    return (
        <div className="m-3">
            <h1>Счета</h1>
            <Table className="table table-bordered">
                <thead>
                <tr>
                    <th>Название счета</th>
                    <th>Количество участников</th>
                    <th>Владелец</th>
                    <th>Удалить</th>
                </tr>
                </thead>
                <tbody>
                {data.map(item => (
                    <tr key={item.id}>
                        <td>{item.name}</td>
                        <td> {item.number}</td>
                        <td>{item.username}</td>
                        <td>
                            <button onClick={ onDeleteAccount(item) }>
                                Удалить
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </div>
    )
}