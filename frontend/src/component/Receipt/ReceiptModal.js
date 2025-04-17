import {ButtonGroup, Col, Form, InputGroup, Modal, ModalBody, Row, Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import parseDate from "../../utils/utils";
import Button from "react-bootstrap/Button";
import NewCategoryModal from "./Page/NewCategoryModal";
import {instance} from "../../axios/axiosConfig";


const ReceiptModal = ({
                          state,
                          receipt,
                          setCurrReceipt,
                          changeMode,
                          categories,
                          inputFields,
                          changeInputFields,
                          confirm,
                          handle,
                          changeCategories
                      }) => {

    let buffdate = {};
    try {
        const dateValue = new Date(receipt.date);
        if (!isNaN(dateValue.getTime())) {
            buffdate = dateValue;
            buffdate.setDate(buffdate.getDate() + 1)
        } else {
            buffdate = new Date();
        }
    } catch (e) {
        buffdate = new Date();
    }
    let newDate = buffdate.toISOString().slice(0, 10);

    const [date, setDate] = useState(newDate)
    let category = {}
    category.categoryname = ''

    const calculateTotal = (receiptList) => {
        try {
            return receiptList.map(position => position.price).flat()
                .reduce((acc, price) => acc + parseFloat(price), 0);
        } catch (e) {
            return 0
        }

    }

    const handleFormChange = (index, event) => {
        let data = [...inputFields];
        data[index][event.target.name] = event.target.value;
        changeInputFields(data);
        receipt.total = calculateTotal(data)
    }


    const save = (e) => {
        e.preventDefault();
        confirm(receipt);
    }

    const removeFields = (index) => {
        let data = [...inputFields];
        data.splice(index, 1)
        changeInputFields(data)
        receipt.total = calculateTotal(data)
    }


    const addField = () => {
        let newfield = {name: '', category: '', price: ''}
        changeInputFields([...inputFields, newfield]);
        receipt.total = calculateTotal(inputFields)
    }

    const handleDate = (e) => {
        let newReceipt = receipt;
        newReceipt.date = e.target.value
        setCurrReceipt(newReceipt)

    }
    console.log(date)

    const returnCategoryByName = (categories, category) => {
        if (category === null) return 0;
        let categoryName = category.categoryName
        const index = categories.findIndex((category) => category.categoryName === categoryName);
        return index !== -1 ? index : 0;
    }



    const [newCategoryModal, setCategoryModal] = useState(false)

    const [newCategoryValue, setCategoryValue] = useState("")


    function handleCategory(index, event) {
        let data = [...inputFields];
        if (event.target.value === "-1") {
            setCategoryModal(true)
            data[index]["category"] = categories[0]
        } else {
            data[index]["category"] = categories[event.target.value];
        }
        changeInputFields(data);
    }

    const handleClose = () => {
        setCategoryModal(false);
    }

    const handleSave = (newValue) => {
        setCategoryModal(false);
        const newCategory = {
            categoryName: newValue
        }
        let newCategories = categories;
        newCategories.push(newCategory)
        changeCategories(newCategories);
    }

    const handleReceipt = (e) => {
        let newReceipt = receipt;
        newReceipt.shop.name = e.target.value
        setCurrReceipt(newReceipt)
    }

    return (
        <>
            {newCategoryModal &&
                <NewCategoryModal className = "w-75" state={newCategoryModal} handleSave={handleSave} newCategoryValue={newCategoryValue}
                                  setCategoryValue={setCategoryValue} handleCancel={handleClose}/>}
            <Modal size="lg"
                   show={state} onHide={handle}>
                <Modal.Header closeButton>
                    <Modal.Title>Чек</Modal.Title>
                </Modal.Header>
                <ModalBody>
                    <Form>
                        <Form.Control type="text" defaultValue={receipt.shop.name || " "} onChange={handleReceipt}
                                      disabled={!changeMode}></Form.Control>
                        {!changeMode ? <Form.Control type="text" value={date} on disabled></Form.Control> :
                            <Form.Control type="date" defaultValue={date} onChange={handleDate}></Form.Control>
                        }
                        <Row className="mt-2">
                            <Col sm={3}>Продукт</Col>
                            <Col sm={3}>Категория</Col>
                            <Col sm={3}>Цена</Col>
                        </Row>
                        {
                            inputFields != null && inputFields.map((item, index) => (
                                <Form>
                                    <InputGroup className="mb-3 ">
                                        <Form.Control type="text" name="name" value={item.name}
                                                      onChange={event => handleFormChange(index, event)}
                                                      disabled={!changeMode}></Form.Control>
                                        <Form.Select type="text" name="category"
                                                     defaultValue={returnCategoryByName(categories, item.category)}
                                                     onChange={event => handleCategory(index, event)}
                                                     disabled={!changeMode}>
                                            {categories.map((item, index) => (
                                                <option key={index} value={index}>
                                                    {item.categoryName}
                                                </option>
                                            ))}
                                            <option value={-1}> Добавить категорию</option>
                                        </Form.Select>
                                        <Form.Control type="text" name="price"
                                                      onChange={event => handleFormChange(index, event)}
                                                      value={item.price} disabled={!changeMode}></Form.Control>
                                        {changeMode && <Button onClick={event => removeFields(index)}>Удалить</Button>}
                                    </InputGroup>

                                </Form>
                            ))
                        }
                        <Row className="bg-light">
                            <Col>
                            </Col>
                            <Col>
                                Итого
                            </Col>
                            <Col>
                                {receipt.total}
                            </Col>
                        </Row>
                    </Form>
                    <Row className="justify-content-center">
                        {changeMode && <> <ButtonGroup> <Button className="m-2" variant="primary"
                                                                onClick={save}>
                            Save Changes
                        </Button>
                            <Button className="m-2" onClick={addField}>Add More..</Button>
                        </ButtonGroup>

                        </>
                        }
                    </Row>
                </ModalBody>
            </Modal>
        </>
    )

}


export default ReceiptModal