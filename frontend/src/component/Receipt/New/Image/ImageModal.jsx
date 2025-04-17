import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal"
import {Alert, Form, Spinner} from "react-bootstrap";
import {useState} from "react";
import {instance} from "../../../../axios/axiosConfig";

const ImageModal = ({newReceipt, setNewReceipt, state, close, closeOnConfirm}) => {
    const [error, setError] = useState(null)
    const [isLoading, setIsLoading] = useState(false); // Состояние для отслеживания загрузки
    const handleSubmit = async (e) => {
        e.preventDefault();

        setIsLoading(true);
        const formData = new FormData();
        const image = e.target[0].files[0];

        formData.append("image", image);

        try {
            const response = await instance.post('/receipt/upload/image', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            })


            setIsLoading(false);

            const data = JSON.parse(JSON.stringify(response.data, (key, value) => value === null ? "" : value));
            data.shop = {
                name :''
            }
            setNewReceipt(data);
            closeOnConfirm(data);
        } catch (error) {

            setIsLoading(false);
            setError("Произошла ошибка при загрузке изображения.");
        }


    };

    return (<Modal show={state} onHide={close}>
            <Modal.Header closeButton>
                <Modal.Title>Загрузите фотографию и нажмите отправить</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {error && (
                    <Alert variant="danger">{error}</Alert>
                )}
                {isLoading ? (
                    <div className="text-center">
                        <Spinner animation="border"/>
                        <p>Идет загрузка...</p>
                    </div>
                ) : (
                    <Form className="m-2" onSubmit={handleSubmit}>
                        <Form.Control
                            accept="image/jpeg, image/png, image/gif"
                            className="m-2"
                            type="file"
                            placeholder="Загрузите фото"
                        />
                        <Button variant="primary" type="submit">
                            Отправить фото
                        </Button>
                    </Form>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={close}>
                    Отмена
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ImageModal