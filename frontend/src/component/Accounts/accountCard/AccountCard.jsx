import Card from "react-bootstrap/Card";
import {NavLink, useNavigate} from "react-router-dom";
import React, {useState} from "react";
import Button from "react-bootstrap/Button";
import { newAccountFakeObject } from "../../../constants/Constants"
import {instance} from "../../../axios/axiosConfig";

const AccountCard = (props)=>{
    let item;
    if (props === undefined) return;
    let url = 'account/' + props.item.id
    const leaveAccount =  (e) =>{
        instance.delete(url).then(
            r =>{
                props.fetchData();
            }
        );

    }


    if (props.item === newAccountFakeObject) {
        return (
            <Card className="m-2 border-2 mw-100 flex-grow-1" style={{minWidth: 200}}>
                <Card.Body>
                    <Card.Title>Создать новый счет</Card.Title>
                    <Button variant="primary " style={{maxWidth: 200}} className="m-2">
                        <NavLink className="nav-link" to={`/account/new`}>Создать новый счет</NavLink>
                    </Button>
                </Card.Body>
            </Card>
        )
    }
    return(
        <Card className="m-2 border-2 mw-100 flex-grow-1" style={{minWidth: 200}}>
            <Card.Body>
                <Card.Title>{props.item.name}</Card.Title>

                {props.item.dateCreated && <Card.Text>Создан {props.item.dateCreated}</Card.Text> }
                <Button variant="primary " style={{maxWidth: 200}} className="m-2">
                    <NavLink className="nav-link" to={`/account/${props.item.id}`}>Перейти в меню счета</NavLink>
                </Button>
                <Button variant="primary " style={{maxWidth: 200}} className="m-2">
                    <NavLink className="nav-link" to={`/account/${props.item.id}/receipt/new`}>Добавить чек</NavLink>
                </Button>
                <Button variant="primary" style={{maxWidth: 200}} onClick={leaveAccount}>
                    <NavLink className="nav-link" to={`/accounts/`}>Выйти из счета</NavLink>

                </Button>
            </Card.Body>
        </Card>
    )
}

export default AccountCard