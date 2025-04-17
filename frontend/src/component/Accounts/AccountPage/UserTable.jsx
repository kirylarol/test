import {Alert, Table} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {useState} from "react";
import NewPersonModal from "./NewPersonModal";
import {ACCOUNT_ROLES} from "../../../constants/Constants";
import {instance} from "../../../axios/axiosConfig";
import {useParams} from "react-router-dom";
import {almostWhole} from "chart.js/helpers";

const UserTable = ({userList,setUserList,permission}) => {
    let id = useParams()

    const [newUserState, setNewUserState] = useState(false);

    const handleClose = () => {
        setNewUserState(false)
    }

    const handleSave = async (newUser) => {
        console.log(newUser.username, newUser.weight, newUser.role)
        const url = `/account/${id.id}/adduser`
        try {


            const response = await instance.post(url, newUser)
            if (response.status === 200){
                setAddUserMessage("Пользователь успешно добавлен в счет");
                setTimeout(() => {
                }, 2000);
                window.location.reload()
                setNewUserState(false)
            }else{
                setAddUserMessage(response.data.message)
            }
        }catch (e) {
            setAddUserMessage(e.response.data.message)
        }

    }


    const handleButton =  (e) =>{
        setNewUserState(true)
    }

    const [addUserMessage, setAddUserMessage] = useState()
    const [message, setMessage] = useState()

    const handleDelete = async (e) =>{
        const login = e.target.value;
        let url = `account/${id.id}/deleteuser/${login}`
        try {
            const response = await instance.delete(url);
            setMessage(response.data.message);
            setTimeout(() => {
            }, 2000);
            window.location.reload();
        }catch (e){
            setMessage(e.response.data.message);
        }

    }

    return (
        <>
            {message && <Alert> {message} </Alert>}
            {
                newUserState &&<NewPersonModal state={newUserState} handleSave={handleSave} message={addUserMessage} changeMessage={setAddUserMessage}  handleClose={handleClose} />
            }
            
        <Table className="border-1" bordered border={2} striped hover>
            <thead>
            <tr>
                <th>Фамилия и имя пользователя</th>
                <th>Роль в аккаунте</th>
                <th>Проценты</th>
                {(permission === "ACCOUNT_CREATOR" || permission === "ACCOUNT_ADMIN") && (
                    <th>Удалить пользователя</th>
                )}
            </tr>
            </thead>
            <tbody>
            {userList.map((item, index) => (
                <tr key={index}>
                    <td>{item.user.identity.name + " " + item.user.identity.surname}</td>
                    <td>{ACCOUNT_ROLES[item.permission]}</td>
                    <td>{parseFloat(item.weight) * 100}%</td>
                    {(permission === "ACCOUNT_CREATOR" || permission === "ACCOUNT_ADMIN") && (
                        <td>
                            <Button value={item.user.login} onClick={handleDelete}>Удалить пользователя</Button>
                        </td>
                    )
                    }
                </tr>
            ))}
            {(permission === "ACCOUNT_CREATOR" || permission === "ACCOUNT_ADMIN") &&  <Button onClick={handleButton}>Добавить пользователя в аккаунт</Button>

            }
                </tbody>
        </Table>
            </>
    );
};

export default UserTable;
