import {Alert, Table} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {useState} from "react";
import {instance} from "../../../axios/axiosConfig";
import {useParams} from "react-router-dom";
import NewGoalModal from "./NewGoalModal";

const UserTable = ({goalList, permission}) => {

    let id = useParams();

    const [goalMessage, setGoalMessage] = useState();


    const calculateDiff = (date1, date2) => {
        const timeDiff = Math.abs(date2 - date1);
        return timeDiff / (1000 * 60 * 60 * 24).toFixed(2);
    }

    const calculatePlan = (date1Str, date2Str, target) => {
        const [day1, month1, year1] = date1Str.split("-").map(Number);
        const [day2, month2, year2] = date2Str.split("-").map(Number);
        const date1 = new Date(year1, month1 - 1, day1);
        const date2 = new Date(year2, month2 - 1, day2);
        return (target / calculateDiff(date1, date2)).toFixed(2);
    }

    const calculateReal = (date1Str, curr) => {
        const datenow = new Date();
        const [day1, month1, year1] = date1Str.split("-").map(Number);
        const date1 = new Date(year1, month1 - 1, day1);
        return (curr / calculateDiff(datenow, date1)).toFixed(2);
    }


    const [modalMessage, setModalMessage] = useState(false)
    const [newModalState, setNewModalState] = useState(false);

    const handleClose = () => {
        setNewModalState(false)
    }

    const handleSave = async (data) => {
        const url = `/account/${id.id}/goal/`
        try {
            let sendData = {...data}
            sendData["dateStart"] = new Date(sendData["dateStart"]).getTime()
            sendData["dateEnd"] = new Date(sendData["dateEnd"]).getTime()

            const response = await instance.post(url, sendData)
            if (response.status === 200) {
                setModalMessage("Цель успешно добавлена");
                setTimeout(() => {
                }, 2000);
                window.location.reload()
                setModalMessage(false)
            } else {
                setModalMessage(response.data.message)
            }
        } catch (e) {
            setModalMessage(e.response.data.message)
        }
    }

    const deleteGoal = async (goalId) => {
        const url = `/account/${id.id}/goal/${goalId}`
        try {
            const response = await instance.delete(url)
            if (response.status === 200) {
                setGoalMessage("Цель успешно удалена");
                setTimeout(() => {
                }, 2000);
                window.location.reload()
                setGoalMessage(false)
            } else {
                setGoalMessage(response.data.message)
            }
        } catch (e) {
            setGoalMessage(e.response.data.message)
        }
    }


    const handleButton = (e) => {
        setNewModalState(true)
    }


    return (
        <>
            {
                goalMessage && <Alert> {goalMessage}</Alert>
            }
            {
                newModalState &&
                <NewGoalModal changeMessage={setModalMessage} message={modalMessage} state={newModalState}
                              handleClose={handleClose} handleSave={handleSave}/>
            }
            <Button className="m-2 text-lg-center" onClick={handleButton}>Добавить цель</Button>
            <Table className="border-1" bordered border={2} striped hover>
                <thead>
                <tr>
                    <th>Название цели</th>
                    <th>Дата создания</th>
                    <th>Дата окончания</th>
                    <th> Цель</th>
                    <th> Потрачено</th>
                    <th>Плановые расходы в день</th>
                    <th>Реальные расходы в день</th>
                    {(permission === "ACCOUNT_CREATOR" || permission === "ACCOUNT_ADMIN") && (
                        <th>Удалить цель</th>
                    )}
                </tr>
                </thead>
                <tbody>
                {goalList.map((item, index) => (
                    <>
                        <tr key={index}>
                            <td>{item.name}</td>
                            <td>{item.created}</td>
                            <td>{item.valid}</td>
                            <td> &lt; {item.target}</td>
                            <td> {item.state}</td>
                            <td> {calculatePlan(item.created, item.valid, item.target)}</td>
                            <td> {calculateReal(item.created, item.state)}</td>
                            {(permission === "ACCOUNT_CREATOR" || permission === "ACCOUNT_ADMIN") && (
                                <td>
                                    <Button onClick={e => deleteGoal(item.id)}>Удалить цель</Button>
                                </td>
                            )
                            }


                        </tr>
                    </>
                ))}

                </tbody>
            </Table>
        </>
    );
};

export default UserTable;
