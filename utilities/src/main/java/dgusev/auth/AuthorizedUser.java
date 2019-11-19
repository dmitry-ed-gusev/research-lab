package dgusev.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Класс авторизованного пользователя. Содержит ФИО пользователя из БД "Кадры", идентификатор
 * пользователя из БД "Кадры" и код (строку) ошибки (используется, если не удалось получить
 * ФИО и идентификатор).
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 08.10.2008)
 */

@ToString
public class AuthorizedUser implements Serializable {
    static final long serialVersionUID = 4720090046144350344L;

    @Getter @Setter private String     fullName;
    @Getter @Setter private int        personnelID;
    @Getter @Setter private String     errorCode;
    @Getter @Setter private SimpleUser simpleUser;

    public AuthorizedUser() {
        this.fullName    = null;
        this.personnelID = -1;
        this.errorCode   = null;
        this.simpleUser  = null;
    }

    public AuthorizedUser(String fullName, int personnelID, String errorCode, SimpleUser simpleUser) {
        this.fullName    = fullName;
        this.personnelID = personnelID;
        this.errorCode   = errorCode;
        this.simpleUser  = simpleUser;
    }

    /**
     * Проверка, не является ли данный объект пустым. Пустым данный объект признается, если пусто хотя бы одно
     * значимое поле - кроме поля errorCode, т.е. если поле errorCode заполнено, а остальные поля пусты - объект
     * считается пустым.
     *
     * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст данный объект или нет.
     */
    public boolean isEmpty() {
        return (StringUtils.isBlank(this.fullName) || (this.personnelID <= 0) ||
                (this.simpleUser == null) || (this.simpleUser.isEmpty()));
    }

}