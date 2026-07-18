package com.example.final_am_acn4a_debandi_juan.data.mappers;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.CastMemberDto;
import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TmdbCastMapper {
    public CastMember toModel(CastMemberDto dto) {
        return new CastMember(
            dto.getId(),
            dto.getName(),
            dto.getCharacter(),
            dto.getProfilePath()
        );
    }

    public List<CastMember> toModels(List<CastMemberDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        List<CastMember> cast = new ArrayList<>(dtos.size());
        for (CastMemberDto dto : dtos) {
            if (dto != null) {
                cast.add(toModel(dto));
            }
        }
        return cast;
    }
}
