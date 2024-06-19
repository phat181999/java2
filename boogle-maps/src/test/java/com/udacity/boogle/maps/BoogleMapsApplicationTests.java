package com.udacity.boogle.maps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(MapsController.class)
public class BoogleMapsApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void contextLoads() {    }

    @Test
    public void getAdress() throws Exception {

        mockMvc.perform(get("/maps?lat=40.730610&lon=-73.935242"))
                .andExpect(status().isOk());

    }

}
