package dgusev.auth;

import java.io.Serializable;

/**
 * Password storage class (from ancient time :)).
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
 */

public class Password implements Serializable {

    private final static int XOR_MODULE = 13; // base module vbalue for XOR function

    /**
     * Непосредственно сохраняемый пароль. Хранится в виде байтового массива, который получен путем побайтового
     * применения операции XOR к исходному массиву (String или char[]).
     */
    private byte[] bytePassword = null;

    /**
     * Конструкторы класса.
     *
     * @param password String значение пароля для хранения в данном классе.
     * @throws EmptyPassException ИС - при создании экземпляра класса указано пустое значение для пароля.
     */
    public Password(String password) throws EmptyPassException {
        // Работаем, только если указанный пароль не пуст
        if (!StringUtils.isBlank(password)) {
            byte[] foreignPass = password.getBytes();
            bytePassword = new byte[foreignPass.length];
            // Побайтно ксорим (XOR) исходный пароль по нашему модулю и полученные значения сохраняем
            for (int i = 0; i < foreignPass.length; i++) {
                bytePassword[i] = (byte) (foreignPass[i] ^ XOR_MODULE);
            }
        }
        // Если указан пустой пароль - ошибка (генерируем ИС)
        else {
            throw new EmptyPassException("Password cannot be empty!");
        }
    }

    /**
     * Метод получения пароля.
     *
     * @return String хранящийся в данном классе пароль.
     */
    public String getPassword() {
        byte[] byteResult = new byte[bytePassword.length];
        // Для превращения заксоренного пароля в обычный вид - снова ксорим его по нашему модулю
        for (int i = 0; i < bytePassword.length; i++) {
            byteResult[i] = (byte) (bytePassword[i] ^ XOR_MODULE);
        }
        return new String(byteResult);
    }

    /**
     * Метод установки пароля для хранения.
     *
     * @param password String значение пароля для хранения в данном классе.
     * @throws EmptyPassException ИС - для установки пароля указано пустое значение.
     */
    public void setPassword(String password) throws EmptyPassException {
        // Работаем, только если указанный пароль не пуст
        if (!StringUtils.isBlank(password)) {
            byte[] foreignPass = password.getBytes();
            bytePassword = new byte[foreignPass.length];
            // Побайтно ксорим (XOR) исходный пароль по нашему модулю и полученные значения сохраняем
            for (int i = 0; i < foreignPass.length; i++) {
                bytePassword[i] = (byte) (foreignPass[i] ^ XOR_MODULE);
            }
        }
        // Если указан пустой пароль - ошибка (генерируем ИС)
        else {
            throw new EmptyPassException("Password cannot be empty!");
        }
    }

    @Override
    public String toString() {
        return ("PASSWORD [" + this.getPassword() + "]");
    }

}