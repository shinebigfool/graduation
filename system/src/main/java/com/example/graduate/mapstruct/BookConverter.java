package com.example.graduate.mapstruct;

import com.example.graduate.domain.BookDO;
import com.example.graduate.dto.BookDTO;
import com.example.graduate.pojo.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookConverter {
    BookConverter INSTANCE = Mappers.getMapper(BookConverter.class);
    @Mappings({})
    BookDTO domain2dto(Book book);
    List<BookDO> po2do(List<Book> books);
}
