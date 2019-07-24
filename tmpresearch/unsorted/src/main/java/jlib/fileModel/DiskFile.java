package jlib.fileModel;

import gusev.dmitry.utils.MyIOUtils;

import java.io.Serializable;

/**
 * Программная модель файла на жестком диске. Хранит имя файла с относительным путем к нему, а также CRC-код файла.
 * CRC-код в данном классе НЕ ВЫЧИСЛЯЕТСЯ! Также класс реализует метод сравнения двух экземпляров данного класса -
 * метод equals(), и метод проверки экземпляра класса на пустоту - если поля не заполнены, метод возвращает значение
 * ИСТИНА. При сравнении экземпляров класса они(экземпляры) считаются эквивалентными, если совпадают отнисительные пути
 * к ним и имена файлов - поля fileName каждого экземпляра. Это сделано, т.к. экземпляры будут использоваться для
 * создания файлов на реальных файловых системах(NTFS, FAT32), которые не допускают двух файлов в одном каталоге с
 * одинаковыми именами. <br>
 * ВНИМАНИЕ! Данный класс предназначен для внутреннего использования классом FileSystem для создания списков файлов.
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 19.11.2007)
 */

// todo: хранение имен файлов и каталогов лучше перевести в верхний регистр символов - для унификации сравнения

public class DiskFile implements Serializable {
    /**
     * Параметр нужен для совместимости новых версий класса с предыдущими при сериализации/десериализации.
     */
    static final long serialVersionUID = -8130377985046806721L;

    /**
     * Имя файла с относительным путем к файлу (относительно какого-либо каталога).
     */
    private String fileName;
    /**
     * Вычисленный код CRC для данного файла.
     */
    private long crcCode;

    /**
     * Конструктор по умолчанию.
     */
    public DiskFile() {
        this.fileName = null;
        this.crcCode = -1;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = MyIOUtils.fixFPath(fileName, false);
    }

    public long getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(long crcCode) {
        this.crcCode = crcCode;
    }

    /**
     * Данный метод сравнивает два объекта "файл на диске" (DiskFile) - текущий и переданный в качестве параметра. Если
     * объекты идентичны - метод возвращает значение ИСТИНА, если же нет - значение ЛОЖЬ. Идентичность объектов
     * устанавливается по содержимому полей, идентичности объекта самому себе, совпадению классов, которым принадлежат
     * объекты.
     * <p>
     * Тег @Override в описании метода указывает, что данный метод призван заместить(переопределить) соответствующий
     * метод суперкласса - Object. Метод супер класса определяет, ссылаются ли переменные на один и тот же объект. Если
     * же данный метод не переопределит метод суперкласса (напр., если его объявить так:
     * public boolean equals (DiskFile obj) ), то проверка из метода суперкласса выполнена не будет.
     * <p>
     * КОММЕНТАРИЙ К СТРОЧКЕ КОДА ***
     * Для определения, принадлежат ли оба объекта одному классу используется метод instanceof() - т.к. принцип
     * проверки идентичности будет верен и в подклассах (которых нет и не будет...). Если же семантика проверки
     * может измениться в подклассе (дочернем классе), то необходимо использовать метод getClass().
     * Если в подклассе данного класса будет переопределен метод equals() - в него (метод дочернего класса)
     * необходимо будет включить вызов данного метода - метода родительского класса - super.equals(otherObject).
     *
     * @param obj Object объект, с которым сравниваем текущий объект.
     * @return boolean результат сравнения текущего объекта с объектом obj.
     */
    @Override
    public boolean equals(Object obj) {
        // Результат сравнения экземпляров данного класса
        boolean result = false;
        // Быстрая проверка идентичности экземпляров
        if (this == obj) result = true;
            // Если быстрая проверка не прошла - проверяем далее - если явный параметр null или классы не совпадают
            // (данные экземпляры от разных классов) - возвращается значение false и проверки прекращаются. Если же это
            // экземпляры одного класса - приводим внешний объект к данному классу и проверяем соответствие имен полей.
            // ** вместо [this.getClass() == obj.getClass()] можно использовать [obj instanceof DiskFile]
        else if (obj != null && this.getClass() == obj.getClass()) {
            // Теперь мы знаем, что объект obj имеет тип DiskFile и не является нулевым
            DiskFile diskFile = (DiskFile) obj;
            // Сравниваем имена файлов и их контрольные суммы. Имена файлов сравниваем без учета регистра символов.
            if ((this.fileName.equalsIgnoreCase(diskFile.fileName)) && (this.crcCode == diskFile.crcCode)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (int) (crcCode ^ (crcCode >>> 32));
        return result;
    }

    /**
     * Метод возвращает значение ИСТИНА, если все поля данного экземпляра класса пусты (имеют значения по умолчанию).
     * Точнее, поле <имя файла> (fileName) должно быть непустым. Поле же crcCode вполне может содержать
     * значение по умолчанию.
     *
     * @return boolean значение ИСТИНА/ЛОЖЬ в зависимости от значений полей экземпляра класса.
     */
    public boolean isEmpty() {
        return ((fileName == null) || (fileName.trim().equals("")));
    }

    /**
     * Метод генерирует и возвращает строковое представление данного класса. Метод используется в основном для отладки.
     *
     * @return String строковое представление данного экземпляра класса.
     */
    @Override
    public String toString() {
        return "\n[file=" + fileName + ", crc=" + crcCode + "]";
    }

}