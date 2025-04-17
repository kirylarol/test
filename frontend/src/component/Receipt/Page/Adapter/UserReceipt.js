import {useEffect, useState} from "react";
import ReceiptTable from "../ReceiptTable";
import {instance} from "../../../../axios/axiosConfig";


const UserReceipt = ({data, changeData, setCategories , categories})=>{
    const [receiptList, setReceiptList] = useState(data);
    const [access, setAccess] = useState({})
    const [weight, setWeight] = useState({})
    const [isReady , setReady] = useState(false)
    const [id, setId] = useState(0)

    useEffect(() => {
        setReceiptList(data)
    }, [data]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await instance.get("/user/accountinfo");
                const data = response.data;
                let newAccess = { ...access };
                let newWeight = { ...weight };

                if (Array.isArray(data)) {
                    data.forEach((item) => {
                        setId(item.user.id);
                        newAccess[item.account.id] = item.permission;
                        newWeight[item.account.id] = item.weight;
                    });
                    setAccess(newAccess);
                    setWeight(newWeight);
                    setReady(true);
                    console.log(response.data);
                } else {
                }
            } catch (e) {
            }
        };
        fetchData();
    }, []);


    const setList = (list) =>{
        changeData (list)
    }

    return (
        <>
            {isReady && <ReceiptTable receiptList = {receiptList} account={true} setList = {setList} categories = {categories } setCategories = {setCategories} userid = {id} permission = {access}  weight={weight} /> }
        </>
    )
}

export default UserReceipt