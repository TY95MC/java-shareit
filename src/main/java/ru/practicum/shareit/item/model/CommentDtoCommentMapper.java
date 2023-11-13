package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentDtoCommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "author", source = "user")
    @Mapping(target = "author.id", source = "user.id")
    @Mapping(target = "author.name", source = "user.name")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "item.name", source = "item.name")
    @Mapping(target = "id", source = "commentDto.id")
    Comment commentDtoToComment(CommentDto commentDto, User user, Item item);

    List<CommentDto> commentsToCommentDtos(List<Comment> list);
}
