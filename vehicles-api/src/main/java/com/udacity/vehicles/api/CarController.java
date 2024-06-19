package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * REST controller for managing Car entities.
 */
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final CarResourceAssembler resourceAssembler;

    public CarController(CarService carService, CarResourceAssembler resourceAssembler) {
        this.carService = carService;
        this.resourceAssembler = resourceAssembler;
    }

    /**
     * Retrieves a list of all cars.
     *
     * @return a list of car resources
     */
    @GetMapping
    public ResponseEntity<Resources<Resource<Car>>> getAllCars() {
        List<Resource<Car>> cars = carService.list().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());
        Resources<Resource<Car>> resources = new Resources<>(cars,
                linkTo(methodOn(CarController.class).getAllCars()).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves the details of a specific car by ID.
     *
     * @param id the car ID
     * @return the car resource
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource<Car>> getCarById(@PathVariable Long id) {
        Car car = carService.findById(id);
        if (car != null) {
            Resource<Car> resource = resourceAssembler.toResource(car);
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Adds a new car to the system.
     *
     * @param car the new car entity
     * @return the created car resource
     * @throws URISyntaxException if URI creation fails
     */
    @PostMapping
    public ResponseEntity<Resource<Car>> createCar(@Valid @RequestBody Car car) throws URISyntaxException {
        Car savedCar = carService.save(car);
        Resource<Car> resource = resourceAssembler.toResource(savedCar);
        URI location = new URI(resource.getId().expand().getHref());
        return ResponseEntity.created(location).body(resource);
    }

    /**
     * Updates an existing car.
     *
     * @param id  the car ID
     * @param car the updated car entity
     * @return the updated car resource
     */
    @PutMapping("/{id}")
    public ResponseEntity<Resource<Car>> updateCar(@PathVariable Long id, @Valid @RequestBody Car car) {
        Car existingCar = carService.findById(id);
        if (existingCar != null) {
            car.setId(id);
            Car updatedCar = carService.save(car);
            Resource<Car> resource = resourceAssembler.toResource(updatedCar);
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a car by ID.
     *
     * @param id the car ID
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        Car car = carService.findById(id);
        if (car != null) {
            carService.delete(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
