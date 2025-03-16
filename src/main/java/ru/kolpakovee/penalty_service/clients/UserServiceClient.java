package ru.kolpakovee.penalty_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${integration.services.user-service.name}",
        url = "${integration.services.user-service.url}")
public interface UserServiceClient {

//    @GetMapping("/api/v1/apartments/by-user")
//    ApartmentInfo getApartmentByToken();
}
