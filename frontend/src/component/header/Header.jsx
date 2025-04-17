import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container"
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import {NavLink} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {instance} from "../../axios/axiosConfig";

const Header = ({headerData, updateData}) => {

    /*
        instance.post("/login", {
            login: 'johndoe124',
            password: 'password123'
        }).then(r => console.log(r));

     */

    const handleLogout= () => {
        localStorage.removeItem("username",null)
        localStorage.removeItem("token",null)
        updateData(null);
    }


    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container fluid>
                <Navbar.Brand href="/" style={{"color": 'gold'}}>
                    SpendSculptor
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="navbarScroll"/>
                <Navbar.Collapse id="navbarScroll">
                    <Nav
                        className="me-auto my-2 my-lg-0"
                        style={{maxHeight: '100px'}}
                        navbarScroll
                    >
                        <NavLink className="nav-link" to="/">Главная страница</NavLink>
                        <NavLink className="nav-link" to="/accounts">Мои счета</NavLink>
                        <NavLink className="nav-link" to="/">О нас</NavLink>

                    </Nav>
                    {headerData === null || headerData === undefined || headerData === "undefined" ?
                        <>
                            <Button variant="outline-info" className="me-2"> <NavLink className="nav-link"
                                                                                      to="/auth">Войти/Зарегистрироваться </NavLink>
                            </Button>
                        </>
                        : (
                            <>
                                <Button variant="outline-info" className="me-3"> <NavLink className="nav-link"
                                                                                          to="/profile">{headerData} </NavLink></Button>
                                <Button variant="outline-info" className="me-3" onClick={handleLogout}> <NavLink className="nav-link"
                                                                                          to="/">Выйти </NavLink></Button>

                            </>
                        )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default Header