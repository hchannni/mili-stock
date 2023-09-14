package com.milistock.develop.controller.products;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.service.HeartService;
import com.milistock.develop.domain.Heart;

@RestController
@RequestMapping("/hearts")
public class HeartController {

    private final HeartService heartService;

    @Autowired
    public HeartController(HeartService heartService) {
        this.heartService = heartService;
    }

    @GetMapping
    public List<Heart> getAllHearts() {
        return heartService.getAllHearts();
    }

    @GetMapping("/{heartId}")
    public Optional<Heart> getHeartById(@PathVariable int heartId) {
        return heartService.getHeartById(heartId);
    }

    @GetMapping("/user/{userId}")
    public List<Heart> getAllHeartsByUserId(@PathVariable int userId) {
        return heartService.getAllHeartsByUserId(userId);
    }

    @PostMapping
    public Heart saveHeart(@RequestBody Heart heart) {
        return heartService.saveHeart(heart);
    }

    @DeleteMapping("/{heartId}")
    public void deleteHeart(@PathVariable int heartId) {
        heartService.deleteHeart(heartId);
    }

}
