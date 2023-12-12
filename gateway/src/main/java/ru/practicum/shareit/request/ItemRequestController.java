package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.request.dto.InputItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                              @RequestBody @Valid InputItemRequestDto dto) {
        if (dto.getId() != null) {
            throw new EntityValidationException("Проверьте корректность данных нового запроса!");
        }
        log.info("Gateway. Posting request with requestorId={}.", userId);
        return requestClient.postRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(USER_ID) @NotNull @Positive Long userId) {
        log.info("Gateway. Get requests with requestorId={}.", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                                        @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Gateway. Get requests with requestorId !={}.", userId);
        return requestClient.getUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                             @PathVariable @NotNull @Positive Long requestId) {
        log.info("Gateway. Get request with requestId={}.", userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
