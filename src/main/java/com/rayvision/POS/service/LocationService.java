package com.rayvision.POS.service;

import com.rayvision.POS.domain.Location;
import com.rayvision.POS.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Get all locations
     */
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Get location by ID
     */
    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    /**
     * Create a new location
     */
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Update an existing location
     */
    public Location updateLocation(Long id, Location locationDetails) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        location.setName(locationDetails.getName());
        location.setCode(locationDetails.getCode());
        location.setAddress(locationDetails.getAddress());
        location.setCity(locationDetails.getCity());
        location.setState(locationDetails.getState());
        location.setZip(locationDetails.getZip());
        location.setPhone(locationDetails.getPhone());
        location.setCompanyId(locationDetails.getCompanyId());

        return locationRepository.save(location);
    }

    /**
     * Delete a location
     */
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        locationRepository.delete(location);
    }
}