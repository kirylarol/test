import Button from "react-bootstrap/Button";
import {Alert, Form, Modal} from "react-bootstrap";
import {useState} from "react";


const NewGoalModal = ({state, handleClose, handleSave, message, changeMessage}) => {
    let newDate = new Date().toISOString().slice(0, 10);
    const [title, setTitle] = useState("")
    const [target, setTarget] = useState(100);
    const [startDate, setStartDate] = useState(newDate);
    const [endDate, setEndDate] = useState(newDate)

    const save = () => {

        let newObject = {
            title : title,
            dateStart: startDate,
            dateEnd: endDate,
            target: target
        }
        handleSave(newObject);
    }

    const changeTotal = (e) => {
        let value = parseFloat(e.target.value)
        if  (value > 0) {
            changeMessage(undefined)
            setTarget(value)
        } else {
            changeMessage("Цель должна быть больше нуля")
            setTarget(0)
        }
    }

    return (
        <>
            <Modal show={state} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Добавить цель</Modal.Title>

                </Modal.Header>
                <Modal.Body>
                    {
                        message && <Alert> {message} </Alert>
                    }
                    <Form>
                        Введите название цели
                        <Form.Control type="text" className="m-2" value={title} onChange={e=>{setTitle(e.target.value)}} />
                        Дата начала
                        <Form.Control type="date" className="m-2" value={startDate}
                                      onChange={e => setStartDate(e.target.value)}
                                      placeholder={"Введите дату начала"}></Form.Control>
                        Дата окончания
                        <Form.Control type="date" className="m-2" value={endDate}
                                      onChange={e => setEndDate(e.target.value)}
                                      placeholder={"Введите username пользователя"}></Form.Control>
                        Цель
                        <Form.Control type="number" className="m-2" value={target}
                                      onChange={e=> setTarget(e.target.value) }
                                      placeholder={"Цель"}></Form.Control>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={save}>
                        Добавить цель
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default NewGoalModal