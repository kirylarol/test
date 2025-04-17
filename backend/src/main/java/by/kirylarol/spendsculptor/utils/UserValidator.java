package by.kirylarol.spendsculptor.utils;

import by.kirylarol.spendsculptor.service.UserService;
import by.kirylarol.spendsculptor.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
        private final UserService userService;

        @Autowired
        public UserValidator(UserService userService) {
            this.userService = userService;
        }

        @Override
        public boolean supports(Class<?> aClass) {
            return User.class.equals(aClass);
        }

        @Override
        public void validate(Object o, Errors errors) {
            User user = (User) o;
            try {
               if (userService.getUser(user.getLogin()) == null){
                   return;
               };
            } catch (UsernameNotFoundException ignored) {
                return;
            }

            errors.rejectValue("username", "", "Человек с таким именем пользователя уже существует");
        }
    }
    
