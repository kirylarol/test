import Tab from 'react-bootstrap/Tab';
import {Col, Row} from "react-bootstrap";
import Nav from "react-bootstrap/Nav";
import {UserPage} from "./UserPage";
import {AdminUsers} from "./AdminParts/AdminUsers";
import {AdminAccounts} from "./AdminParts/AdminAccounts";

export const AdminPage = () =>{

    return(
        <Tab.Container>
            <Row className="flex-grow-1 m-0 p-0">
                <Col sm={2} className="d-flex flex-column justify-content-center">
                    <Nav variant="pills" className="flex-column align-content-center bg-body-secondary rounded-5 pt-3 pb-3" style={{  maxHeight: '100%' }}>
                        <Nav.Item className="m-3 w-75">
                            <Nav.Link eventKey="profile">Мой профиль</Nav.Link>
                        </Nav.Item>
                        <Nav.Item className="m-3 w-75">
                            <Nav.Link eventKey="users">Пользователи</Nav.Link>
                        </Nav.Item>
                        <Nav.Item className="m-3 w-75">
                            <Nav.Link eventKey="accounts">Счета</Nav.Link>
                        </Nav.Item>
                    </Nav>
                </Col>
                <Col sm={10} className="h-100">
                    <Tab.Content>
                        <Tab.Pane eventKey = "profile"><UserPage/></Tab.Pane>
                    </Tab.Content>
                    <Tab.Content>
                        <Tab.Pane eventKey = "users"> <AdminUsers/>  </Tab.Pane>
                    </Tab.Content>
                    <Tab.Content>
                        <Tab.Pane eventKey = "accounts"> <AdminAccounts/> </Tab.Pane>
                    </Tab.Content>
                </Col>
            </Row>
        </Tab.Container>
    )
}