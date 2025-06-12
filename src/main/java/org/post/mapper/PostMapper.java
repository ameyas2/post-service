package org.post.mapper;

import org.mapstruct.Mapper;
import org.post.dto.PostDTO;
import org.post.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    //@Mapping(target = "id", ignore = true)
    PostDTO toPostDTO(Post post);
    Post toPost(PostDTO postDTO);
}
