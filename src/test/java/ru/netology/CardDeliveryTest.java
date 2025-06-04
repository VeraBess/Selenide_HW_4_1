package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setup() {

        Selenide.open("http://localhost:9999");
    }

    private String generateDate(long addDays, String pattern) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Test
    public void sendigFormValidData() {
        $("[data-test-id='city'] input").setValue("Москва");
        String planningDate = generateDate(4, "dd.MM.yyyy");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван Иванович");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $(".notification__content")
                .should(visible, Duration.ofSeconds(15))
                .should(text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    public void sendigFormValidDataSelectDateInCalendarAndSity() { //выбор даты из календаря и города из списка
        $("[data-test-id='city'] input").setValue("Мо");
        $(".input__menu").find(byText("Москва")).should(visible, Duration.ofSeconds(15)).click();

        $("button.icon-button").click(); // Нажатие на иконку календаря
        LocalDate planningDateLocal = LocalDate.now().plusDays(30); //добавляем к текущей дате неделю
        String planningDate = planningDateLocal.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); //кладем в переменную planningDate и приводим к нужному формату
        int necessaryMonth = planningDateLocal.getMonthValue(); //определяем нужный месяц из планируемой даты и кладем в переменную
        int currentMonth = extractCurrentMonthFromCalendar(); //определяем текущий месяц отображаемый на экране и кладем в переменную (вспомогательный метод)
        navigateToCorrectMonth(currentMonth, necessaryMonth); //переключаем календарь на нужный (планируемы) месяц если он не совпадает с текущим, с помощью вспомогательного метоад
        String dayForClick = planningDateLocal.format(DateTimeFormatter.ofPattern("d")); //форматируем день полученный из даты в строку
        $$(".calendar__day").filterBy(Condition.text(dayForClick)).first().click(); //нажимаем на выбранный день

        $("[data-test-id='name'] input").setValue("Иванов Иван Иванович");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $(".notification__content")
                .should(visible, Duration.ofSeconds(15))
                .should(text("Встреча успешно забронирована на " + planningDate));
    }

    //метод возвращающий текущий месяц отображаемый в данный момент на экране в календаре
    private int extractCurrentMonthFromCalendar() {
        String fullName = $(".calendar__name").getText().trim(); //В fullName кладем месяц+год
        String[] parts = fullName.split("\\s+"); // разбиение на части (месяц и год) и кладем в массив
        String monthNameRu = parts[0]; //извлекаем имя месяца из массива и кладем в monthName, но месяц на русском
        switch (monthNameRu) {  // Таблица соответствия русских названий месяцев с числами, что бы избежать русских названий присваиваем им цифры
            case "Январь":
                return 1;
            case "Февраль":
                return 2;
            case "Март":
                return 3;
            case "Апрель":
                return 4;
            case "Май":
                return 5;
            case "Июнь":
                return 6;
            case "Июль":
                return 7;
            case "Август":
                return 8;
            case "Сентябрь":
                return 9;
            case "Октябрь":
                return 10;
            case "Ноябрь":
                return 11;
            case "Декабрь":
                return 12;
            default:
                throw new IllegalStateException("Недопустимое имя месяца: " + monthNameRu);
        }
    }

    //метод для поиска нужного месяца. Кликаем по стрелке вправо пока не найдем нужный месяц. В currentMonth кладем отображаемый на экране месяц
    private void navigateToCorrectMonth(int currentMonth, int nesseseryMonth) {
        while (currentMonth != nesseseryMonth) {
            $$(".calendar__arrow.calendar__arrow_direction_right").last().click();
            currentMonth = extractCurrentMonthFromCalendar();
        }
    }
}
