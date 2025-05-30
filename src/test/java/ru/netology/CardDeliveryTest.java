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
    public void sendigFormValidDataSelectDateInCalendar() { //выбор даты из календаря
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);  //очищаем поле календаря
        $("button").click(); // Нажатие на иконку календаря
        $("[data-test-id='date'] input").should(visible, Duration.ofSeconds(15));
        String planningDate = LocalDate.now().plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));//добавляем к текущей дате неделю и кладем в переменную planningDate и приводим к нужному формату
        $$(".calendar__day").find(exactText("31")).click();
        $("[data-test-id='name'] input").setValue("Иванов Иван Иванович");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $("button.button").click();
        $(".notification__content")
                .should(visible, Duration.ofSeconds(15))
                .should(text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    public void sendigFormValidDataDropDownList() {  //выбор города из выпадающего списка
        $("[data-test-id='city'] input").setValue("Мо");
        $(".input__menu").find(byText("Москва")).should(visible, Duration.ofSeconds(15)).click();
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
}
