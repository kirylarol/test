import {useEffect, useState} from "react";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal"
import {useParams} from "react-router-dom";
import Card from "react-bootstrap/Card";
import {Alert, Col, Row} from "react-bootstrap";
import ReceiptModal from "../ReceiptModal";
import ImageModal from "./Image/ImageModal";
import {instance} from "../../../axios/axiosConfig";
import {EmptyReceipt} from "../../../constants/Constants";


const NewModal = () => {

    const [ready, setReady] = useState(false)

    let accountid = useParams();

    const [show, setShow] = useState(true);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const [newReceipt, changeReceipt] = useState(EmptyReceipt);

    const [inputFields, setInputFields] = useState(newReceipt.positionList)

    const [showInputModal, changeInputShow] = useState(false)




    useEffect( () => {
        const fetch = async () => {
            const response = await instance.get("categories/all");
            setCategories(response.data);
            setReady(true)
        }
        fetch();
    }, []);

    const [categories, setCategories] = useState()


    const [imageModalState, setImageModalState] = useState(false)

    const closeOnConfirm = (newRec) => {
        changeInputShow(true);
        setInputFields(newRec.positionList)
    }

    const showImage = () => {
        handleModalClose()
        setImageModalState(true)
    }

    const [message, setMessage] = useState(null)

    const showInput = () => {
        closeImage()
        changeInputShow(true)
    }

    const closeImage = () => {
        setImageModalState(false)
    }


    const changeMode = true

    const handleModalClose = () => {
        changeInputShow(false);
    }

    const confirm = async () => {
        const url = `account/${accountid.id}/receipt/new`;
        try {
            newReceipt.positionList = inputFields
            let data = JSON.parse(JSON.stringify(newReceipt, (key, value) => (value === '' ? null : value), 2));
            data["date"] = new Date(data["date"]).getTime() || new Date().getTime();
            const result = await instance.post(url, data)
            setMessage("Чек успешно добавлен")
            handleModalClose()
        } catch (e) {
            setMessage("Произошла ошибка")
        }
    }

    return (
        <> {ready &&
            <>
                {message && <Alert> {message}</Alert>}
                <ImageModal newReceipt={newReceipt} state={imageModalState} close={closeImage}
                            closeOnConfirm={closeOnConfirm} setNewReceipt={changeReceipt}/>
                <ReceiptModal changeCategories={setCategories} receipt={newReceipt} confirm={confirm}
                              handle={handleModalClose} changeMode={changeMode}
                              state={showInputModal} categories={categories} changeInputFields={setInputFields}
                              inputFields={inputFields} setCurrReceipt={changeReceipt}/>
                <Modal
                    show={show}
                    onHide={handleClose}
                    backdrop="static"
                    keyboard={false}
                >
                    <Modal.Header closeButton>
                        <Modal.Title>Выберите вариант создания</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Row>
                            <Col>
                                <Card>
                                    <Card.Title className="m-2"> Вариант 1 </Card.Title>
                                    <Card.Body>
                                        <Button onClick={(e) => showInput()}> Добавить вручную</Button>
                                    </Card.Body>
                                </Card>
                            </Col>
                            <Col>
                                <Card>
                                    <Card.Title className="m-2"> Вариант 2 </Card.Title>
                                    <Card.Body>
                                        <Button onClick={(e) => showImage()}>Сканировать с фото</Button>
                                    </Card.Body>
                                </Card>
                            </Col>
                        </Row>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleClose}>
                            Отмена
                        </Button>
                    </Modal.Footer>
                </Modal>
            </>
        } </>
    );
}

export default NewModal