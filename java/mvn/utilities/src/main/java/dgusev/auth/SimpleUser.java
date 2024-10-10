package dgusev.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Класс реализует хранение и доступ к имени и паролю пользователя. Из соображений безопасности пароль хранится
 * как массив типа char[] (не строка - String).
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 06.06.2008)
 */

@ToString
public class SimpleUser implements Serializable {

    static final long serialVersionUID = 2758427109481257255L;

    @Getter @Setter private String login;
    @Getter @Setter private String password;

    public SimpleUser() {
        this.login = null;
        this.password = null;
    }

    public SimpleUser(String login, String password) {
        this.login    = login;
        this.password = password;
    }

    /**
     * Проверка - не является ли данный объект пустым. Объект признается пустым если пусто хотя бы одно поле - логин
     * или пароль. Пусто - это значит имеет значение null или пустую строку ("").
     *
     * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст объект или нет.
     */
    public boolean isEmpty() {
        return (StringUtils.isBlank(this.login) || StringUtils.isBlank(this.password));
    }

}