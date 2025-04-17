import Button from "react-bootstrap/Button";
import {Alert, Form, Modal} from "react-bootstrap";
import {useState} from "react";


const NewPersonModal = ({state, handleClose, handleSave, message, changeMessage}) => {

    const [username, setUsername] = useState();
    const [weight, setWeight] = useState(50);
    const [role, setRole] = useState("ACCOUNT_USER")

    const save = () => {
        if (username.toString().length < 3) {
            let userMessage = ", Имя пользователя должно содержать больше 2 букв"
            if (!message.toString().includes(userMessage))
                changeMessage([...message, userMessage])
            return
        } else {
            changeMessage(undefined);
        }
        let newObject = {
            username: username,
            weight: weight,
            role: role
        }
        handleSave(newObject);
    }

    const changeForm = (e) => {
        let value = parseFloat(e.target.value)
        if (value < 100 && value > 0) {
            changeMessage(undefined)
            setWeight(value)
        } else {
            changeMessage("Проценты должны принадлежать от 0 до 100")
            value > 100 ? setWeight(100) : setWeight(0)
        }
    }

    return (
        <>

            <Modal show={state} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Введите пользователя для добавления</Modal.Title>

                </Modal.Header>
                <Modal.Body>
                    {
                        message && <Alert> {message} </Alert>
                    }
                    <Form>
                        <Form.Control type="text" className="m-2" value={username}
                                      onChange={e => setUsername(e.target.value)}
                                      placeholder={"Введите username пользователя"}></Form.Control>
                        <Form.Control type="number" className="m-2" min="0" max="100" value={weight}
                                      onChange={changeForm}
                                      placeholder={"Введите проценты пользователя"}></Form.Control>
                        <Form.Select value={role} onChange={e => setRole(e.target.value)}>
                            <option value="ACCOUNT_USER"> Пользователь счета
                            </option>
                            <option value="ACCOUNT_ADMIN">
                                Администратор счета
                            </option>
                        </Form.Select>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={save}>
                        Добавить пользователя
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default NewPersonModal