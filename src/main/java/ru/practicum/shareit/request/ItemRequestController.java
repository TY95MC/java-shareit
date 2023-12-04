package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public InputItemRequestDto postRequest(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                           @RequestBody @Valid InputItemRequestDto dto) {
        return requestService.post(userId, dto);
    }

    @GetMapping
    public List<OutputItemRequestDto> getOwnRequests(@RequestHeader(USER_ID) @NotNull @Positive Long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<OutputItemRequestDto> getOtherUsersRequests(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                                            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        return requestService.getUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public OutputItemRequestDto getRequest(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                           @PathVariable @NotNull @Positive Long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}
