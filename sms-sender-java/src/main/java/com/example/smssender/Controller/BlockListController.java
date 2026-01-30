package com.example.smssender.Controller;

import java.util.Set;

import javax.validation.constraints.Pattern;

import com.example.smssender.Service.BlockListService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms/block")
@Validated
public class BlockListController {

    private final BlockListService blockListService;

    public BlockListController(BlockListService blockListService) {
        this.blockListService = blockListService;
    }

    // Block a number
    @PostMapping("/{phoneNumber}")
    public ResponseEntity<String> blockNumber(
        @PathVariable 
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a 10 digit number")
        String phoneNumber) {

        blockListService.blockNumber(phoneNumber);
        return ResponseEntity.ok("Number blocked successfully");
    }

    // Unblock a number
    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<String> unblockNumber(
        @PathVariable 
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a 10 digit number")
        String phoneNumber) {
        blockListService.unblockNumber(phoneNumber);
        return ResponseEntity.ok("Number unblocked successfully");
    }

    // View all blocked numbers
    @GetMapping("/list")
    public ResponseEntity<Set<String>> getAllBlockedNumbers() {
        return ResponseEntity.ok(blockListService.getAllBlockedNumbers());
    }
}
