package com.celonis.weather;

import com.celonis.weather.repository.IForecastDAO;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = WeatherApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
@Transactional
public class ForecastApiIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    IForecastDAO forecastDAO;

    @Test
    public void testSave_ExistingCity_status201() throws Exception {
        LocalDate date = LocalDate.now();
        mvc.perform(post(String.format("/weather/forecast/save/Madrid?date=%s", date.toString()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //long deleted = forecastDAO.deleteByName("Madrid");
        //Assert.assertEquals(2, deleted);
    }

    @Test
    public void testSave_NotExistingCity_status400() throws Exception {
        mvc.perform(post("/weather/forecast/save/Disneyland")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFetch_ExistingCity_status200() throws Exception {
        mvc.perform(post("/weather/forecast/save/Madrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        LocalDate now = LocalDate.now();

        mvc.perform(get("/weather/forecast/fetch/Madrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Madrid")))
                .andExpect(jsonPath("$[0].date", Matchers.is(now.toString())))
                .andExpect(jsonPath("$[1].name", Matchers.is("Madrid")))
                .andExpect(jsonPath("$[1].date", Matchers.is(now.plusDays(1).toString())));
    }

    @Test
    public void testFetch_NotSaveCity_status404() throws Exception {
        mvc.perform(get("/weather/forecast/fetch/Madrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
