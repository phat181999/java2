package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = createExampleCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system.
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void testCreateCar() throws Exception {
        Car car = createExampleCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(car.getId()))
                .andExpect(jsonPath("$.details.model").value(car.getDetails().getModel()));
        verify(carService, times(1)).save(any());
    }

    /**
     * Tests for successful updating of a car in the system.
     *
     * @throws Exception when car updating fails in the system
     */
    @Test
    public void testUpdateCar() throws Exception {
        Car car = createExampleCar();
        car.getDetails().setModel("Updated Model");

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/cars/" + car.getId())
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.model").value("Updated Model"));
        verify(carService, times(1)).save(any());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void testListCars() throws Exception {
        mvc.perform(
                get("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.carList[0].id").value(1L));
        verify(carService, times(1)).list();
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void testFindCarById() throws Exception {
        mvc.perform(
                get("/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.details.model").value("Impala"));
        verify(carService, times(1)).findById(1L);
    }

    /**
     * Tests the read operation for a non-existing car by ID.
     *
     * @throws Exception if the read operation for a non-existing car fails
     */
    @Test
    public void testFindCarByIdNotFound() throws Exception {
        mvc.perform(
                get("/cars/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(carService, times(1)).findById(2L);
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void testDeleteCarById() throws Exception {
        mvc.perform(
                delete("/cars/1"))
                .andExpect(status().isNoContent());
        verify(carService, times(1)).delete(1L);
    }

    /**
     * Tests the deletion of a non-existing car by ID.
     *
     * @throws Exception if the delete operation of a non-existing vehicle fails
     */
    @Test
    public void testDeleteCarByIdNotFound() throws Exception {
        mvc.perform(
                delete("/cars/2"))
                .andExpect(status().isNotFound());
        verify(carService, times(0)).delete(2L);
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car createExampleCar() {
        Car car = new Car();
        car.setId(1L);
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}
