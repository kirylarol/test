import {Table} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {useState} from "react";
import ReceiptModal from "../ReceiptModal";

import {parseDate} from "../../../utils/utils";
import {instance} from "../../../axios/axiosConfig";
import {NavLink} from "react-router-dom";

const ReceiptTable = ({receiptList, userid, permission, setList, weight, account, categories, setCategories}) => {

    const grantedAccess = (item) => {
        if (permission[item.account.account.id] === "ACCOUNT_CREATOR" || "ACCOUNT_ADMIN") return true;
        return item.user.id === userid;
    }

    const confirm = async (receipt) => {
        receipt.positionList = inputFields
        receiptList[id] = receipt
        setList(receiptList)
        const url = `receipt/${receipt.receiptId}/update`
        const submitData = receiptList[id];
        const data = JSON.parse(JSON.stringify(receiptList[id], (key, value) => (value === '' ? null : value), 2));
        data["date"] =new Date(submitData.date).getTime();
        const result = await instance.post(url, data)
        handleClose()
    }
    const [id, setId] = useState(0)

    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const [filter, setFilter] = useState(false)

    const handleFilter = (e) => {
        setFilter(!filter)

    }

    const handleClick = (e) => {
        handleShow();
        setChange(false);
        setId(e.target.id)
        setCurrReceipt(receiptList.at(e.target.id))
        changeInputFields(receiptList.at(e.target.id).positionList.slice(0))
    }

    const [onchange, setChange] = useState(false)

    const [currReceipt, setCurrReceipt] = useState(receiptList.at(0));


    const [inputFields, changeInputFields] = useState(receiptList.at(0).positionList);


    const deleteCheck = (e) => {
        let index = e.target.id
        let newList = receiptList.splice (index, 1)
        setList(newList)
    }

    function handleRClick(e) {
        handleShow();
        setChange(true);
        setId(e.target.id)
        let newReceipt = JSON.parse(JSON.stringify(receiptList.at(e.target.id)));
        setCurrReceipt(newReceipt);
        let list = newReceipt.positionList;
        changeInputFields(list)
    }

    return (
        <>
            {show && <ReceiptModal state={show} receipt={currReceipt} setCurrReceipt = {setCurrReceipt} changeMode={onchange} inputFields={inputFields}
                                handle={handleClose} changeCategories={setCategories} confirm={confirm} categories ={categories}  setCategories = {setCategories}  changeInputFields = {changeInputFields} />}
            <div className="table-responsive">
                <Table className="border-1" bordered border={2} striped hover>
                    <thead>
                    <tr>

                        <th>Чек</th>
                        <th>Дата</th>
                        {account && <th>Счет</th>}
                        {!account && <th> Добавивший пользователь </th>}
                        <th> Полная цена чека</th>
                        <th> Цена чека с процентами</th>
                        <th>Изменить</th>
                        <th>Просмотреть</th>
                        <th> Удалить</th>
                    </tr>
                    </thead>
                    <tbody>
                    {/* <tr>
                        <td>
                            <input placeholder="Начинайте вводить" className="m-2" disabled={!filter}/>
                        </td>
                        <td>
                            <input className="m-2" type="date" placeholder="Начало промежутка" disabled={!filter}/>
                            <input className="m-2" type="date" placeholder="Конец промежутка" disabled={!filter}/>
                        </td>
                        <td>
                            {account ? <input className="m-2" placeholder=""/> :
                                <input className="m-2" placeholder="Начинайте вводить" disabled={!filter}/>}
                        </td>
                        <td>
                            <Button disabled={!filter}> Сортировка</Button>
                        </td>
                        <td>
                            <input className="m-1" type="checkbox" onChange={handleFilter}/>
                            Включить фильтры
                        </td>
                    </tr>
                    */}
                    {receiptList.map((item, index) => (
                        <>
                            <tr key={index}>
                                <td>Чек "{item.shop.name}"</td>
                                <td>{parseDate(new Date(item.date))}</td>
                                {account && <td>{item.account.account.name}</td>}
                                {!account &&
                                    <td> {item.account.user.identity.name + " " + item.account.user.identity.surname} </td>}
                                <td> {item.total}</td>
                                {<td>{parseFloat(item.total) * parseFloat(weight[item.account.account.id] || 1)}</td>}
                                {grantedAccess(item) ?
                                    <td>
                                        <Button id={index} onClick={handleRClick}>Изменить чек</Button>
                                    </td>
                                    : <td></td>
                                }

                                <td>
                                    <Button  id={index} onClick={handleClick}> Просмотреть чек </Button>
                                </td>

                                {grantedAccess(item) ?
                                    <td key={index}>
                                        <Button  id={index} onClick={deleteCheck}>Удалить чек</Button>
                                    </td>
                                    : <td></td>
                                }


                            </tr>
                        </>
                    ))}
                    <tr>
                        <td> <Button variant="primary" className="h-100">
                            <NavLink className="w-100 h-100" to={`./categories`}>
                                <text  className="text-white">
                                    Перейти к аналитике по покупкам и категориям
                                </text>
                            </NavLink>
                        </Button></td>
                    </tr>
                    </tbody>
                </Table>
            </div>
        </>
    )
}


export default ReceiptTable