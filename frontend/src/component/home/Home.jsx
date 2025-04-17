import Card from 'react-bootstrap/Card';
import Header from "../header/Header";
import {CardGroup, Container} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import { NavLink} from "react-router-dom";
import {alignPropType} from "react-bootstrap/types";

const Home=  () => {

    const linkStyle = {
        color: 'white',
        width:'100%'
    };
    return(
        <Container style={{flexGrow : '1'}} className="d-flex justify-content-center align-items-center">
        <CardGroup aria-multiline={"true"} className="d-flex justify-content-center flex-wrap"  >
         <Card bg={"light"} className="m-2 w-100 border-2">
                <Card.Body>
                    <Card.Title>1) Зарегистрируйтесь в приложении</Card.Title>
                    <Button variant="primary">
                        <NavLink style={linkStyle} to="/">Зарегистрироваться</NavLink>
                    </Button>
                </Card.Body>
            </Card>
            <Card bg={"light"} className="m-2 w-100 border-2" >
                <Card.Body>
                    <Card.Title>2) Добавьте счет</Card.Title>
                    <Button variant="primary">
                        <NavLink style={linkStyle} to="/">Добавить счет</NavLink>
                    </Button>
                </Card.Body>
            </Card>

            <Card bg={"light"} className="m-2 w-100 border-2" >
                <Card.Body>
                    <Card.Title>3) Добавьте расходы</Card.Title>
                    <Button variant="primary" >
                        <NavLink style={linkStyle} to="/accounts">Перейти к расходам</NavLink>
                    </Button>
                </Card.Body>
            </Card>

            <Card bg={"light"}  className="m-2 w-100 border-2" >
                <Card.Body>
                    <Card.Title>4) Учитывайте финансы и экономьте деньги</Card.Title>
                    <Button variant="primary">
                        <NavLink style={linkStyle} to="/">Перейти к финансам</NavLink>
                    </Button>
                </Card.Body>
            </Card>
        </CardGroup>
        </Container>
    )

}


export default Home