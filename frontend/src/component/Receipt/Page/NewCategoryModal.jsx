import Modal from "react-bootstrap/Modal"
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";
import {useState} from "react";

const NewCategoryModal = ({state, handleSave, handleCancel}) => {

    const handleSaveClick = ()=>{
        handleSave(formValue);
    }
    const [formValue, setFormValue] = useState('');
    return (
        <>
            <Modal show={state} onHide={handleCancel}>
                <Modal.Header closeButton>
                    <Modal.Title>Добавить новую категорию</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Control value={formValue} onChange={ e => setFormValue(e.target.value)} type="text" placeholder="Введите название новой категории"></Form.Control>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCancel}>
                        Закрыть
                    </Button>
                    <Button onClick={handleSaveClick} variant="primary">
                        Сохранить категорию
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default NewCategoryModal;