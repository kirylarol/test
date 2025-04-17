import React, {useContext, useState} from "react"
import {instance} from "../../axios/axiosConfig";
import data from "bootstrap/js/src/dom/data";
import * as events from "events";
import {useNavigate} from "react-router-dom";


const Auth = (props)=> {

    let [authMode, setAuthMode] = useState("signin")

    let [errors, setErrors] = useState("");

    const changeAuthMode = () => {
        setAuthMode(authMode === "signin" ? "signup" : "signin")
    }
    let navigate = useNavigate();

    let login = (e)=> {
        e.preventDefault();

        const formData = new FormData(e.target);
        const data = {};
        formData.forEach((value, key) => {
            data[key] = value;
        });
        instance.post('/login', data)
            .then((response) => {
                if (response && response.data && response.data.message) {
                    setErrors(response.data.message);
                } else {
                    props.updateHeaderState(response.data.username);
                    setErrors("");
                    navigate("/")

                }
            }).catch(
                e => {
                    setErrors(e.value)
                }
        )
    }

    let register = (e)=>{
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = {};
        data["identityDTO"] = {};
        data["userDTO"] = {};
        formData.forEach( (value, key) => {
           if (key === "surname" || key === "name"){
               data["identityDTO"][key] = value;
           }else{
               data["userDTO"][key] = value;
           }

        });
        instance.post('/register',data) .then((response) => {
            if (response && response.data && response.data.message) {
                setErrors(response.data.message);
            } else {
                navigate("/")
            }
        })
    }


    if (authMode === "signin") {
        return (
            <div className="Auth-form-container" >
                <form className="Auth-form" id ="login" onSubmit={login}>
                    <div className="Auth-form-content">
                        <h3 className="Auth-form-title">Войти</h3>
                        {errors !=="" && <div>
                            {errors}
                        </div>
                        }
                        <div className="text-center">
                            Еще не зарегистрированы?{" "}
                            <span className="link-primary" onClick={changeAuthMode}>
                Регистрация
              </span>
                        </div>
                        <div className="form-group mt-3">
                            <label>Имя пользователя</label>
                            <input
                                name="login"
                                type="text"
                                className="form-control mt-1"
                                placeholder="Введите login"
                            />
                        </div>
                        <div className="form-group mt-3">
                            <label>Пароль</label>
                            <input
                                name = "password"
                                type="password"
                                className="form-control mt-1"
                                placeholder="Введите пароль"
                            />
                        </div>
                        <div className="d-grid gap-2 mt-3">
                            <button type="submit" className="btn btn-primary">
                                Войти
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        )
    }

    return (
        <div className="Auth-form-container">
            <form className="Auth-form" id = "register" onSubmit={register}>
                <div className="Auth-form-content">
                    <h3 className="Auth-form-title">Регистрация</h3>
                    <div className="text-center">
                        <span className="link-primary" onClick={changeAuthMode}>
              Войти
            </span>
                    </div>
                    {errors !=="" && <div>
                        {errors}
                    </div>
                    }
                    <div className="form-group mt-3">
                        <label>Фамилия</label>
                        <input
                            name="surname"
                            type="text"
                            className="form-control mt-1"
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Имя</label>
                        <input
                            name="name"
                            type="text"
                            className="form-control mt-1"
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Имя пользователя</label>
                        <input
                            name="login"
                            type="text"
                            className="form-control mt-1"
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Пароль</label>
                        <input
                            name = "password"
                            type="password"
                            className="form-control mt-1"
                        />
                    </div>
                    <div className="d-grid gap-2 mt-3">
                        <button type="submit" className="btn btn-primary">
                            Зарегистрироваться
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default Auth