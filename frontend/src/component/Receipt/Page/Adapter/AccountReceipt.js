import {useEffect, useState} from "react";
import ReceiptTable from "../ReceiptTable";


const AccountReceipt = ({data, changeData, setCategories , categories})=>{

    const [receiptList, setReceiptList] = useState(data.receiptList);
    const [access, setAccess] = useState({})
    const [weight, setWeight] = useState({})

    useEffect(() => {
        setReceiptList(data.receiptList)
        const updatedAccess = { ...access };
        const updatedWeight = { ...weight };

        updatedAccess[data.accountUser.account.id] = data.accountUser.permission;
        updatedWeight[data.accountUser.account.id] = data.accountUser.weight;
        setAccess(updatedAccess)
        setWeight(updatedWeight)

    }, [data]);

    const setList = (list) =>{
        let newData = data
        newData.receiptList = list
        setReceiptList(list)
        changeData (newData)
    }

    return (
        <ReceiptTable receiptList = {receiptList} setList = {setList} categories = {categories } setCategories = {setCategories} userid = {data.accountUser.user.id} permission = {access}  weight={weight} />
    )
}

export default AccountReceipt