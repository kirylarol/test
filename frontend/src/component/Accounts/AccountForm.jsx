import {Alert, Form} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {useState} from "react";
import data from "bootstrap/js/src/dom/data";
import {instance} from "../../axios/axiosConfig";


const AccountForm = () => {
    let newDate = new Date().toISOString().slice(0, 10);
    const [date, setDate] = useState(newDate);


    const [enabledDate, setEnable] = useState(false);
    const [message, setMessage] = useState("");

    const handleCheck = (e) => {
        setEnable(!enabledDate);
        if (enabledDate === true) {
            setDate(newDate);
        }
    }
    const handleDate = (e) => {
        setDate(e.target.value)
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        let dataToSend = {}
        dataToSend = Object.fromEntries(formData);
        if (!enabledDate){
            delete dataToSend.check;
            dataToSend["date"] = new Date(newDate).getTime();
        }else{
            dataToSend["date"] = new Date().getTime();
            delete dataToSend.check;
        }
        instance.post("/accounts/new",dataToSend).then(response => {
            (response.data && response.data.message) ? setMessage(response.data.message) : setMessage("Произошла ошибка")
        }).catch(error => {
            error.request ? setMessage(error.request) : setMessage("Произошла ошибка")
            }
        )
    }


    const today = new Date();
    return (
        <div className="d-flex flex-column align-items-center flex-grow-1">
            <div className="d-flex flex-row align-items-center flex-grow-1 " style={{
                minWidth: 250,
                width: '40%'
            }}>
                <Form className=" m-2   mt-0 -50 align-items-center flex-grow-1 h-50 " onSubmit={handleSubmit}>
                    <Form.Label> Создать новый счет</Form.Label>
                    {
                        message && <Alert>{message}</Alert>
                    }
                    <Form.Control type="text" className="m-2" placeholder={"Введите название счета"} name="name"></Form.Control>
                    <Form.Check
                        type="checkbox"
                        style={{
                            textAlign: "start"
                        }}
                        name = "check"
                        label="Использовать другую дату"
                        className="m-2"
                        value={enabledDate}
                        onChange={handleCheck}
                    />
                    <Form.Control type="date" name="date" className="m-2" value={date} onChange={handleDate}
                                  placeholder={"Введите дату открытия"} disabled={!enabledDate}></Form.Control>
                    <Button type="submit" variant="secondary">Отправить</Button>
                </Form>
            </div>
        </div>
    )
}

export default AccountForm;