import {useLocation, useParams} from 'react-router-dom';
import {useEffect, useState} from "react";
import {instance} from "../../../axios/axiosConfig";
import AccountReceipt from "./Adapter/AccountReceipt";
import UserReceipt from "./Adapter/UserReceipt";
import {EmptyFullAccount, EmptyReceipt} from "../../../constants/Constants";



const ReceiptPage = ()=>{
    const location = useLocation();
    const [data, setData] = useState(EmptyFullAccount);
    const [categories, setCategories] = useState([])
    const [isReady, setReady] = useState(false)

    const changeData = (newData) =>{
        setData(newData)
    }
    const [result, setResult] = useState();

    const fetchData = async () => {
        try {
            const response1 = await instance.get(location.pathname);
            setData(response1.data);
            const response2 = await instance.get("categories/all");

            setCategories(response2.data);
            setReady(true)
        } catch (error) {
            console.error("Ошибка при загрузке данных", error);
        }
    };






    useEffect( () => {
        fetchData();
    }, []);

    if (location.pathname.includes("account")){
        return (
            <>
            {isReady && <AccountReceipt data = {data} changeData = {changeData} categories = {categories} setCategories ={setCategories} ></AccountReceipt> }
            </>
        )
    }else{
        return (
            <>
                {isReady && <UserReceipt data = {data} changeData = {changeData} categories = {categories} setCategories ={setCategories} ></UserReceipt> }
            </>
        )
    }
}

export default ReceiptPage;