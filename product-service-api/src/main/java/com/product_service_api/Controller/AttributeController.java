package com.product_service_api.Controller;

import com.product_service_api.DTO.*;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping("/colour")
    public ResponseEntity<?> addColour(@RequestBody ColourRequest colourRequest) {
        return new ResponseEntity<>(attributeService.addColour(colourRequest), HttpStatus.OK);
    }

    @GetMapping("/colours")
    public ResponseEntity<?> getAllColours() {
        return new ResponseEntity<>(attributeService.getAllColours(), HttpStatus.OK);
    }

    @PostMapping("/size-categories")
    public ResponseEntity<?> addSizeCategory(@RequestBody SizeCategoryRequest sizeCategoryRequest) {
        return new ResponseEntity<>(attributeService.addSizeCategory(sizeCategoryRequest), HttpStatus.OK);
    }

    @GetMapping("/size-categories")
    public ResponseEntity<?> getAllSizeCategories() {
        return new ResponseEntity<>(attributeService.getAllSizeCategories(), HttpStatus.OK);
    }

    @PostMapping("/size-options")
    public ResponseEntity<?> addSizeOption(@RequestBody SizeOptionRequest sizeOptionRequest) {
        return new ResponseEntity<>(attributeService.addSizeOption(sizeOptionRequest), HttpStatus.OK);
    }

    @GetMapping("/size-options")
    public ResponseEntity<?> getAllSizeOptions() {
        return new ResponseEntity<>(attributeService.getAllSizeOptions(), HttpStatus.OK);
    }

    @PostMapping("/attribute-type")
    public ResponseEntity<?> addAttributeType(@RequestBody AttributeTypeRequest attributeTypeRequest) {
        return new ResponseEntity<>(attributeService.addAttributeType(attributeTypeRequest), HttpStatus.OK);
    }

    @GetMapping("/attribute-types")
    public ResponseEntity<?> getAllAttributeTypes() {
        return new ResponseEntity<>(attributeService.getAllAttributeTypes(), HttpStatus.OK);
    }

    @PostMapping("/attribute-option")
    public ResponseEntity<?> addAttributeOption(@RequestBody AttributeOptionRequest attributeOptionRequest) {
        return new ResponseEntity<>(attributeService.addAttributeOption(attributeOptionRequest), HttpStatus.OK);
    }

    @GetMapping("/attribute-options")
    public ResponseEntity<?> getAllAttributeOptions() {
        return new ResponseEntity<>(attributeService.getAllAttributeOptions(), HttpStatus.OK);
    }
}
