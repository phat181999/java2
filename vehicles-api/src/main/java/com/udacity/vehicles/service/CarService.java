package com.udacity.vehicles.service;

import com.udacity.vehicles.VehiclesApiApplication;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.cxf.jaxws.spring.EndpointDefinitionParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;

    private final WebClient maps;
    private final WebClient pricing;
    private final ModelMapper modelMapper;

    private MapsClient mapsClient;
    private PriceClient priceClient;

    @Autowired
    public CarService(CarRepository repository, WebClient maps, WebClient pricing, ModelMapper modelMapper) {

        this.repository = repository;
        this.maps = maps;
        this.pricing = pricing;
        this.modelMapper = modelMapper;

        this.mapsClient = new MapsClient(maps, modelMapper);
        this.priceClient = new PriceClient(pricing);
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Car car;
        if (repository.findById(id).isPresent()) {
             Optional<Car> optionalCar = repository.findById(id);
            car = optionalCar.get();
            } else {
             throw new CarNotFoundException ("Car not found");
        }

        String price = this.priceClient.getPrice(id);
        car.setPrice(price);

        Location location = this.mapsClient.getAddress(car.getLocation());
        car.setLocation(location);
        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {

        Optional<Car> optionalCar;
        if (repository.findById(id).isPresent()){
            optionalCar = repository.findById(id);
        } else {
            throw new CarNotFoundException ();
        }
        optionalCar.ifPresent(repository::delete);

    }
}
