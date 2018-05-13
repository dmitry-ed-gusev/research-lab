Project general information:
  У нас (Larga) есть договор с Балтийским Текстильным Комбинатом на внедрение платформы BFG SOFT, которая заточена
  под оптимизацию производственной системы и оперативное управление предприятием.

1. Development portal. Obsolete, 13.05.2018
  https://redmine.luxms.com/login
  user: kulabukhov.sa@larga.ru
  password: skCsbi12

2. BFG System demo:
  https://saturn.bfg-group.ru/
  username: saturn
  password: Miepu4chah8o
  auth inside app: stolov/1

3. Project JIRA/Confluence/Stash:
  https://bfg-integral.atlassian.net/secure/RapidBoard.jspa?rapidView=3&projectKey=BTKTEX

4. Architecture has been drawn in: draw.io

5. LuxMS virtual PC:
    Образ виртуальной машины с платформой LuxmsBI доступен по адресу: http://releases.luxmsbi.com/luxmsbi_3.0.177.ova
    login / password: bi/bi

    Образ настроен на работу с сетью с использованием протокола DHCP.
    Образ необходимо развернуть на платформе виртуализации VMWare.

    После развертывания VM необходимо подключиться к серверу по протоколу SSH (порт 22)
    login / password: bi/bi

    Затем следует создать пользователя - администратора системы с помощью скрипта: /opt/luxmsbi/bin/create_admin
    [bi@luxmsbi   ~]$ cd /opt/luxmsbi/bin/
    [bi@luxmsbi bin]$ ./create_admin
    Please specify new Luxms BI Administrator account.
    Enter login (email): <ввести имя пользователя>
    После этого, с помощью заданного логина и пароля можно авторизоваться в системе.

    WEB интерфейс LuxmsBI с комплектом демонстрационных наборов данных доступен по ссылке: http://<ip-адрес-виртуальной-машины>/
    WEB интерфейс администратора доступен по ссылке: http://<ip-адрес-виртуальной-машины>/admin
    Документация доступна по адресу http://<ip-адрес-виртуальной-машины>/docs/ после авторизации в WEB интерфейсе администратора

    Для отображения карт при работе в LuxmsBI используется ресурс в интернете http://*.tile.openstreetmap.org/
    Использование этого ресурса в LuxmsBI возможно в двух вариантах:
    - прямое обращение к ресурсу с картой из браузеров пользователей, для этого необходимо обеспечить
        доступность этого ресурса с рабочих мест пользователей
    - проксирование запросов за картой через виртуальную машину LuxmsBI; для этого необходимо открыть
        доступ к ресурсу http://*.tile.openstreetmap.org/ только с виртуальной машины LuxmsBI и выполнить
        настройку платформы силами инженеров обеспечивающих поддержку платформы LuxmsBI

    12.05.2018
        Created admin user: admin/admin
        Created simple user: user/user

6. Keys for VMWare:
    FG3TU-DDX1M-084CY-MFYQX-QC0RD <- this one is used and OK (10.05.2018)
    YZ71K-A0Y1P-48EGY-TQWEC-M7AY0
    FV14H-DCG9Q-H84LY-XMXZT-Z3UY8
    FF5D2-0RX46-H88KP-7YP7E-Z7KTD
    AZ71U-FZX5P-H81ZP-Q4XQX-XVR9A
    ZY59H-87W0Q-H84EZ-87MXC-X7HC4