package ru.netology;

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
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);  //очищаем поле календаря
        $("button.icon-button").click(); // Нажатие на иконку календаря
        LocalDate planningDateLocal = LocalDate.now().plusDays(7); //добавляем к текущей дате неделю
        String planningDate = planningDateLocal.format(DateTimeFormatter
                .ofPattern("dd.MM.yyyy")); //кладем в переменную planningDate и приводим к нужному формату
        String dayForClick = planningDateLocal.getDayOfMonth() + ""; //определяем только день месяца
        $$(".calendar__day").filter(visible)
                .find(text(dayForClick)).click(); //кликаем по найденному дню
        $("[data-test-id='name'] input").setValue("Иванов Иван Иванович");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $(".notification__content")
                .should(visible, Duration.ofSeconds(15))
                .should(text("Встреча успешно забронирована на " + planningDate));
    }
}
