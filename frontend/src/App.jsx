import './App.css';
import Header from './component/header/Header'
import 'bootstrap/dist/css/bootstrap.min.css';
import {Navigate, Route, Routes} from "react-router-dom";
import Home from './component/home/Home'
import Auth from './component/auth/auth'
import {useEffect, useState} from "react";
import Accounts from "./component/Accounts/Accounts";
import AccountForm from "./component/Accounts/AccountForm";
import AccountPage from "./component/Accounts/AccountPage";
import ReceiptPage from "./component/Receipt/Page/ReceiptPage";
import NewModal from "./component/Receipt/New/NewModal";
import {CategoryAnalytics} from "./component/Receipt/ByCategory/CategoryAnalytics";
import {UserPage} from "./component/UserPage/UserPage";
import {AdminPage} from "./component/UserPage/AdminPage";
import {instance} from "./axios/axiosConfig";

function App() {

    const [adminLevel, setAdmin] = useState(false);
    const [headerData, setHeaderData] = useState(localStorage.getItem("username"));

    const checkLogin = () => {
        const login = window.localStorage.getItem("token");
        return login !== null;
    };

        const isAdmin = async () => {
            try {
                const response = await instance.get("/admin");
                return response.data;
            } catch (error) {
                return false;
            }
        };


    const isItAdmin = () => {
        return adminLevel
    }

        const fetchAdminStatus = async () => {
            const adminStatus = await isAdmin();
            setAdmin(adminStatus);
        };


    const updateHeaderState = (newHeaderData) => {
        fetchAdminStatus()
        setHeaderData(newHeaderData);
    }


    return (
        <div className="App">
            <Header headerData={headerData} updateData={updateHeaderState}/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/auth" element={<Auth updateHeaderState={updateHeaderState}/>}/>
                <Route path="/accounts" element={checkLogin() ? <Accounts/> : <Navigate to="/auth"/>}/>
                <Route path="/account/new" element={checkLogin() ? <AccountForm/> : <Navigate to="/auth"/>}/>
                <Route path="/account/:id" element={checkLogin() ? <AccountPage/> : <Navigate to="/auth"/>}/>
                <Route path="/account/:id/receipts" element={checkLogin() ? <ReceiptPage/> : <Navigate to="/auth"/>}/>
                <Route path="/account/:id/receipt/new" element={checkLogin() ? <NewModal/> : <Navigate to="/auth"/>}/>
                <Route path="/user/receipts" element={checkLogin() ? <ReceiptPage/> : <Navigate to="/auth"/>}/>
                <Route path="/account/:id/receipts/categories"
                       element={checkLogin() ? <CategoryAnalytics/> : <Navigate to="/auth"/>}/>
                <Route path="/profile"
                       element={checkLogin() && !isItAdmin() ? <UserPage/> : isItAdmin() ? <Navigate to="/admin"/> :
                           <Navigate to="/login"/>}/>
                <Route path="/admin" element={isItAdmin() ? <AdminPage/> : <Navigate to="/"/> }/>
            </Routes>
        </div>
    );
}

export default App;
