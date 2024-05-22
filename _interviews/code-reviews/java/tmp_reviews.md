/*
Компонент проведения платежей
Платеж - это сумма в валюте, которая переводится от одного клиента другому
Сумма платежа при сохранении должна быть пересчитана в рубли по курсу ЦБ на текущую дату
При платеже также должна быть выставлена комиссия, которая расчитывается в зависимости от суммы платежа
После платежа надо вызвать сервис нотификаций, который прокинет нотификации пользователям - для клиентов это будет выглядеть как push уведомление в итоге.
Компонент переводит деньги от залогиненного пользователя переданному на вход
 */
@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRestClient notificationRestClient;
    @Autowired
    private CbrRestClient cbrRestClient;
    @Transactional
    public void processPayment(double amount, Currency currency, Long recipientId) {
        double amountInRub = amount * cbrRestClient.doRequest().getRates().get(currency.getCode());
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findUserById(userId);
        Payment payment = new Payment(amountInRub, user, recipientId);
        paymentRepository.save(payment);
        if (amountInRub < 1000) {
            Fee fee = new Fee(amountInRub * 0.015, user);
            feeRepository.save(fee);
        }
        if (amountInRub > 1000) {
            Fee fee = new Fee(amountInRub * 0.01, user);
            feeRepository.save(fee);
        }
        if (amountInRub > 5000) {
            Fee fee = new Fee(amountInRub * 0.005, user);
            feeRepository.save(fee);
        }
        try {
            notificationRestClient.notify(payment);
        } catch (Exception e) {
            // do nothing
        }
    }
}

расшифровка:
1. внедрение зависимостей через @Autowired не следует использовать
2. слишком много зависимостей в одном классе
3. использование double вместо BigDecimal
4. нет проверки на null для входных параметров в методе processPayment
5. getPrincipal() может не суметь привести к Long, нужна проверка на instance of
6. cbrRestClient.doRequest().getRates().get(currency.getCode()) можем упасть с NPE
7. нет гарантии получения результата от cbrRestClient.doRequest() и эта ситуация никак не обработана
8. спагетти-код из последовательности условных операторов: можно переписать на паттерн-стратегия (если условия не будут меняться), либо реализовать таблицу в бд, в которой можно в рантайме устанавливать нужные условия и считывать их из метода
9. использование http-соединений в транзакционном методе недопустимо: даже в случае корректного ответа от удаленного сервера, кол-во одновременно открытых сессий коннекта к базе будет несоразмерно больше, что будет аффектить на производительность
10. нарушение принципа единой ответственности: метод processPayment и рассчитывает комиссию, и ходит за значениями к разным клиентам, констрирует и сохраняет сразу две сущности базы



-------------------------------------------------------------

поток1 вызывает метод1, поток2 вызывает метод2 - что выведется в консоль первым?

public class SynchronizedTask {
    private static final Long SLEEP_BEFORE_MILLS = 3_000L;
    private static final Long SLEEP_AFTER_MILLS = 1_000L;

    public synchronized void method1() throws InterruptedException {
        Thread.sleep(SLEEP_BEFORE_MILLS);
        System.out.println("This is amazing!");
        Thread.sleep(SLEEP_AFTER_MILLS);
    }

    public synchronized void method2() throws InterruptedException {
        Thread.sleep(SLEEP_BEFORE_MILLS);
        System.out.println("This is amazing too!");
        Thread.sleep(SLEEP_AFTER_MILLS);
    }
}

-----------------------------------------------------------

import java.util.Map;

public class HashMapTask {
    public static class Id {
        private String id;
        private Integer number;

        /// fields, setters, getters, constructors, other methods
    }

    public static class User {
        private String name;

        /// fields, setters, getters, constructors, other methods
        public User getUser(Map<Id, User> map) {
            Id id = new Id(); // "id" object is not null
            User user = new User(); // "user" object is not null
            map.put(id, user);
            return map.get(id); // result == null - possible?
        }
    }
}

-----------------------------------------------------------

for (Integer i = 0; i < grades3.length; i++) {
            sum5CS003 += grades3[i];
}

вот тут че не так, кроме именования переменных?)
каждую итерацию - будет создан новый объект (инстанс) - оператором i++

-----------------------------------------------------------

public abstract class GradesCalculator {
    public abstract int[] getModuleGrades(String module);
    public void calc() {
        int[] grades1 = getModuleGrades("4CS001");
        int sum4CS001 = 0;
        for (int i = 0; i < grades1.length; i++) {
            sum4CS001 += grades1[i];
        }
        int averageGrade4CS001 = sum4CS001 / grades1.length;
        int[] grades2 = getModuleGrades("6CS002");
        int sum6CS002 = 0;
        for (int j : grades2) {
            sum6CS002 += j;
        }
        int averageGrade6CS002 = sum6CS002 / grades2.length;
        int[] grades3 = getModuleGrades("5CS003");
        int sum5CS003 = 0;
        for (Integer i = 0; i < grades3.length; i++) {
            sum5CS003 += grades3[i];
        }
        int averageGrade5CS003 = sum5CS003 / grades3.length;
        System.out.println("4CS001 Average: " + averageGrade4CS001);
        System.out.println("6CS002 Average: " + averageGrade6CS002);
        System.out.println("5CS003 Average: " + averageGrade5CS003);
    }
}

